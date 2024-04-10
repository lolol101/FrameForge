package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.OutputStream;
import java.net.Socket;

public class SocketManager {
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

//    private void sendPhoto(ImageHandler imgHandler)
//            throws IOException {
//
//        ByteArrayOutputStream out = imgHandler.getByteArray();
//        String imgAsJson = Base64.getEncoder().encodeToString(out.toByteArray());
//
//        ObjectNode node = jsMapper.createObjectNode();
//        node.put("type", ACTIONS.IMAGE.toString());
//        node.put("image", imgAsJson);
//        node.put("typeImage", imgHandler.typeImage.toString());
//        node.put("formatImage", imgHandler.formatImage);
//        sendJson(node);
//    }
//
//    private void register(String username, String password)
//            throws IOException {
//        ObjectNode node = jsMapper.createObjectNode();
//        node.put("type", ACTIONS.REGISTRATION.toString());
//        node.put("username", username);
//        node.put("password", password);
//        sendJson(node);
//    }
//
//    private void authorize(String username, String password)
//            throws IOException {
//        ObjectNode node = jsMapper.createObjectNode();
//        node.put("type", ACTIONS.AUTHORIZATION.toString());
//        node.put("username", username);
//        node.put("password", password);
//        sendJson(node);
//    }
//
}
