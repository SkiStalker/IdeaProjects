import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    static public void printTitle() {
        System.out.println("Sea battle game");
        System.out.println("Menu:");
        System.out.println("1: Connect to game");
        System.out.println("2: Create new game");
        System.out.println("3: Exit");
    }
    public static void main(String[] args) {
        SeaBattleGame seaBattleGame = new SeaBattleGame();

        Scanner in = new Scanner(System.in);
        boolean run = true;
        while (run) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            printTitle();
            System.out.print("Input action: ");
            try {
                int num = in.nextInt();
                switch (num) {
                    case 1 -> {
                        seaBattleGame.connectToGame();
                    }
                    case 2 -> {
                        seaBattleGame.createNewGame();
                    }
                    case 3 -> {
                        run = false;
                    }
                    default -> {
                        System.out.println("Incorrect input");
                    }
                }
            }
            catch (Exception ex) {
                System.out.println("Incorrect input");
            }
        }

    }
}
