package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.OutputStream;
import java.net.Socket;

public class SocketManager implements Runnable {
    private Socket socket = null;
    private static ObjectMapper jsMapper;

    public SocketManager() {
        try {
            jsMapper = new ObjectMapper();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendJson(ObjectNode node) {
        try (OutputStream outStream = socket.getOutputStream()) {
            jsMapper.writeValue(outStream, node);
            outStream.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void run() {

    }
}
