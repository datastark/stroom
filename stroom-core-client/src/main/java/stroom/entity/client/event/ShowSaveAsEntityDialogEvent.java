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

package stroom.entity.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

import stroom.entity.client.EntityTabData;

public class ShowSaveAsEntityDialogEvent extends GwtEvent<ShowSaveAsEntityDialogEvent.Handler> {
    public interface Handler extends EventHandler {
        void onSaveAs(final ShowSaveAsEntityDialogEvent event);
    }

    private static Type<Handler> TYPE;

    private final EntityTabData tabData;

    private ShowSaveAsEntityDialogEvent(final EntityTabData tabData) {
        this.tabData = tabData;
    }

    public static void fire(final HasHandlers handlers, final EntityTabData tabData) {
        handlers.fireEvent(new ShowSaveAsEntityDialogEvent(tabData));
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    @Override
    public final Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onSaveAs(this);
    }

    public EntityTabData getTabData() {
        return tabData;
    }
}
