import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class EnumerateFile implements Iterable<String> {
    private final RandomAccessFile randomAccessFile;
    private int linesCnt;

    public EnumerateFile(String path) throws IOException {
        randomAccessFile = new RandomAccessFile(path, "r");

        this.linesCnt = 0;
        int curChar = 0;
        while ((curChar = this.randomAccessFile.read()) != -1) {
            if (curChar == '\n') {
                linesCnt++;
            }
        }
        randomAccessFile.seek(0);
    }

    synchronized public String get(int i) {
        try {
            long pos = randomAccessFile.getFilePointer();
            randomAccessFile.seek(0);
            String line = randomAccessFile.readLine();
            while (i > 0) {
                line = randomAccessFile.readLine();
                i--;
            }
            randomAccessFile.seek(pos);
            return new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return null;
        }
    }

    public int size() {
        return this.linesCnt;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {

                try {
                    boolean res = randomAccessFile.read() != -1;
                    randomAccessFile.seek(randomAccessFile.getFilePointer() - 1);
                    return res;
                } catch (IOException ex) {
                    return false;
                }
            }

            @Override
            public String next() {
                try {
                    return new String(randomAccessFile.readLine().getBytes(StandardCharsets.ISO_8859_1),
                            StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return null;
                }
            }
        };
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return Iterable.super.spliterator();
    }
}
