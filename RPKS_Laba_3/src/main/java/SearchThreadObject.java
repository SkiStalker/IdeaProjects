import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

public class SearchThreadObject {
    public Integer getStartPos() {
        return startPos;
    }

    public Integer getThrStringsAmount() {
        return thrStringsAmount;
    }

    public EnumerateFile getLines() {
        return lines;
    }

    public FileWriter getWriter() {
        return writer;
    }

    public Integer getPreviousStringsCount() {
        return previousStringsCount;
    }

    public Integer getSubsequentStringsCount() {
        return subsequentStringsCount;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public TotalMatchesAmount getTotalMatchesAmount() {
        return totalMatchesAmount;
    }

    final private Integer startPos;
    final private Integer thrStringsAmount;
    final private EnumerateFile lines;
    final private FileWriter writer;
    final private Integer previousStringsCount;
    final private Integer subsequentStringsCount;
    final private String keyWord;
    final private Semaphore semaphore;

    final private TotalMatchesAmount totalMatchesAmount;

    public SearchThreadObject(Integer startPos, Integer thrStringsAmount,
                              EnumerateFile lines,
                              FileWriter writer, Integer previousStringsCount,
                              Integer subsequentStringsCount, String keyWord, Semaphore semaphore, TotalMatchesAmount totalMatchesAmount) {
        this.startPos = startPos;

        this.thrStringsAmount = thrStringsAmount;
        this.lines = lines;
        this.writer = writer;
        this.previousStringsCount = previousStringsCount;
        this.subsequentStringsCount = subsequentStringsCount;
        this.keyWord = keyWord;
        this.semaphore = semaphore;
        this.totalMatchesAmount = totalMatchesAmount;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
