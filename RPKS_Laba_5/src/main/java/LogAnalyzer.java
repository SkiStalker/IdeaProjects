import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

public class LogAnalyzer {
    final private EnumerateFile lines;
    final private Long criticalDeviation;
    final private Boolean isCustomCriticalDeviation;
    private Long averageDeviation;

    public LogAnalyzer(String path, Long msCriticalDeviation) throws IOException {
        this.isCustomCriticalDeviation = msCriticalDeviation != null;
        this.lines = new EnumerateFile(path);
        this.criticalDeviation = msCriticalDeviation;
        this.averageDeviation = 0L;
    }

    Boolean checkDeviant(Long deviation) {
        if (isCustomCriticalDeviation) {
            return deviation < this.criticalDeviation;
        } else {
            if (averageDeviation == 0) {
                averageDeviation = deviation;
                return true;
            } else if (deviation > averageDeviation) {
                averageDeviation = (averageDeviation +  deviation) / 2;
                return false;
            } else {
                return true;
            }
        }
    }

    public void Analyze() {
        Stack<QueryAction> actions = new Stack<>();
        Long linesProceed = 1L;
        int linesCnt = lines.size();
        for (String line : lines) {
            try {
                QueryAction action = new QueryAction(line);
                if (action.getAction().equals("PERFORM")) {
                    actions.push(action);
                } else if (action.getAction().equals("RESULT")) {
                    Optional<QueryAction> actionOptional = actions.stream()
                            .filter((item) -> item.getNumber().equals(action.getNumber())).findFirst();

                    if (actionOptional.isEmpty()) {
                        System.out.println("Undeclared query");
                    } else {
                        QueryAction startAction = actionOptional.get();

                        Long deviation = action.getActionDate().getTime() -startAction.getActionDate().getTime();                             ;

                        if (!checkDeviant(deviation)) {
                            System.out.printf("Abnormal query execution time. Query ID=%s\n", action.getNumber());
                        }
                    }
                } else {
                    System.out.println("Incorrect query action type");
                }
            } catch (ParseException ex) {
                System.out.println("Incorrect query format");
            }
            System.out.printf("Lines proceed %d of %d\n", linesProceed, linesCnt);
            linesProceed++;
        }
    }
}
