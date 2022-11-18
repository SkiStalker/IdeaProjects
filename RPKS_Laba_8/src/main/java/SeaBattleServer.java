import java.io.*;
import java.net.ServerSocket;

public class SeaBattleServer {
    private final ServerSocket server;

    public SeaBattleClient getSeaBattleClient() {
        return seaBattleClient;
    }

    private SeaBattleClient seaBattleClient;


    public SeaBattleServer() throws IOException {
        this.server = new ServerSocket(5000);
    }

    public void waitConnection() throws IOException {
        this.seaBattleClient = new SeaBattleClient(server.accept());
    }

    public String getServerParameters() {
        return "%s:%d".formatted(this.server.getInetAddress().getHostAddress(), this.server.getLocalPort());
    }

    public String getClientParameters() {
        return this.seaBattleClient.getClientParameters();
    }


}
