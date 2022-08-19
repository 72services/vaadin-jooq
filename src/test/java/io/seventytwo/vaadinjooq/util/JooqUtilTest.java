package io.seventytwo.vaadinjooq.util;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Field;
import org.jooq.OrderField;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JooqUtilTest {

    @Test
    void getPropertyName() {
        String propertyName = JooqUtil.getPropertyName(CUSTOMER.FIRST_NAME);

        assertEquals("firstName", propertyName);
    }

    @Test
    void getField() {
        Field<?> firstName = JooqUtil.getField(CUSTOMER, "firstName");

        assertEquals("FIRST_NAME", firstName.getName());
    }

    @Test
    void createOrderBy() {
        QuerySortOrder sortOrder = new QuerySortOrder("firstName", SortDirection.ASCENDING);
        OrderField<?>[] orderBy = JooqUtil.createOrderBy(CUSTOMER, Collections.singletonList(sortOrder));

        Arrays.stream(orderBy).forEach(orderField -> assertEquals("\"CUSTOMER\".\"FIRST_NAME\" asc", orderField.toString()));
    }

}
