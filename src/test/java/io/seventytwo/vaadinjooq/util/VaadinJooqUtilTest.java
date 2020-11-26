package io.seventytwo.vaadinjooq.util;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.OrderField;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static org.junit.Assert.assertEquals;

public class VaadinJooqUtilTest {

    @Test
    public void orderByEmailAsc() {
        QuerySortOrder sortOrder = new QuerySortOrder("EMAIL", SortDirection.ASCENDING);
        List<QuerySortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);

        List<OrderField<?>> orderFields = VaadinJooqUtil.orderFields(CUSTOMER, new Query(0, 50, sortOrders, null, null));

        assertEquals(1, orderFields.size());

        OrderField<?> orderField = orderFields.get(0);
        assertEquals("\"CUSTOMER\".\"EMAIL\"", orderField.toString());
    }

    @Test
    public void orderByEmailDesc() {
        QuerySortOrder sortOrder = new QuerySortOrder("EMAIL", SortDirection.DESCENDING);
        List<QuerySortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);

        List<OrderField<?>> orderFields = VaadinJooqUtil.orderFields(CUSTOMER, new Query(0, 50, sortOrders, null, null));

        assertEquals(1, orderFields.size());

        OrderField<?> orderField = orderFields.get(0);
        assertEquals("\"CUSTOMER\".\"EMAIL\" desc", orderField.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownField() {
        QuerySortOrder sortOrder = new QuerySortOrder("NAME", SortDirection.ASCENDING);
        List<QuerySortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);

        VaadinJooqUtil.orderFields(CUSTOMER, new Query(0, 50, sortOrders, null, null));
    }
}
