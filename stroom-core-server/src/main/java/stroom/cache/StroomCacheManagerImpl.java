/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import stroom.cache.shared.CacheInfo;
import stroom.cache.shared.FindCacheInfoCriteria;
import stroom.entity.shared.BaseResultList;
import stroom.entity.shared.Clearable;
import stroom.entity.shared.PageRequest;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Statistics;

@Component
public class StroomCacheManagerImpl implements StroomCacheManager, Clearable {
    @Resource
    private CacheManager cacheManager;

    @Override
    public BaseResultList<CacheInfo> find(final FindCacheInfoCriteria criteria) throws RuntimeException {
        final PageRequest pageRequest = criteria.obtainPageRequest();

        List<CacheInfo> list = findCaches(criteria);

        // Trim the list to the specified range.
        if (pageRequest.getLength() != null && pageRequest.getLength() < list.size()) {
            int from = 0;
            int to = 0;
            if (pageRequest.getOffset() != null) {
                from = pageRequest.getOffset().intValue();
            }

            to = from + pageRequest.getLength();

            final List<CacheInfo> shortList = new ArrayList<>(pageRequest.getLength());
            for (int i = from; i < to; i++) {
                shortList.add(list.get(i));
            }
            list = shortList;
        }

        // Return a base result list.
        final int cacheCount = cacheManager.getCacheNames().length;
        final boolean more = pageRequest.getOffset() + list.size() < cacheCount;
        return new BaseResultList<>(list, pageRequest.getOffset(), Long.valueOf(cacheCount), more);
    }

    private List<CacheInfo> findCaches(final FindCacheInfoCriteria criteria) {
        final String[] cacheNames = cacheManager.getCacheNames();
        Arrays.sort(cacheNames);

        final List<CacheInfo> list = new ArrayList<>(cacheNames.length);
        for (final String cacheName : cacheNames) {
            boolean include = true;
            if (criteria != null && criteria.getName() != null) {
                include = criteria.getName().isMatch(cacheName);
            }

            if (include) {
                final Ehcache cache = cacheManager.getEhcache(cacheName);
                if (cache != null) {
                    final Statistics stats = cache.getStatistics();

                    if (stats != null) {
                        final CacheInfo info = new CacheInfo(stats.getAssociatedCacheName(), stats.getCacheHits(),
                                stats.getOnDiskHits(), stats.getOffHeapHits(), stats.getInMemoryHits(),
                                stats.getCacheMisses(), stats.getOnDiskMisses(), stats.getOffHeapMisses(),
                                stats.getInMemoryMisses(), stats.getObjectCount(), stats.getAverageGetTime(),
                                stats.getEvictionCount(), stats.getMemoryStoreObjectCount(),
                                stats.getOffHeapStoreObjectCount(), stats.getDiskStoreObjectCount(),
                                stats.getSearchesPerSecond(), stats.getAverageSearchTime(), stats.getWriterQueueSize());

                        list.add(info);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Removes all items from all caches.
     */
    @Override
    public void clear() {
        final String[] cacheNames = cacheManager.getCacheNames();
        for (final String cacheName : cacheNames) {
            final Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.removeAll();
            }
        }
    }

    /**
     * Clears all items from named cache.
     */
    @Override
    public Long findClear(final FindCacheInfoCriteria criteria) {
        final List<CacheInfo> caches = findCaches(criteria);
        for (final CacheInfo cache : caches) {
            cacheManager.getCache(cache.getName()).removeAll();
        }
        return null;
    }

    @Override
    public FindCacheInfoCriteria createCriteria() {
        return new FindCacheInfoCriteria();
    }
}
