package client;

public class Main {
    public static void main() {
        final String ip = "185.97.201.52";
        final int port = 8080;
        SocketManager socketManager = new SocketManager();
        socketManager.connect(ip, port);

    }
}
