package io.seventytwo.vaadinjooq.util;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;

public class VaadinJooqUtil {

    private VaadinJooqUtil() {
    }

    /**
     * Converts {@link QuerySortOrder} to {@link OrderField}
     *
     * @param table The table the OrderFields should use
     * @param query The {@link Query}
     * @return a {@link List} of {@link OrderField}
     */
    public static List<OrderField<?>> orderFields(Table<?> table, Query<?, ?> query) {
        List<OrderField<?>> orderFields = new ArrayList<>();
        for (QuerySortOrder sortOrder : query.getSortOrders()) {
            Field<?> field = table.field(sortOrder.getSorted());
            orderFields.add(sortOrder.getDirection().equals(SortDirection.DESCENDING) ? field.desc() : field);
        }
        return orderFields;
    }

}
