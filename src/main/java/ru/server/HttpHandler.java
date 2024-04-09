package ru.server;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.mongodb.reactivestreams.client.*;
import com.mongodb.client.result.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;


import org.bson.Document;
import org.bson.conversions.Bson;
import ru.server.SubscriberHelper.CountSubscriber;
import ru.server.SubscriberHelper.DocumentSubscriber;
import ru.server.SubscriberHelper.InsertSubscriber;



public class HttpHandler implements Runnable {
    private Socket socket;
    public static MongoDatabase db;
    private static ObjectMapper jsMapper = new ObjectMapper();
    private static String pathToFullImgs = "/home/project/fullImages/";
    private static String pathToScaledImgs = "/home/project/scaledImages/";
    private static String pathToAvatarImgs = "/home/project/avatarImages/";

    private enum ACTIONS {
        REGISTRATION,
        AUTHORIZATION,
        GET_MAIN_PAGE_POSTS_SET,
        SET_LIKE, 
        SET_COMMENT,
        SUBSCRIBE
    };

    private enum RESPONSE_TYPE {
        REGISTER_BACK, 
        AUTHORIZATION_BACK,
        MAKE_POST_BACK,
        GET_MAIN_PAGE_POSTS_SET_BACK,
        SET_LIKE_BACK,
        SET_COMMENT_BACK,
        SUBSCRIBE_BACK
    };

    private enum STATUS {
        OK,
        USERNAME_EXIST,
        USERNAME_NOT_FOUND,
        PASS_WRONG,
        ERROR,
    };

    static {
        pathToFullImgs = "/home/project/fullImages/";
        pathToScaledImgs = "/home/project/scaledImages/";
        pathToAvatarImgs = "/home/project/avatarImages/";
    }

    public HttpHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("start handleRequest");
            handleRequest();
            System.out.println("End handleRequest");
        } catch (Exception e) {
            System.out.println("Error (HttpHandler/run): " + e.getMessage());
            try {
                this.socket.close();
            } catch (IOException ae) {
                System.out.println("Error while close socket conn!");
            }
        }
    }

    private void handleRequest() throws Exception {
        // JsonNode req = getRequest();
        // ACTIONS type = ACTIONS.valueOf(req.get("type").textValue());
        // if (type == ACTIONS.IMAGE) {
        //     String img = req.get("image").textValue();
        //     byte[] decodedBytes = Base64.getDecoder().decode(img);
        //     BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            
        //     ImageIO.write(bufImg, "JPG", new File(outPath));
        // }
        JsonNode req = getRequest();
        //need to wrap in try - catch
        ACTIONS type = ACTIONS.valueOf(req.get("type").textValue());
        ObjectNode response = null;
        switch (type) {
            case REGISTRATION:
                response = register(req); 
                break;
            case AUTHORIZATION:
                response = authorize(req);
                break;
            case GET_MAIN_PAGE_POSTS_SET:
                response = getMainPagePostsSet(req);
                break;
            case SET_LIKE:
                response = setLike(req);
                break;
            case SET_COMMENT:
                response = setComment(req);
                break;
            case SUBSCRIBE:
                response = subscribe(req);
                break;
            default:
                System.out.println("Unsuppoted type");
                System.exit(1);
        }
        sendJson(response);
    }

    public List<String> getKeysInJsonUsingJsonNodeFieldNames(JsonNode json, ObjectMapper mapper) throws JsonMappingException, JsonProcessingException {

        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = json.fieldNames();
        iterator.forEachRemaining(e -> keys.add(e));
        return keys;
    }

    private JsonNode getRequest() throws IOException {
        InputStream inStream = socket.getInputStream();
        String json = new BufferedReader(
            new InputStreamReader(inStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
        JsonNode jsNode = jsMapper.readTree(json);
        return jsNode;
    }

    private ObjectNode register(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.REGISTER_BACK.toString());
        response.put("status", STATUS.OK.toString());

        MongoCollection<Document> users = db.getCollection("users");
        String username = req.get("username").textValue();

        CountDownLatch latch = new CountDownLatch(1);
        int[] count = {0}; 
        Bson filter = eq("username", username);
        users.find(filter).subscribe(new CountSubscriber<>(latch, count));
        try {
            latch.await();
        } catch (Exception e) {}
        if (count[0] > 0) {
            response.put("status", STATUS.USERNAME_EXIST.toString());
            return response;
        }
        String password = req.get("password").textValue();
        String extension = req.get("extensionOfImage").textValue();
        String img = req.get("authorPhoto").textValue();
        byte[] decodedBytes = Base64.getDecoder().decode(img);
        String pathToAvatar = pathToAvatarImgs + username + "." + extension;  
        try {
            BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            ImageIO.write(bufImg, extension, new File(pathToAvatar));
        } catch (IOException e) {
            response.put("status", STATUS.ERROR.toString());
            return response;
        }
        
        Document newUser = new Document()
                            .append("username", username)
                            .append("password", password)
                            .append("authorPhoto", pathToAvatar);
        users.insertOne(newUser)
            .subscribe(new InsertSubscriber<InsertOneResult>());
        return response;
    }

    private ObjectNode authorize(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("status", STATUS.OK.toString());

        MongoCollection<Document> users = db.getCollection("users");
        String username = req.get("username").textValue();
        String password = req.get("password").textValue();

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Document> receivedDocs = new ArrayList<>();
        Bson filter = eq("username", username);
        users.find(filter)
            .subscribe(new DocumentSubscriber<Document>(latch, receivedDocs));
        try {
            latch.await();
        } catch (Exception e) {}
        if (receivedDocs.size() == 0) {
            response.put("status", STATUS.USERNAME_NOT_FOUND.toString());
            return response;
        } 
        Document user = receivedDocs.get(0);
        if (!user.containsKey("password")
         || !(password != user.get("password"))) {
            response.put("status", STATUS.PASS_WRONG.toString());
        }
        return response;
    }

    private ObjectNode getMainPagePostsSet(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.GET_MAIN_PAGE_POSTS_SET_BACK.toString());



        return response;
    }

    private ObjectNode setLike(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.SET_LIKE_BACK.toString());

        MongoCollection<Document> users = db.getCollection("users");
        


        return response;
    }

    private ObjectNode setComment(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.SET_COMMENT_BACK.toString());



        return response;
    }

    private ObjectNode subscribe(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.SUBSCRIBE_BACK.toString());



        return response;
    }


    // private void sendPhoto(ImageHandler imgHandler) 
    // throws IOException {

    //     ByteArrayOutputStream out = imgHandler.getByteArray();
    //     String imgAsJson = Base64.getEncoder().encodeToString(out.toByteArray());
    
    //     ObjectNode node = jsMapper.createObjectNode();
    //     node.put("type", ACTIONS.IMAGE.toString());
    //     node.put("image", imgAsJson);
    //     node.put("typeImage", imgHandler.typeImage.toString());
    //     node.put("formatImage", imgHandler.formatImage);
    //     sendJson(node);
    // }   

    private void sendJson(ObjectNode node)
     throws IOException{
        OutputStream outStream = socket.getOutputStream();
        jsMapper.writeValue(outStream, node);
        outStream.flush();
        outStream.close();
    }
}
