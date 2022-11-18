import java.lang.reflect.Type;
import java.util.*;

public class Row {
    private final Map<String, Type> columns;
    final private List<Pair<String, Type>> orderedColumns;
    private final Map<String, Object> fields;

    public Row(List<Pair<String, Type>> orderedColumns, Map<String, Type> columns, Map<String, Object> fields) {
        this.columns = columns;
        this.fields = new HashMap<>();
        this.orderedColumns = orderedColumns;

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (!entry.getValue().getClass().equals(this.columns.get(entry.getKey()))) {
                throw new ClassCastException();
            } else {
                this.fields.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void updateRow(Map<String, Object> fields) throws NullPointerException {
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            Object rowField = this.fields.get(field.getKey());
            if (rowField.getClass() != field.getValue().getClass()) {
                throw new ClassCastException();
            } else {
                this.fields.put(field.getKey(), field.getValue());
            }
        }
    }

    public Pair<Object, Type> getField(String field) {
        return new Pair<>(this.fields.get(field), this.columns.get(field));
    }


    public List<Pair<String, Pair<Object, Type>>> getFields() {
        List<Pair<String, Pair<Object, Type>>> res = new ArrayList<>();
        for (var entry : this.fields.entrySet()) {
            res.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(),
                    this.columns.get(entry.getKey()))));
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Pair<String, Type> column : this.orderedColumns) {
            stringBuilder.append("%s ".formatted(this.fields.get(column.getLeft())));
        }
        return stringBuilder.toString();
    }

}
