package io.seventytwo.vaadinjooq;

import com.google.common.base.CaseFormat;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Table;

import java.util.List;
import java.util.stream.Collectors;

public class JooqUtil {

    private JooqUtil() {
    }

    public static OrderField[] createOrderBy(Table table, List<QuerySortOrder> sortOrders) {
        List<OrderField<?>> list = sortOrders.stream().map(sortOrder -> {
            Field<?> field = table.field(getFieldName(sortOrder.getSorted()));
            if (sortOrder.getDirection() == SortDirection.ASCENDING) {
                return field.asc();
            } else {
                return field.desc();
            }
        }).collect(Collectors.toList());
        return list.toArray(new OrderField[list.size()]);
    }

    public static String getPropertyName(Field<?> field) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName());
    }

    private static String getFieldName(String propertyName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, propertyName);
    }

    public static Field<?> getField(Table table, String propertyName) {
        String fieldName = getFieldName(propertyName);
        return (Field<?>) table.field(fieldName);
    }
}
