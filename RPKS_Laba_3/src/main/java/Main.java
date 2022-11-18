import java.io.IOException;

public class Main {
    static public void main(String[] args) {
        try {
            StringSearcher stringSearcher = new StringSearcher("keyWord", "source.txt", "res.txt",
                    2, 0, 0);

            stringSearcher.Process();
        } catch (IOException ex) {
            System.out.println("Can not find specified file");
        } catch (InterruptedException ex) {
            System.out.println("Internal semaphore exception");
        }
    }
}
