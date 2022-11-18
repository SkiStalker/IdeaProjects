import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class SearchRunnable implements Runnable {
    final SearchThreadObject parameter;

    public SearchRunnable(SearchThreadObject parameter) {
        this.parameter = parameter;

    }

    synchronized void WriteMatchesToFile(Integer curPos) throws IOException {
        for (int i = Math.max(curPos - this.parameter.getPreviousStringsCount(), 0);
             i < Math.min(curPos + this.parameter.getSubsequentStringsCount() + 1,
                     this.parameter.getLines().size()); i++) {
            this.parameter.getWriter().write(this.parameter.getLines().get(i) + "\n");
        }
    }

    synchronized void incTotalMatches() {
        this.parameter.getTotalMatchesAmount().setMatches(this.parameter.getTotalMatchesAmount().getMatches() + 1);
    }

    @Override
    public void run() {
        for (int i = this.parameter.getStartPos(); i < this.parameter.getStartPos() + this.parameter.getThrStringsAmount(); i++) {
            System.out.printf("%s at %d string (%d of %d)\n", Thread.currentThread().getName(), i,
                    i - this.parameter.getStartPos(), this.parameter.getThrStringsAmount());

            if (Arrays.stream(this.parameter.getLines().get(i).split(" ")).anyMatch((item) ->
                    Objects.equals(item, this.parameter.getKeyWord()))) {

                incTotalMatches();

                try {
                    this.WriteMatchesToFile(i);
                } catch (IOException e) {
                    Thread thr = Thread.currentThread();
                    thr.getUncaughtExceptionHandler().uncaughtException(thr, e);
                }

                System.out.printf("%s find key word-{\"%s\"} at \"%s\" string, total matches found = %d\n",
                        Thread.currentThread().getName(),
                        this.parameter.getKeyWord(),
                        this.parameter.getLines().get(i),
                        this.parameter.getTotalMatchesAmount().getMatches());
            }
        }
        System.out.printf("%s completed \n", Thread.currentThread().getName());
        this.parameter.getSemaphore().release();
    }
}
