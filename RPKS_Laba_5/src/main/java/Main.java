import java.io.IOException;

public class Main {
    static public void main(String[] args) {
        try {
            LogAnalyzer logAnalyzer = new LogAnalyzer("logs.txt", null);
            logAnalyzer.Analyze();
        }
        catch (IOException ex) {
            System.out.println("Can not open log file");
        }
    }
}