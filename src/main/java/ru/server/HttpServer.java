import java.io.*;
import java.net.*;
import ru.server.*;
import java.util.concurrent.*;

public class HttpServer {

    private static final int NUM_THREADS = 4;
    private static int port = 8080;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start();
    }

    HttpServer() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            
            HttpHandler connection = new HttpHandler(socket);
            pool.execute(connection);
        }
    }

    
}
