import java.io.IOException;
import java.util.Scanner;

public class SeaBattleGame {
    public void connectToGame() {

    }
    public void processGame(SeaBattleClient seaBattleClient) {
        boolean process = true;
        SeaBattleField myField = new SeaBattleField();
        SeaBattleField enemyField = new SeaBattleField();
        Scanner scanner = new Scanner(System.in);
        boolean myMotion = true;
        while(process) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Your field");
            System.out.println(myField.getStringField());
            System.out.println();
            System.out.println("Enemy field");
            System.out.println(enemyField.getStringField());

            if (myMotion) {
                boolean incorrectInput = true;
                while(incorrectInput) {
                    System.out.println("Enter shot position ('Horizontal pos as char', 'Vertical pos as int')");
                    Integer x = scanner.next().charAt(0) - 'A' + 1;
                    Integer y = scanner.nextInt();

                    if (!enemyField.tryAddMyShot(x, y)) {
                        System.out.println("Incorrect input");
                    } else {
                        seaBattleClient.sendMyShot(new Integer[]{x, y});
                        incorrectInput = false;
                    }
                }
                myMotion = false;
            } else {
                myMotion = true;
                System.out.println("Enemy motion");
                Integer[] shotPos =  seaBattleClient.readEnemyShot();

            }


        }

    }
    public void createNewGame() {
        try {
            SeaBattleServer seaBattleServer = new SeaBattleServer();
            System.out.printf("Create server with parameters %s%n", seaBattleServer.getServerParameters());
            System.out.println("Wait connection another player");
            seaBattleServer.waitConnection();
            System.out.printf("Player with address %s connected\n", seaBattleServer.getClientParameters());
            System.out.println("Game will start in 2 seconds");
            Thread.sleep(2000);
            processGame(seaBattleServer.getSeaBattleClient());

        } catch (IOException | InterruptedException ex) {
            System.out.println("Can not create game server");
        }

    }
}
