//package src.main.java.client;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Client {

    public static final String SERVER = "127.0.0.1";
    public static final int PORT = 8080;
    public static Socket socket;

    private static ObjectMapper jsMapper = new ObjectMapper();
    private enum ACTIONS {
        REGISTRATION,
        AUTHORAZATION,
        SET,
        GET, 
        UPDATE
    };

    public static void main(String[] args) {
        socket = null;
        try {
            socket = new Socket(SERVER, PORT);
            
            register("Igor", "SaB4_BaSS");
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // private static String getString() throws IOException {
    //     InputStream in = socket.getInputStream();
    //     DataInputStream dataIn = new DataInputStream(in);
    //     String req = dataIn.readUTF();
    //     dataIn.close();
    //     return req;
    // }

    // private static void sendString(String req) throws IOException {
    //     OutputStream out = socket.getOutputStream();
    //     DataOutputStream dataOut = new DataOutputStream(out);
    //     dataOut.writeUTF(req);
    //     dataOut.flush();
    //     dataOut.close();
    // }

    private static JsonNode getRequest() throws IOException {
        InputStream inStream = socket.getInputStream();
        String json = new BufferedReader(
            new InputStreamReader(inStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
        JsonNode jsNode = jsMapper.readTree(json);
        return jsNode;
    }

    private static void register(String username, String password) 
    throws IOException {
        ObjectNode node = jsMapper.createObjectNode();
        node.put("type", ACTIONS.REGISTRATION.toString());
        node.put("username", username);
        node.put("password", password);
        sendJson(node);
    }

    private static void sendJson(ObjectNode node)
     throws IOException{
        OutputStream outStream = socket.getOutputStream();
        jsMapper.writeValue(outStream, node);
        outStream.flush();
        outStream.close();
    }
}