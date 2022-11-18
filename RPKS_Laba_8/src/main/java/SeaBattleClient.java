import java.io.*;
import java.net.Socket;

public class SeaBattleClient {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    public SeaBattleClient(Socket socket) throws IOException {
        clientSocket = socket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public String getClientParameters() {
        if (clientSocket != null) {
            return "%s:%d".formatted(this.clientSocket.getInetAddress().getHostAddress(),
                    this.clientSocket.getLocalPort());
        } else {
            return null;
        }
    }

    public void sendMyShot(Integer[] pos) {

    }
    public Integer[] readEnemyShot() {
        return new Integer[2];
    }
}
