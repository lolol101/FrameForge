package frameforge.server;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.mongodb.reactivestreams.client.*;

import frameforge.server.Pair;
import frameforge.server.ImageHandler.ImgType;
import frameforge.server.SubscriberHelper.CountSubscriber;
import frameforge.server.SubscriberHelper.DocumentSubscriber;
import frameforge.server.SubscriberHelper.InsertSubscriber;
import frameforge.server.SubscriberHelper.PrintSubscriber;

import com.mongodb.client.result.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Updates.set;

import frameforge.serializable.ImageKeeper;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.apache.commons.codec.binary.Hex;



public class HttpHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public static MongoDatabase db;
    private static ObjectMapper jsMapper = new ObjectMapper();
    private static String pathToFullImgs = "/home/project/fullImages/";
    private static String pathToScaledImgs = "/home/project/scaledImages/";
    private static String pathToAvatarImgs = "/home/project/avatarImages/";

    private enum ACTIONS {
        REGISTRATION,
        AUTHORIZATION,
        GET_MAIN_POST,
        GET_FULL_PHOTO,
        SET_LIKE, 
        SET_COMMENT,
        SUBSCRIBE,
        //EXTEND_TOKEN
    };

    private enum RESPONSE_TYPE {
        REGISTER_BACK, 
        AUTHORIZATION_BACK,
        MAKE_POST_BACK,
        GET_MAIN_POST_BACK,
        GET_FULL_PHOTO_BACK,
        SET_LIKE_BACK,
        SET_COMMENT_BACK,
        SUBSCRIBE_BACK,
        //EXTEND_TOKEN_BACK
    };

    private enum STATUS {
        OK,
        USERNAME_EXIST,
        USERNAME_NOT_FOUND,
        PASS_WRONG,
        ERROR
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
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
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
        System.out.println("in handleRequest method");
        JsonNode req = deserialize(JsonNode.class);
        System.out.println("after getReq");
        ACTIONS type = ACTIONS.valueOf(req.get("type").textValue());
        ObjectNode response = null;
        Pair<ObjectNode, ImageKeeper> pair = null;
        System.out.println("before switch");
        switch (type) {
            case REGISTRATION:
                response = register(req); 
                break;
            case AUTHORIZATION:
                response = authorize(req);
                break;
            case GET_MAIN_POST:
                //response = getMainPost(req);
                break;
            case GET_FULL_PHOTO:
                System.out.println("Case GET_FULL_PHOTO");
                pair = getFullPhoto(req);
                response = pair.a;
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
        sendSerializableObject(response);
        if (type == ACTIONS.GET_FULL_PHOTO || 
        type == ACTIONS.GET_MAIN_POST) {
            System.out.println(pair.b.toString());
            sendSerializableObject(pair.b);
        }

        //closeAll();
    }

    private <T> T deserialize(Class<T> clazz)
    throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        return clazz.cast(obj);
    }

    private void closeAll() throws IOException {
        socket.close();
        out.close();
        in.close();
    }

    private void sendSerializableObject(Object obj)
    throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    private byte[] getPhotoBytesFromPath(String path)
    throws IOException {
        System.out.println("path: " + path);
        BufferedImage image = ImageIO.read(new File(path));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] tmp = path.split("\\.");
        String ext = tmp[tmp.length-1];
        ImageIO.write(image, ext, baos);
        baos.flush();
        byte[] byteArray = baos.toByteArray();
        baos.close();
        return byteArray;
    }

    private Pair<ObjectNode, ImageKeeper> getFullPhoto(JsonNode req)
    throws IOException {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.GET_FULL_PHOTO_BACK.toString());
        response.put("status", STATUS.OK.toString());
        ObjectId postId = new ObjectId(req.get("postId").textValue());
        Integer pos = Integer.valueOf(req.get("pos").textValue());

        MongoCollection<Document> posts = db.getCollection("posts");
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Document> receivedDoc = new ArrayList<>();
        Bson filter = eq("_id", postId);
        posts.find(filter)
            .subscribe(new DocumentSubscriber<Document>(latch, receivedDoc));
        try {
            latch.await();
        } catch (Exception e) {}
        if (receivedDoc.isEmpty()) {
            response.put("status", STATUS.ERROR.toString());
            return new Pair<>(response, null);
        }             
        Document post = receivedDoc.get(0);
        ArrayList<String> arrayPhotos = (ArrayList<String>)post.get("arrayPhotos");
        // String path = "/home/igorstovba/Documents/Test/"; // replace Path with pathToFullImgs
    
        byte[] bytes = getPhotoBytesFromPath(pathToFullImgs + arrayPhotos.get(pos));
        ImageKeeper keeper = new ImageKeeper(bytes);
        System.out.println(keeper.getImages().size());
        return new Pair<>(response, keeper);
    }

    // private JsonNode getRequest() throws IOException {
    //     String json = in.readLine();
    //     return jsMapper.readTree(json);
    // }

    private ObjectNode register(JsonNode req) {
        System.out.println("in register method");
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
        // String extension = req.get("extensionOfImage").textValue();
        // String img = req.get("authorPhoto").textValue();
        // byte[] decodedBytes = Base64.getDecoder().decode(img);
        // String pathToAvatar = pathToAvatarImgs + username + "." + extension;  
        // try {
        //     BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(decodedBytes));
        //     ImageIO.write(bufImg, extension, new File(pathToAvatar));
        // } catch (IOException e) {
        //     response.put("status", STATUS.ERROR.toString());
        //     return response;
        // }
        
        Document newUser = new Document()
                            .append("username", username)
                            .append("password", password);
                            //.append("authorPhoto", pathToAvatar);
        users.insertOne(newUser)
            .subscribe(new InsertSubscriber<InsertOneResult>());
        return response;
    }

    private ObjectNode authorize(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.AUTHORIZATION_BACK.toString());
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
        if (receivedDocs.isEmpty()) {
            response.put("status", STATUS.USERNAME_NOT_FOUND.toString());
            return response;
        } 
        Document user = receivedDocs.get(0);
        if (!user.containsKey("password")
         || (password == user.get("password"))) {
            response.put("status", STATUS.PASS_WRONG.toString());
        }
        return response;
    }

    private ObjectNode getMainPost(JsonNode req) 
    throws IOException {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.GET_MAIN_POST_BACK.toString());
        response.put("status", STATUS.OK.toString());

        MongoCollection<Document> posts = db.getCollection("posts");
        ArrayList<Document> receivedDocs = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        posts.find()
            .subscribe(new DocumentSubscriber<Document>(latch, receivedDocs));
        
        try {
            latch.await();
        } catch (Exception e) {}
        if (receivedDocs.isEmpty()) {
            response.put("status", STATUS.ERROR.toString());
            return response;
        } 
        Document post = receivedDocs.get(0);
        //ImgType typeImg = ImgType.valueOf(req.get("typeImage").textValue());
        //String get_username = post.getString("username");
        ArrayList<String> get_photos_names = (ArrayList<String>)post.get("arrayPhotos");
        //ArrayList<String> get_comments = (ArrayList<String>)post.get("arrayComments");
        //Integer get_likes = post.getInteger("likes");
        //ObjectId get_id = post.getObjectId("_id");

        

        ArrayList<byte[]> images = new ArrayList<>();
        //String path = (typeImg == ImgType.FULL) ? pathToFullImgs : pathToScaledImgs;
        String path = "/home/igorstovba/Documents/Test/";  // replace Path
        
        for (String fname: get_photos_names) {
            byte[] tmp = getPhotoBytesFromPath(path + fname);
            images.add(tmp);
        }
        ImageKeeper keeper = new ImageKeeper(images);
        return response;
    }

    private ObjectNode setLike(JsonNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        response.put("type", RESPONSE_TYPE.SET_LIKE_BACK.toString());
        response.put("status", STATUS.OK.toString());
        MongoCollection<Document> posts = db.getCollection("posts");
        
        //update single document
        // collection.updateOne(
        //     eq("_id", new ObjectId("57506d62f57802807471dd41")),
        //     combine(set("stars", 1), set("contact.phone", "228-555-9999"), currentDate("lastModified"))
        // ).subscribe(new ObservableSubscriber<UpdateResult>());

        // posts.updateOne(
        //     eq("username", username), set("likes")
        // ).subscribe(new PrintSubscriber<String>());

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

    // private void sendJson(ObjectNode node)
    //  throws IOException{        
    //     String str = jsMapper.writeValueAsString(node);
    //     out.println(str);
    //     out.flush();
    // }
}
