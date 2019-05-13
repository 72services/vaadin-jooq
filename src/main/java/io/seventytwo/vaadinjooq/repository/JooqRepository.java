package io.seventytwo.vaadinjooq.repository;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

public class JooqRepository {

    private final DSLContext dslContext;

    public JooqRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public <T extends Record> List<T> findAll(Table<T> table, Condition condition, Map<Field<?>, Boolean> orderBy, int offset, int limit) {
        SelectConditionStep<T> where;
        if (condition == null) {
            where = dslContext.selectFrom(table)
                    .where(DSL.noCondition());
        } else {
            where = dslContext.selectFrom(table)
                    .where(condition);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            return createOrderBy(table, where, orderBy)
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        } else {
            return where
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        }
    }

    public <T extends Record> int count(Table<T> table, Condition condition) {
        if (condition == null) {
            return dslContext.fetchCount(dslContext.selectFrom(table));
        } else {
            return dslContext.fetchCount(dslContext.selectFrom(table).where(condition));
        }
    }

    private <T extends Record> SelectSeekStepN<T> createOrderBy(Table<T> table, SelectConditionStep<T> where, Map<Field<?>, Boolean> orderColumns) {
        List<OrderField<?>> orderFields = new ArrayList<>();
        orderColumns.forEach((key, value) -> {
            List<String> qualifiers = new ArrayList<>();
            qualifiers.add(table.getSchema().getName());
            qualifiers.addAll(Arrays.asList(table.getQualifiedName().getName()));
            qualifiers.add(key.getName());

            Name column = name(qualifiers);
            Field<Object> field = field(column);

            orderFields.add(value ? field.asc() : field.desc());
        });
        return where.orderBy(orderFields);
    }

}
