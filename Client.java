import java.io.*;
import java.net.*;
import ru.server.Request;

public class Client {

    public static final String SERVER = "127.0.0.1";
    public static final int PORT = 8080;

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(SERVER, PORT);
            OutputStream out = socket.getOutputStream();
            //Writer writer = new OutputStreamWriter(out, "UTF-8");
            ObjectOutputStream writer = new ObjectOutputStream(out);
            
            //creating request object
            Request rq = new Request(Request.Type.REGISTRATION);
            writer.writeObject(rq);
            //writer.write("Hello\n");
            writer.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}