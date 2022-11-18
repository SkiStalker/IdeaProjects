import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Semaphore;

public class StringSearcher {
    final private String keyWord;
    final private String filePath;
    final private Integer threadCount;
    final private FileWriter writer;
    final private Integer previousStringsCount;
    final private Integer subsequentStringsCount;
    private final TotalMatchesAmount totalMatchesAmount;
    final private Semaphore semaphore;

    public StringSearcher(String keyWord, String filePath, String resultPath, Integer threadCount,
                          Integer previousStringsCount, Integer subsequentStringsCount) throws IOException {

        this.keyWord = keyWord;
        this.filePath = filePath;
        this.threadCount = threadCount;
        this.writer = new FileWriter(resultPath);
        this.previousStringsCount = previousStringsCount;
        this.subsequentStringsCount = subsequentStringsCount;
        this.semaphore = new Semaphore(threadCount);
        this.totalMatchesAmount = new TotalMatchesAmount(0);
    }


    public void Process() throws IOException, InterruptedException {
        EnumerateFile lines = new EnumerateFile(this.filePath);
        int linesCount = lines.size();
        int thrLinesCount = linesCount / this.threadCount;
        if (thrLinesCount == 0) {
            thrLinesCount = 1;
        }
        for (int i = 0; linesCount > 0; i += thrLinesCount, linesCount -= thrLinesCount) {
            semaphore.acquire();
            int curThrLinesCount = thrLinesCount;
            if (linesCount - thrLinesCount < thrLinesCount) {
                curThrLinesCount += linesCount - thrLinesCount;
            }
            Thread thr =  new Thread(new SearchRunnable(new SearchThreadObject(
                    i,
                    curThrLinesCount,
                    lines,
                    writer,
                    previousStringsCount,
                    subsequentStringsCount,
                    keyWord,
                    semaphore,
                    totalMatchesAmount
            )));
            thr.setUncaughtExceptionHandler((t, e) -> System.out.println("Can not write info to file"));
            thr.start();
        }

        semaphore.acquireUninterruptibly(this.threadCount);
        this.writer.close();
        System.out.println("Completed");
    }
}
