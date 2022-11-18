import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryAction {
    private final String id;
    private final Date actionDate;
    private final String type;
    private final String query;
    private final String number;

    public String getId() {
        return id;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public String getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public String getNumber() {
        return number;
    }

    public String getAction() {
        return action;
    }

    private final String action;

    public QueryAction(String query) throws ParseException {
        this.query = query;

        String[] dataQueryParts = query.split("–");

        String[] idDataParts = dataQueryParts[0].split(" ");
        this.id = idDataParts[0].substring(0, idDataParts.length - 1);
        this.actionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse(idDataParts[1] + " " + idDataParts[2]);

        this.type = dataQueryParts[1].strip();

        String[] actionNumberParts = dataQueryParts[2].split("=");
        this.number = actionNumberParts[1];

        String[] actionParamsParts = actionNumberParts[0].strip().split(" ");
        if (actionParamsParts.length > 3) {
            this.action = actionParamsParts[0];
        } else {
            this.action = "PERFORM";
        }
    }
}
