package io.seventytwo.vaadinjooq.util;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.OrderField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VaadinJooqUtilTest {

    @Test
    void orderByEmailAsc() {
        QuerySortOrder sortOrder = new QuerySortOrder("EMAIL", SortDirection.ASCENDING);
        List<QuerySortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);

        List<OrderField<?>> orderFields = VaadinJooqUtil.orderFields(CUSTOMER, new Query(0, 50, sortOrders, null, null));

        assertEquals(1, orderFields.size());

        OrderField<?> orderField = orderFields.get(0);
        assertEquals("\"CUSTOMER\".\"EMAIL\"", orderField.toString());
    }

    @Test
    void orderByEmailDesc() {
        QuerySortOrder sortOrder = new QuerySortOrder("EMAIL", SortDirection.DESCENDING);
        List<QuerySortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);

        List<OrderField<?>> orderFields = VaadinJooqUtil.orderFields(CUSTOMER, new Query(0, 50, sortOrders, null, null));

        assertEquals(1, orderFields.size());

        OrderField<?> orderField = orderFields.get(0);
        assertEquals("\"CUSTOMER\".\"EMAIL\" desc", orderField.toString());
    }

    @Test
    void unknownField() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            QuerySortOrder sortOrder = new QuerySortOrder("NAME", SortDirection.ASCENDING);
            List<QuerySortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            VaadinJooqUtil.orderFields(CUSTOMER, new Query(0, 50, sortOrders, null, null));
        });
    }
}
