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

package stroom.pool.client.presenter;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.MyPresenterWidget;

import stroom.data.grid.client.DataGridView;
import stroom.data.grid.client.DataGridViewImpl;
import stroom.data.grid.client.EndColumn;
import stroom.dispatch.client.ClientDispatchAsync;
import stroom.pool.shared.FetchPoolRowAction;
import stroom.pool.shared.PoolRow;
import stroom.streamstore.client.presenter.ActionDataProvider;
import stroom.widget.tooltip.client.presenter.TooltipPresenter;
import stroom.widget.util.client.MySingleSelectionModel;

public class PoolListPresenter extends MyPresenterWidget<DataGridView<PoolRow>> {
    private final MySingleSelectionModel<PoolRow> selectionModel = new MySingleSelectionModel<PoolRow>();
    private final FetchPoolRowAction action = new FetchPoolRowAction();
    private ActionDataProvider<PoolRow> dataProvider;

    @Inject
    public PoolListPresenter(final EventBus eventBus, final ClientDispatchAsync dispatcher,
            final TooltipPresenter tooltipPresenter) {
        super(eventBus, new DataGridViewImpl<PoolRow>(true));
        getView().setSelectionModel(selectionModel);

        getView().addColumn(new Column<PoolRow, String>(new TextCell()) {
            @Override
            public String getValue(final PoolRow row) {
                return row.getPoolName();
            }
        }, "Name");

        getView().addEndColumn(new EndColumn<PoolRow>());

        dataProvider = new ActionDataProvider<PoolRow>(dispatcher, action);
        dataProvider.addDataDisplay(getView());
    }

    public MySingleSelectionModel<PoolRow> getSelectionModel() {
        return selectionModel;
    }
}
