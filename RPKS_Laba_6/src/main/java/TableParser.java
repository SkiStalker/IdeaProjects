import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TableParser {
    final private String path;
    private CSVReader reader;

    public TableParser(String path) throws FileNotFoundException {
        this.path = path;
        this.reader = new CSVReader(new FileReader(path), ';');
    }

    private List<Pair<String, Type>> createTableColumns(String[] types, String[] names) throws IOException {
        List<Pair<String, Type>> columns = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case "int": {
                    columns.add(new Pair<>(names[i], Integer.class));
                    break;
                }
                case "string": {
                    columns.add(new Pair<>(names[i], String.class));
                    break;
                }
                case "date" : {
                    columns.add(new Pair<>(names[i], Date.class));
                    break;
                }
                default: {
                    columns.add(new Pair<>(names[i], Object.class));
                    break;
                }
            }
        }
        return columns;
    }

    static private Object convert(String obj, Type type) {
        if (type.equals(Integer.class)) {
            return Integer.parseInt(obj);
        } else if(type.equals(Date.class)) {
            try {
                return new SimpleDateFormat("yyyy.MM.dd").parse(obj);
            }
            catch (ParseException ex) {
                throw new ClassCastException();
            }
        }
        else if (type.equals(String.class)) {
            return obj;
        } else {
            throw new ClassCastException();
        }
    }

    private Map<String, Object> createTableRow(String[] row, List<Pair<String, Type>> tableColumns) {
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < row.length; i++) {

            fields.put(tableColumns.get(i).getLeft(), convert(row[i], tableColumns.get(i).getRight()));
        }
        return fields;
    }

    static private List<Pair<String, Type>> pairListToMap(List<Pair<String, Type>> list) {
        List<Pair<String, Type>> res = new ArrayList<>();
        for (Pair<String, Type> item : list) {
            res.add(new Pair<>(item.getLeft(), item.getRight()));
        }
        return res;
    }

    public Table parse() throws IOException {
        List<String[]> lines = reader.readAll();
        List<Pair<String, Type>> tableColumns = createTableColumns(lines.get(0), lines.get(1));

        Table curTable = new Table(pairListToMap(tableColumns));

        for (int i = 2; i < lines.size(); i++) {
            curTable.addRow(createTableRow(lines.get(i), tableColumns));
        }
        return curTable;
    }
}
