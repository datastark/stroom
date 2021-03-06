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

package stroom.pipeline.server.errorhandler;

import java.util.ArrayList;
import java.util.List;

import stroom.util.logging.StroomLogger;
import stroom.util.shared.Location;
import stroom.util.shared.Severity;
import stroom.util.shared.StoredError;

public class StoredErrorReceiver implements ErrorReceiver {
    private static final StroomLogger LOGGER = StroomLogger.getLogger(StoredErrorReceiver.class);

    private long totalErrors;
    private List<StoredError> list = new ArrayList<>();

    @Override
    public void log(final Severity severity, final Location location, final String elementId, final String message,
            final Throwable e) {
        final String msg = MessageUtil.getMessage(message, e);

        totalErrors++;
        list.add(new StoredError(severity, location, elementId, msg));

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(msg, e);
        }
    }

    public void replay(final ErrorReceiver errorReceiver) {
        if (errorReceiver != null) {
            for (final StoredError se : list) {
                errorReceiver.log(se.getSeverity(), se.getLocation(), se.getElementId(), se.getMessage(), null);
            }
        }
    }

    public long getTotalErrors() {
        return totalErrors;
    }
}
