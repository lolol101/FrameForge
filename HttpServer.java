import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import ru.server.Request;


public class HttpServer {

    //public static ArrayList<ClientHandler> onlineUsers = new ArrayList<ClientHandler>();
    private static final int NUM_THREADS = 4;
    private static int port;

    public static void main(String[] args) {
        HttpServer server = new HttpServer(8080);
        server.start();
    }

    HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        //ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        char[] array = new char[100];
        try (ServerSocket server = new ServerSocket(this.port)) {
            while (true) {
                try (Socket conn = server.accept()) {
                    //InputStreamReader in = new InputStreamReader(conn.getInputStream(), "UTF8");
                    //in.read(array);
                    
                    ObjectInputStream reader = new ObjectInputStream(conn.getInputStream());

                    Request new_rq = null;
                    try {
                        new_rq = (Request) reader.readObject();
                    } catch (ClassNotFoundException e) {
                        System.out.println("Class not Found!!!!!!");
                    }
                     
                    System.out.println(new_rq.getType());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        
        

    }

    
}
