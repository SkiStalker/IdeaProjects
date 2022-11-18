import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Table implements Iterable<Row> {
    private final List<Row> rows;
    final private Map<String, Type> columns;
    final private List<Pair<String, Type>> orderedColumns;

    public Table(List<Pair<String, Type>> columns) {
        this.rows = new ArrayList<>();
        this.orderedColumns = columns;
        this.columns = new HashMap<>();
        for (Pair<String, Type> column : this.orderedColumns) {
            this.columns.put(column.getLeft(), column.getRight());
        }
    }


    public String[] getAllColumns() {
        return orderedColumns.stream().map(Pair::getLeft).toList().toArray(new String[0]);
    }

    public Boolean hasColumn(String column) {
        return columns.containsKey(column);
    }

    public Type getColumnType(String column) {
        return columns.get(column);
    }

    public void addRow(Map<String, Object> fields) {
        rows.add(new Row(this.orderedColumns, this.columns, fields));
    }

    public void deleteRow(Integer number) {
        rows.remove(rows.get(number));
    }

    public void clear() {
        rows.clear();
    }

    public void updateRow(Integer number, Map<String, Object> fields) {
        rows.get(number).updateRow(fields);
    }

    public final Row getRow(Integer number) {
        return rows.get(number);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(String.join(" ", getAllColumns()));
        res.append("\n");
        for (Row row : this) {
            res.append(row.toString());
            res.append("\n");
        }
        return res.toString();
    }

    @Override
    public Iterator<Row> iterator() {
        return new Iterator<Row>() {
            private Integer pos = 0;

            @Override
            public boolean hasNext() {
                return pos < rows.size();
            }

            @Override
            public Row next() {
                return rows.get(pos++);
            }
        };
    }
}
