//package src.main.java.client;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import com.google.gson.Gson;
import java.util.Base64;


public class Client {

    public static final String SERVER = "127.0.0.1";
    public static final int PORT = 8080;
    public static Socket socket;

    private static ObjectMapper jsMapper = new ObjectMapper();

    private enum ACTIONS {
        REGISTRATION,
        AUTHORIZATION,
        SET,
        GET, 
        UPDATE,
        IMAGE,
        BACK_RESPONSE;
    };

    private enum STATUS {
        OK,
        NOT_EXISTED_USERNAME,
        EXISTED_USERNAME,
        WRONG_PASSWORD
    }

    public static void main(String[] args) {
        socket = null;
        String inPath = "/home/igorstovba/Documents/Test/photo.jpg";
        try {
            socket = new Socket(SERVER, PORT);
            
            //register("kkkk", "uuiuiuiuiu");
            // ImageHandler imgHandler = new ImageHandler(inPath, ImageHandler.ImgType.FULL, "JPG");
            // imgHandler.resizeImage(300, 300);
            // sendPhoto(imgHandler);
            //imgHandler.writeToFile(outPath, "JPG");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static JsonNode getRequest() throws IOException {
        InputStream inStream = socket.getInputStream();
        String json = new BufferedReader(
            new InputStreamReader(inStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
        JsonNode jsNode = jsMapper.readTree(json);
        return jsNode;
    }

    private static void sendPhoto(ImageHandler imgHandler) 
    throws IOException {

        ByteArrayOutputStream out = imgHandler.getByteArray();
        String imgAsJson = Base64.getEncoder().encodeToString(out.toByteArray());
    
        ObjectNode node = jsMapper.createObjectNode();
        node.put("type", ACTIONS.IMAGE.toString());
        node.put("image", imgAsJson);
        node.put("typeImage", imgHandler.typeImage.toString());
        node.put("formatImage", imgHandler.formatImage);
        sendJson(node);
    }

    private static void register(String username, String password) 
    throws IOException {
        ObjectNode node = jsMapper.createObjectNode();
        node.put("type", ACTIONS.REGISTRATION.toString());
        node.put("username", username);
        node.put("password", password);
        sendJson(node);
    }

    private static void authorize(String username, String password)
     throws IOException {
        ObjectNode node = jsMapper.createObjectNode();
        node.put("type", ACTIONS.AUTHORIZATION.toString());
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