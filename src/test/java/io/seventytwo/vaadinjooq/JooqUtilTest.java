package io.seventytwo.vaadinjooq;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import io.seventytwo.vaadinjooq.util.JooqUtil;
import org.jooq.Field;
import org.jooq.OrderField;
import org.junit.Test;

import java.util.Arrays;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static org.junit.Assert.assertEquals;

public class JooqUtilTest {

    @Test
    public void getPropertyName() {
        String propertyName = JooqUtil.getPropertyName(CUSTOMER.FIRST_NAME);

        assertEquals("firstName", propertyName);
    }

    @Test
    public void getField() {
        Field<?> firstName = JooqUtil.getField(CUSTOMER, "firstName");

        assertEquals("FIRST_NAME", firstName.getName());
    }

    @Test
    public void createOrderBy() {
        QuerySortOrder sortOrder = new QuerySortOrder("firstName", SortDirection.ASCENDING);
        OrderField[] orderBy = JooqUtil.createOrderBy(CUSTOMER, Arrays.asList(sortOrder));

        Arrays.stream(orderBy).forEach(orderField -> {
            assertEquals("\"CUSTOMER\".\"FIRST_NAME\" asc", orderField.toString());
        });
    }

}