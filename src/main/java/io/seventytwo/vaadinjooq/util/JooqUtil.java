package io.seventytwo.vaadinjooq.util;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Table;

import java.util.List;

public class JooqUtil {

    private JooqUtil() {
    }

    public static String getPropertyName(Field<?> field) {
        String fieldName = field.getName();
        char[] chars = fieldName.toLowerCase().toCharArray();
        boolean wasUnderScore = false;
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c == '_') {
                wasUnderScore = true;
            } else {
                if (wasUnderScore) {
                    sb.append(Character.valueOf(c).toString().toUpperCase());
                } else {
                    sb.append(c);
                }
                wasUnderScore = false;
            }
        }
        return sb.toString();
    }

    public static Field<?> getField(Table table, String propertyName) {
        String fieldName = getFieldName(propertyName);
        return (Field<?>) table.field(fieldName);
    }

    public static OrderField[] createOrderBy(Table table, List<QuerySortOrder> sortOrders) {
        return sortOrders.stream().map(sortOrder -> {
            Field<?> field = table.field(getFieldName(sortOrder.getSorted()));
            return sortOrder.getDirection() == SortDirection.ASCENDING ? field.asc() : field.desc();
        }).toArray(OrderField[]::new);
    }

    private static String getFieldName(String propertyName) {
        StringBuilder sb = new StringBuilder();
        for (char c : propertyName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toUpperCase();
    }

}
