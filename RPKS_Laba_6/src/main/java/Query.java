import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Query {

    public Pair<String, String>[] getSelect() {
        return select;
    }

    public String[] getFrom() {
        return from;
    }

    public WHERECondition getWhere() {
        return where;
    }

    public String[] getGroupBy() {
        return groupBy;
    }

    private Pair<String,String>[] select;
    private String[] from;
    private WHERECondition where;
    private String[] groupBy;

    static private String[] parseOperation(String str, String operationName,
                                           Boolean optional, Boolean begin) throws ParseException {
        str = str.strip();
        String[] parts = str.split(operationName);
        if (parts.length == 1) {
            if (!optional) {
                throw new ParseException("%s is not exists".formatted(operationName), 0);
            } else {
                return null;
            }
        } else if (parts.length > 2) {
            throw new ParseException("To many %s operations".formatted(operationName), 0);
        } else if (parts[0].strip().equals("") && !begin) {
            throw new ParseException("Unknown operation before %s".formatted(operationName), 0);
        } else if (parts[1].strip().equals("")) {
            throw new ParseException("Empty operation after %s".formatted(operationName), 0);
        }
        return parts;
    }

    static private String[] parseListOperationArgs(String str) throws ParseException {
        String[] operationParts = str.split(",");
        List<String> res = new ArrayList<>();
        if (operationParts[0].equals("") || operationParts[operationParts.length - 1].equals("")) {
            throw new ParseException("Unexpected symbol ','", 0);
        }
        for (String arg : operationParts) {
            arg = arg.strip();
            if (arg.contains(" ")) {
                throw new ParseException("Unexpected symbol ' '", 0);
            } else {
                res.add(arg);
            }
        }
        return res.toArray(new String[0]);
    }

    static private WHERECondition parseWhereOperation(String str) throws ParseException {
        return new WHERECondition(str);
    }

    static private Pair<String, String>[] parseListOperationArgsWithFunctions(String str) throws ParseException {
        String[] operationParts = str.split(",");
        List<Pair<String, String>> res = new ArrayList<>();
        if (operationParts[0].equals("") || operationParts[operationParts.length - 1].equals("")) {
            throw new ParseException("Unexpected symbol ','", 0);
        }
        for (String arg : operationParts) {
            String select = null;
            String func = null;
            String[] selectParts = arg.split("\\(");
            if (selectParts.length == 1) {
                select = arg.strip();
            } else {
                func = selectParts[0].strip().strip();
                select = selectParts[1].substring(0, selectParts.length - 1).strip();
            }
            if (select.contains(" ")) {
                throw new ParseException("Unexpected space", 0);
            }
            if (func != null && func.contains(" ")) {
                throw new ParseException("Unexpected space", 0);
            }
            res.add(new Pair<>(select, func));
        }
        return res.toArray(new Pair[0]);
    }

    public Query(String query) throws ParseException {

        String[] selectParts = parseOperation(query, "SELECT", false, true);

        String[] fromParts = parseOperation(selectParts[1], "FROM", false, false);

        select = parseListOperationArgsWithFunctions(fromParts[0]);

        String[] whereParts = parseOperation(fromParts[1], "WHERE", true, false);
        String[] groupByParts;
        if (whereParts != null) {
            from = parseListOperationArgs(whereParts[0]);
            groupByParts = parseOperation(whereParts[1], "GROUPBY", true, false);
        } else {
            groupByParts = parseOperation(fromParts[1], "GROUPBY", true, false);
        }
        if (groupByParts != null) {
            if (whereParts != null) {
                where = parseWhereOperation(groupByParts[0]);
            } else {
                from = parseListOperationArgs(groupByParts[0]);
            }
            groupBy = parseListOperationArgs(groupByParts[1]);
        } else if (whereParts != null) {
            where = parseWhereOperation(whereParts[1]);
        } else {
            from = parseListOperationArgs(fromParts[1]);
        }

    }
}
