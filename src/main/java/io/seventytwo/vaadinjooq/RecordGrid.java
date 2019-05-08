package io.seventytwo.vaadinjooq;

import com.vaadin.flow.component.grid.Grid;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecordGrid<R extends Record> extends Grid<R> {

    public RecordGrid(Class<R> clazz) {
        super(clazz);
    }

    public void setColumns(TableField<?, ?>... fields) {
        List<String> propertyNames = Arrays.stream(fields).map(field -> JooqUtil.getPropertyName(field)).collect(Collectors.toList());
        super.setColumns(propertyNames.toArray(new String[propertyNames.size()]));
    }
}
