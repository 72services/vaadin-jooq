package io.seventytwo.vaadinjooq.repository;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.text.MessageFormat.format;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

public class JooqRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JooqRepository.class);

    private DSLContext dsl;

    public JooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public <T extends Record> T findOne(Table<T> table, Condition condition) {
        return (T) dsl
                .selectFrom(table)
                .where(condition)
                .fetchOne();
    }

    public <T extends Record> List<T> findByExample(Table<T> table, Condition condition, Map<Field<?>, Boolean> orderBy, int offset, int limit) {
        SelectConditionStep<T> where;
        if (condition == null) {
            where = dsl.selectFrom(table)
                    .where(DSL.noCondition());
        } else {
            where = dsl.selectFrom(table)
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

    public List<Record> findByExample(Table table, Condition condition, Map<Field<?>, Boolean> orderBy, int offset, int limit, Field<?>... fields) {
        SelectConditionStep<Record> where;
        if (condition == null) {
            where = dsl.select(fields)
                    .from(table)
                    .where();
        } else {
            where = dsl.select(fields)
                    .from(table)
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
            return dsl.fetchCount(dsl.selectFrom(table));
        } else {
            return dsl.fetchCount(dsl.selectFrom(table).where(condition));
        }
    }

    public <T extends Record> T findByRecordId(Table<T> table, Record record) {
        Map<Field, Object> keys = getPrimaryKeyValues(table, record);
        return findById(table, keys);
    }

    public <T extends Record> T findById(Table<T> table, Object key) {
        UniqueKey primaryKey = table.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalArgumentException(
                    format("Tabelle {0} hat kein(e) Primärschlüssespalte(en). findById kann nicht benutzt werden!",
                            table.getName()));
        } else {
            if (primaryKey.getFields().size() > 1) {
                throw new IllegalArgumentException(
                        format("Tabelle {0} hat {1} Primärschlüsselfelder. findById mit einem Schlüssel kann nicht benutzt werden!",
                                table.getName(), primaryKey.getFields().size(), 1));
            } else {
                TableField tableField = (TableField) primaryKey.getFields().get(0);
                return dsl.selectFrom(table).where(tableField.eq(key)).fetchOne();
            }
        }
    }

    public <T extends Record> T findById(Table<T> table, Map<Field<?>, Object> keys) {
        UniqueKey primaryKey = table.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalArgumentException(
                    format("Tabelle {0} hat kein(e) Primärschlüssespalte(en). findById kann nicht benutzt werden!",
                            table.getName()));
        } else {
            if (primaryKey.getFields().size() != keys.size()) {
                throw new IllegalArgumentException(
                        format("Tabelle {0} hat {1} Primärschlüsselspalte(en). Es wurden aber {2} Wert(e) übergeben!",
                                table.getName(), primaryKey.getFields().size(), keys.size()));
            } else {
                SelectConditionStep<T> where = dsl.selectFrom(table).where();
                for (Object field : primaryKey.getFields()) {
                    TableField tableField = (TableField) field;
                    Object key = keys.get(tableField);
                    where = where.and(tableField.eq(key));
                }
                return where.fetchOne();
            }
        }
    }

    public int store(UpdatableRecord updatableRecord) {
        dsl.attach(updatableRecord);

        return updatableRecord.store();
    }

    public void delete(UpdatableRecord updatableRecord) {
        dsl.attach(updatableRecord);

        updatableRecord.delete();
    }

    public Configuration configuration() {
        return dsl.configuration();
    }

    public Result<Record> fetch(String sql, Object... parameters) {
        return dsl.fetch(sql, parameters);
    }

    public DSLContext dsl() {
        return dsl;
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

    private <T extends Record> Map<Field, Object> getPrimaryKeyValues(Table<T> table, Record record) {
        Map<Field, Object> keys = new HashMap<>();
        UniqueKey<?> primaryKey = table.getPrimaryKey();
        for (Field<?> field : primaryKey.getFields()) {
            keys.put(field, record.get(field));
        }
        return keys;
    }


}
