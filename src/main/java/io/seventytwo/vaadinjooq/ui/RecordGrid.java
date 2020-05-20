package io.seventytwo.vaadinjooq.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.provider.*;
import io.seventytwo.vaadinjooq.repository.JooqRepository;
import io.seventytwo.vaadinjooq.util.JooqUtil;
import org.jooq.*;
import org.jooq.Record;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecordGrid<R extends Record> extends Grid<R> {

    private ConfigurableFilterDataProvider<R, Void, Condition> filterDataProvider;

    private RecordGrid(Class<R> clazz) {
        super(clazz);
    }

    private void setColumns(TableField<?, ?>... columns) {
        List<String> propertyNames = Arrays.stream(columns).map(field -> JooqUtil.getPropertyName(field)).collect(Collectors.toList());
        super.setColumns(propertyNames.toArray(new String[propertyNames.size()]));
    }

    public void setFilterDataProvider(ConfigurableFilterDataProvider<R, Void, Condition> filterDataProvider) {
        this.filterDataProvider = filterDataProvider;
        super.setDataProvider(filterDataProvider);
    }

    public void filter(Condition condition) {
        filterDataProvider.setFilter(condition);
        filterDataProvider.refreshAll();
    }

    public void refresh() {
        filterDataProvider.refreshAll();
    }

    public static class Builder<R extends Record> {

        private final JooqRepository repository;
        private RecordGrid<R> grid;
        private Table<R> table;
        private TableField<?, ?>[] columns;
        private Condition condition;
        private Map<Field<?>, SortDirection> sort;

        public Builder(Table<R> table, DSLContext dslContext) {
            this.table = table;
            repository = new JooqRepository(dslContext);
        }

        public Builder<R> withColumns(TableField<?, ?>... columns) {
            this.columns = columns;

            return this;
        }

        public Builder<R> withCondition(Condition condition) {
            this.condition = condition;

            return this;
        }

        public Builder<R> withSort(Map<Field<?>, SortDirection> sort) {
            this.sort = sort;

            return this;
        }

        public RecordGrid<R> build() {
            grid = new RecordGrid<>((Class<R>) table.getRecordType());

            if (columns != null && columns.length > 0) {
                grid.setColumns(columns);
            }

            grid.setFilterDataProvider(createDataProvider().withConfigurableFilter());

            return grid;
        }

        private CallbackDataProvider<R, Condition> createDataProvider() {
            return DataProvider.fromFilteringCallbacks(
                    query -> {
                        List<R> rows = repository.findAll(table, createFilter(query), createOrderBy(query), query.getOffset(), query.getLimit());
                        if (!rows.isEmpty() && grid.getSelectedItems().isEmpty()) {
                            grid.select((R) rows.get(0));
                        }
                        return rows.stream();
                    },
                    query -> repository.count(table, createFilter(query))
            );
        }

        private Condition createFilter(com.vaadin.flow.data.provider.Query<R, Condition> query) {
            Condition filter;
            if (query.getFilter().isPresent()) {
                if (condition == null) {
                    filter = query.getFilter().get();
                } else {
                    filter = condition.and(query.getFilter().get());
                }
            } else {
                filter = condition;
            }
            return filter;
        }

        private Map<Field<?>, SortDirection> createOrderBy(com.vaadin.flow.data.provider.Query<R, Condition> query) {
            Map<Field<?>, SortDirection> orderBy = new HashMap<>();
            for (SortOrder<String> sortOrder : query.getSortOrders()) {
                orderBy.put(table.field(sortOrder.getSorted()), sortOrder.getDirection());
            }
            if (orderBy.isEmpty()) {
                orderBy = sort;
            }
            return orderBy;
        }
    }
}
