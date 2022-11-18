import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class DateBase {
    private final Map<String, Table> dbs;
    private final List<Long> dbsLastModified;


    public DateBase() {
        dbs = new HashMap<>();
        dbsLastModified = new ArrayList<>();
    }

    private void loadDateBase(String path) {
        CSVReader reader;
        try {
            dbs.put(FilenameUtils.removeExtension(path), new TableParser(path).parse());
            dbsLastModified.add(new File(path).lastModified());
        } catch (FileNotFoundException ex) {
            System.out.printf("Con not open bd : %s", path);
        } catch (IOException ex) {
            System.out.printf("Con not read bd : %s", path);
        }
    }

    public void loadDateBases(String... dbFiles) {
        for (String path : dbFiles) {
            loadDateBase(path);
        }
    }

    static void recursiveSelect(List<String> relatedTables, Map<String, Table> activeTables, Table resTable,
                                Query query, Map<String, Pair<Object, Type>> args, int depth) throws ParseException {
        if (depth < activeTables.size()) {
            String tableName = relatedTables.get(depth);
            for (Row row : activeTables.get(tableName)) {
                List<Pair<String, Pair<Object, Type>>> fields = row.getFields();
                for (Pair<String, Pair<Object, Type>> field : fields) {
                    args.put("%s.%s".formatted(tableName, field.getLeft()), field.getRight());
                }
                recursiveSelect(relatedTables, activeTables, resTable, query, args, depth + 1);
            }

        } else {
            if (query.getWhere() == null || query.getWhere().execute(args)) {
                Map<String, Object> newRow = new HashMap<>();
                for (String field : resTable.getAllColumns()) {
                    newRow.put(field, args.get(field).getLeft());
                }
                resTable.addRow(newRow);
            }
        }
    }

    static List<Pair<String, String>> checkRelations(List<String> fields, Map<String, Table> activeTables)
            throws ParseException {
        List<Pair<String, String>> resFields = new ArrayList<>();
        for (String field : fields) {
            String tableName = "";
            if (field.contains(".")) {
                String[] selectParts = field.split("\\.");
                tableName = selectParts[0];
                field = selectParts[1];
                if (!activeTables.containsKey(tableName)) {
                    throw new ParseException("Not resolved table", 0);
                }
                if (!activeTables.get(tableName).hasColumn(field)) {
                    throw new ParseException("The table `%s` does not contain `%s` field".formatted(tableName, field), 0);
                }
                resFields.add(new Pair<>(tableName, field));
            } else {
                boolean find = false;
                for (Map.Entry<String, Table> t : activeTables.entrySet()) {
                    if (t.getValue().hasColumn(field)) {
                        if (!find) {
                            tableName = t.getKey();
                            find = true;
                        } else {
                            throw new ParseException("Ambiguous field matching", 0);
                        }
                    }
                }
                if (tableName.equals("")) {
                    throw new ParseException("Con not find field", 0);
                }
                resFields.add(new Pair<>(tableName, field));
            }

        }
        return resFields;
    }

    static void groupRows(Table resTable, List<Pair<String, String>> groupByRelations) throws ParseException {
        String[] resTableColumns = resTable.getAllColumns();

        Arrays.sort(resTableColumns);
        String[] groupByMergeRelations = (String[]) groupByRelations.stream()
                .map((item) -> "%s.%s".formatted(item.getLeft(), item.getRight())).toList().toArray(new String[0]);

        Arrays.sort(groupByMergeRelations);

        if (!Arrays.equals(groupByMergeRelations, resTableColumns)) {
            throw new ParseException("Fields in group by do not associated with select", 0);
        }

        Map<List<Object>, List<Row>> groups = new HashMap<>();
        for (String groupName : groupByMergeRelations) {
            for (Row row : resTable) {
                List<Object> curFields = new ArrayList<>();
                for (Pair<String, Pair<Object, Type>> field : row.getFields()) {
                    if (field.getLeft().equals(groupName)) {
                        curFields.add(field);
                    }
                }
                boolean find = false;
                for (List<Object> key : groups.keySet()) {
                    if (key.equals(curFields)) {
                        groups.get(key).add(row);
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    List<Row> groupRows = new ArrayList<>();
                    groupRows.add(row);
                    groups.put(curFields, groupRows);
                }
            }
        }
        // TODO Можно доделать функции
        resTable.clear();
        groups.values().forEach((item) -> {
            Map<String, Object> newRow = new HashMap<>();
            for (var field : groupByMergeRelations) {
                newRow.put(field, item.get(0).getField(field).getLeft());
            }
            resTable.addRow(newRow);
        });
    }

    public Table processQuery(String queryString) {
        try {
            Query query = new Query(queryString);

            Map<String, Table> activeTables = new HashMap<>();
            for (String table : query.getFrom()) {
                activeTables.put(table, dbs.get(table));
            }

            Table resTable;
            List<Pair<String, Type>> resTableColumns = new ArrayList<>();

            Pair<String, String>[] selectors = query.getSelect();
            if (selectors[0].getLeft().equals("*")) {
                if (selectors.length > 1) {
                    throw new ParseException("Too many * symbols", 0);
                } else {
                    for (Map.Entry<String, Table> t : activeTables.entrySet()) {
                        for (String column : t.getValue().getAllColumns()) {
                            resTableColumns.add(new Pair<>("%s.%s".formatted(t.getKey(), column), t.getValue().getColumnType(column)));
                        }
                    }
                }
            } else {
                List<Pair<String, String>> selectRelation = checkRelations(
                        Arrays.stream(query.getSelect()).map(Pair::getLeft).collect(Collectors.toList()), activeTables);
                selectRelation.forEach((item) -> {
                    resTableColumns.add(new Pair<>("%s.%s".formatted(item.getLeft(), item.getRight()),
                            activeTables.get(item.getLeft()).getColumnType(item.getRight())));
                });
            }

            List<String> relatedTables = null;
            if (query.getWhere() != null) {
                relatedTables = query.getWhere().resolveFields(activeTables);
                if (relatedTables.size() != activeTables.size()) {
                    throw new ParseException("Excess tables in FROM", 0);
                }
            } else {
                relatedTables = activeTables.keySet().stream().toList();
            }
            resTable = new Table(resTableColumns);


            recursiveSelect(relatedTables, activeTables, resTable, query, new HashMap<>(), 0);

            if (query.getGroupBy() != null) {
                List<Pair<String, String>> groupByRelation = checkRelations(Arrays.stream(query.getGroupBy()).toList(),
                        activeTables);
                groupRows(resTable, groupByRelation);
            }


            return resTable;
        } catch (ParseException ex) {
            System.out.printf("Incorrect query : %s\n", ex.getMessage());
            return null;
        }
    }
}
