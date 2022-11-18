import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WHERECondition {
    private List<Pair<String, Boolean>> operations;

    public boolean isEmpty() {
        return operations.isEmpty();
    }

    public List<String> resolveFields(Map<String, Table> tables) throws ParseException {
        List<String> relatedTables = new ArrayList<>();
        for (Pair<String, Boolean> item : operations) {
            if (!item.getLeft().equals("NULL")) {
                if (!item.getRight()) {
                    if (item.getLeft().contains(".")) {
                        String[] itemParts = item.getLeft().split("\\.");
                        boolean find = false;
                        for (Map.Entry<String, Table> t : tables.entrySet()) {
                            if (t.getValue().hasColumn(itemParts[1])) {
                                if (!relatedTables.contains(t.getKey())) {
                                    relatedTables.add(t.getKey());
                                }
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            throw new ParseException("Can not resolve field `%s` in WHERE".formatted(itemParts[0]), 0);
                        }
                    } else {
                        if (!Character.isDigit(item.getLeft().charAt(0)) && item.getLeft().charAt(0) != '\'') {
                            boolean find = false;
                            String tableName = "";
                            for (Map.Entry<String, Table> t : tables.entrySet()) {
                                if (t.getValue().hasColumn(item.getLeft())) {
                                    if (!find) {
                                        tableName = t.getKey();
                                        find = true;
                                    } else {
                                        throw new ParseException("Ambiguous field `%s` matching".formatted(item.getLeft()), 0);
                                    }
                                }
                            }
                            if (tableName.equals("")) {
                                throw new ParseException("Con not find `%s` field".formatted(item.getLeft()), 0);
                            }
                            if (!relatedTables.contains(tableName)) {
                                relatedTables.add(tableName);
                            }
                            item.setLeft("%s.%s".formatted(tableName, item.getLeft()));
                        }
                    }
                }
            }
        }
        return relatedTables;
    }

    static Pair<String, Boolean> getItem(IntRef i, String str) {
        String buf = "";
        List<Character> ops = Arrays.asList('>', '<', '=', '!', '(', ')');
        boolean find = false;
        boolean string = false;
        boolean operation = false;
        while (!find && i.value < str.length()) {
            char t = str.charAt(i.value);
            if (Character.isSpaceChar(t) && buf.length() != 0) {
                break;
            } else if (Character.isSpaceChar(t) && buf.length() == 0) {
                i.value++;
            } else if (t == '\'' && !string) {
                buf += t;
                string = true;
                i.value++;
            } else if (t == '\'' && string) {
                buf += t;
                string = false;
                i.value++;
                find = true;
            } else if (ops.contains(t) && !operation) {
                buf += t;
                i.value++;
                operation = true;
            } else {
                buf += t;
                i.value++;
                switch (buf) {
                    case "AND", "OR", ">", ">=", "<", "<=", "=", "!=", "NOT", "IS", "(", ")" -> {
                        operation = true;
                        find = true;
                        break;
                    }
                }
            }

        }
        return new Pair<>(buf, operation);
    }

    static Integer getPriority(String op) {
        switch (op) {
            case "(", ")" -> {
                return 5;
            }
            default -> {
                return 4;
            }
            case "NOT" -> {
                return 3;
            }
            case "AND" -> {
                return 2;
            }
            case "OR" -> {
                return 1;
            }
        }
    }

    public WHERECondition(String str) throws ParseException {
        str = str.strip();
        operations = new ArrayList<>();

        Stack<Pair<String, Boolean>> stack = new Stack<>();
        IntRef i = new IntRef();
        i.value = 0;
        while (i.value < str.length()) {
            Pair<String, Boolean> temp = getItem(i, str);
            if (temp.getRight()) {
                if (temp.getLeft().equals("(")) {
                    stack.push(temp);
                } else if (temp.getLeft().equals(")")) {
                    Pair<String, Boolean> tempOp;
                    while (!(tempOp = stack.pop()).getLeft().equals("(")) {
                        operations.add(tempOp);
                    }
                } else {
                    if (stack.isEmpty() || stack.peek().getLeft().equals("(")) {
                        stack.push(temp);
                    } else {
                        int prior = getPriority(temp.getLeft());
                        if (!stack.isEmpty() && temp.getLeft().equals("NOT") && stack.peek().getRight() && stack.peek().getLeft().equals("IS")) {
                            Pair<String, Boolean> tmpIs = stack.pop();
                            stack.push(temp);
                            stack.push(tmpIs);
                        } else {
                            if (prior > getPriority(stack.peek().getLeft())) {
                                stack.push(temp);
                            } else {
                                while (!stack.isEmpty() && (getPriority(stack.peek().getLeft()) >= prior || stack.peek().getLeft().equals("("))) {
                                    operations.add(stack.pop());
                                }
                                stack.push(temp);
                            }
                        }
                    }
                }
            } else {
                operations.add(temp);
            }
        }
        while (!stack.isEmpty()) {
            operations.add(stack.pop());
        }
    }

    static Boolean compareInts(Integer left, Integer right, String op) {
        switch (op) {
            case "<" -> {
                return left < right;
            }
            case ">" -> {
                return left > right;
            }
            case "<=" -> {
                return left <= right;
            }
            case ">=" -> {
                return left >= right;
            }
            case "=" -> {
                return Objects.equals(left, right);
            }
            case "!=" -> {
                return !Objects.equals(left, right);
            }
            default -> {
                return false;
            }
        }
    }

    static Boolean compareStrings(String left, String right, String op) {
        int res = left.compareTo(right);
        switch (op) {
            case "<" -> {
                return res < 0;
            }
            case ">" -> {
                return res > 0;
            }
            case "<=" -> {
                return res <= 0;
            }
            case ">=" -> {
                return res >= 0;
            }
            case "=" -> {
                return res == 0;
            }
            case "!=" -> {
                return res != 0;
            }
            default -> {
                return false;
            }
        }
    }

    static Boolean compareDates(Date left, Date right, String op) {

        switch (op) {
            case "<" -> {
                return left.getTime() < right.getTime();
            }
            case ">" -> {
                return left.getTime() > right.getTime();
            }
            case "<=" -> {
                return left.getTime() <= right.getTime();
            }
            case ">=" -> {
                return left.getTime() >= right.getTime();
            }
            case "=" -> {
                return left.getTime() == right.getTime();
            }
            case "!=" -> {
                return left.getTime() != right.getTime();
            }
            default -> {
                return false;
            }
        }
    }

    static Boolean compareObjects(String left, String
            right, String op, Map<String, Pair<Object, Type>> args) throws ParseException {
        Boolean res = true;

        Boolean hasLeftField = args.containsKey(left);
        Boolean hasRightField = args.containsKey(right);

        if (hasLeftField && hasRightField) {
            if (args.get(left).getRight().equals(Integer.class)) {
                res = compareInts(
                        (Integer) args.get(left).getLeft(), (Integer) args.get(right).getLeft(), op);

            } else if (args.get(left).getRight().equals(String.class)) {
                res = compareStrings((String) args.get(left).getLeft(), (String) args.get(right).getLeft(), op);
            } else if (args.get(left).getRight().equals(Date.class)) {
                res = compareDates((Date) args.get(left).getLeft(), (Date) args.get(right).getLeft(), op);
            }
        } else if (hasLeftField) {
            if (args.get(left).getRight().equals(Integer.class)) {
                res = compareInts(
                        (Integer) args.get(left).getLeft(), Integer.parseInt(right), op);

            } else if (args.get(left).getRight().equals(String.class)) {
                res = compareStrings((String) args.get(left).getLeft(), right.substring(1, right.length() - 1), op);
            } else if (args.get(left).getRight().equals(Date.class)) {
                res = compareDates((Date) args.get(left).getLeft(),
                        new SimpleDateFormat("yyyy.MM.dd").parse(right.substring(1, right.length() - 1)), op);
            }

        } else if (hasRightField) {
            if (args.get(right).getRight().equals(Integer.class)) {
                res = compareInts(
                        Integer.parseInt(left), (Integer) args.get(right).getLeft(), op);

            } else if (args.get(left).getRight().equals(String.class)) {
                res = compareStrings(
                        left.substring(1, right.length() - 1), (String) args.get(right).getLeft(), op);
            } else if (args.get(left).getRight().equals(Date.class)) {
                res = compareDates(new SimpleDateFormat("yyyy.MM.dd").parse(left.substring(1, right.length() - 1)), (Date) args.get(right).getLeft(), op);
            }
        } else {
            if (left.charAt(0) != '\'') {
                res = compareInts(
                        Integer.parseInt(left), Integer.parseInt(right), op);

            } else {
                res = compareStrings(left, right, op);
            }
        }
        return res;
    }

    static Boolean compareWithNull(String left, String
            right, String op, Map<String, Pair<Object, Type>> args) throws ParseException {
        if (left.equals("NULL") || !right.equals("NULL")) {
            throw new ParseException("Right operand is not NULL", 0);
        }
        return args.get(left).getLeft() == null;
    }

    static Pair<String, Boolean> execOperation(String left, String
            right, String op, Map<String, Pair<Object, Type>> args) throws ParseException {
        Boolean res = true;

        switch (op) {
            case "AND" -> {
                res = Boolean.parseBoolean(left) && Boolean.parseBoolean(right);
            }
            case "OR" -> {
                res = Boolean.parseBoolean(left) || Boolean.parseBoolean(right);
            }
            case "IS" -> {
                res = compareWithNull(left, right, op, args);
            }
            default -> {
                res = compareObjects(left, right, op, args);
            }
        }
        return new Pair<>(res ? "true" : "false", false);
    }

    public Boolean execute(Map<String, Pair<Object, Type>> args) throws ParseException {
        Stack<Pair<String, Boolean>> stack = new Stack<>();
        for (Pair<String, Boolean> op : operations) {
            if (op.getRight()) {
                if (op.getLeft().equals("NOT")) {
                    Pair<String, Boolean> prevOp = stack.pop();
                    prevOp.setLeft(prevOp.getLeft().equals("true") ? "false" : "true");
                    stack.push(prevOp);
                } else {
                    Pair<String, Boolean> right = stack.pop();
                    Pair<String, Boolean> left = stack.pop();
                    stack.push(execOperation(left.getLeft(), right.getLeft(), op.getLeft(), args));
                }
            } else {
                stack.push(op);
            }
        }

        return stack.peek().getLeft().equals("true");
    }
}
