package io.seventytwo.vaadinjooq.util;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Table;

import java.util.List;

/**
 * A utility class with some helper methods that uses the jOOQ generate metamodel
 */
public class JooqUtil {

    private JooqUtil() {
    }

    /**
     * Returns the property name base on a jOOQ {@link Field}
     * Converts upper with _ delimiter to camel case
     *
     * @param field The field
     * @return The property name
     */
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

    /**
     * Returns the {@link Field} base from a table based on a property name
     *
     * @param table        The table
     * @param propertyName The property name
     * @return The field
     */
    public static Field<?> getField(Table<?> table, String propertyName) {
        String fieldName = getFieldName(propertyName);
        return (Field<?>) table.field(fieldName);
    }

    /**
     * Converts a Vaadin {@link QuerySortOrder} to an array of jOOQ {@link OrderField}
     *
     * @param table      The table
     * @param sortOrders The sort orders
     * @return An array of order fields
     */
    public static OrderField<?>[] createOrderBy(Table<?> table, List<QuerySortOrder> sortOrders) {
        return sortOrders.stream().map(sortOrder -> {
            Field<?> field = table.field(getFieldName(sortOrder.getSorted()));
            return sortOrder.getDirection() == SortDirection.ASCENDING ? field.asc() : field.desc();
        }).toArray(OrderField[]::new);
    }

    /**
     * Converts camel case to upper case with _ as delimiter
     *
     * @param propertyName The name of the property
     * @return the name of the field
     */
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
