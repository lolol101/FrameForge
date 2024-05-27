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
import frameforge.server.SubscriberHelper.*;
import com.mongodb.client.result.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;

import java.security.*;
import java.math.*;
import frameforge.serializable.JsonSerializable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import frameforge.server.Constants.*;



public class HttpHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public static MongoDatabase db;
    private static ObjectMapper jsMapper = new ObjectMapper();
    private static String pathToFullImgs = "/home/project/fullImages/";
    private static String pathToScaledImgs = "/home/project/scaledImages/";
    private static String pathToAvatarImgs = "/home/project/avatarImages/";

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
        JsonSerializable msg = deserialize(JsonSerializable.class);
        ObjectNode req = msg.getJson();
        ArrayList<byte[]> keeper = msg.getImages();
        ACTIONS type = ACTIONS.valueOf(req.get("type").textValue());
        JsonSerializable response = null;
        switch (type) {
            case REGISTRATION:
                response = register(req); 
                break;
            case AUTHORIZATION:
                response = authorize(req);
                break;
            case SET_MAIN_POST:
                response = setMainPost(req, keeper);
                break;
            case GET_MAIN_POST:
                System.out.println("Case GET_MAIN_POST");
                response = getMainPost(req);
                break;
            case GET_FULL_PHOTO:
                System.out.println("Case GET_FULL_PHOTO");
                response = getFullPhoto(req);
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

    private JsonSerializable getFullPhoto(ObjectNode req)
    throws IOException {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
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
            ret.setJson(response);
            return ret;
        }             
        Document post = receivedDoc.get(0);
        ArrayList<String> arrayPhotos = (ArrayList<String>)post.get("arrayPhotos");
        // String path = "/home/igorstovba/Documents/Test/"; // replace Path with pathToFullImgs
    
        byte[] bytes = getPhotoBytesFromPath(pathToFullImgs + arrayPhotos.get(pos));
        ret.setJson(response);
        ret.setOnePhoto(bytes);
        return ret;
    }

    private JsonSerializable register(ObjectNode req) {
        System.out.println("in register method");
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
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
            ret.setJson(response);
            return ret;
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
        ret.setJson(response);
        return ret;
    }

    private JsonSerializable authorize(ObjectNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
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
            ret.setJson(response);
            return ret;
        } 
        Document user = receivedDocs.get(0);
        if (!user.containsKey("password")
         || (password == user.get("password"))) {
            response.put("status", STATUS.PASS_WRONG.toString());
        }
        ret.setJson(response);
        return ret;
    }

    private JsonSerializable getMainPost(ObjectNode req) 
    throws IOException {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
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
            ret.setJson(response);
            return ret;
        } 
        Document post = receivedDocs.get(0);
        ImgType typeImg = ImgType.valueOf(req.get("typeImage").textValue());
        String username = post.getString("username");
        ArrayList<String> photos_names = (ArrayList<String>)post.get("arrayPhotos");
        ArrayList<String> comments = (ArrayList<String>)post.get("arrayComments");
        Integer likes = post.getInteger("likes");
        ObjectId id = post.getObjectId("_id");

        

        ArrayList<byte[]> images = new ArrayList<>();
        String path = (typeImg == ImgType.FULL) ? pathToFullImgs : pathToScaledImgs;
        // String path = "/home/igorstovba/Documents/Test/";  // replace Path
        
        for (String fname: photos_names) {
            byte[] tmp = getPhotoBytesFromPath(path + fname);
            images.add(tmp);
        }
        response.put("username",username);
        response.put("likes", likes);
        response.put("id", id.toString());
        ArrayNode arrNode = response.putArray("comments");
        for (var item: comments) 
            arrNode.add(item);
        

        ret.setJson(response);
        ret.setManyPhotos(images);
        return ret;
    }

    private JsonSerializable setMainPost(ObjectNode req, ArrayList<byte[]> photos) throws IOException, NoSuchAlgorithmException {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
        response.put("type", RESPONSE_TYPE.SET_MAIN_POST_BACK.toString());
        response.put("status", STATUS.OK.toString());

        MongoCollection<Document> posts = db.getCollection("posts");
        
        String username = req.get("username").textValue();
        ArrayNode arrayOfExtensions = req.withArrayProperty("extensionOfImage");
        
        // Putting photos in FS
        ArrayList<String> namesPhotos = new ArrayList<>();
        for (int i = 0; i < photos.size(); ++i) {
            String ext = arrayOfExtensions.get(0).textValue();
            namesPhotos.add(__putFileIntoFS(ext, photos.get(i)));
        }

        Document newPost = new Document()
                        .append("username", username)
                        .append("arrayPhotos", namesPhotos)
                        .append("arrayComments", new ArrayList<String>())
                        .append("likes", 0);
        posts.insertOne(newPost)
            .subscribe(new InsertSubscriber<InsertOneResult>());
        ret.setJson(response);
        return ret;
    }

    private String __putFileIntoFS(String ext, byte[] photo)
    throws IOException, NoSuchAlgorithmException {
        /*
         * Format: FULL
         */
        String filename = "photo_" + __hashFromTime() + "." + ext;
        String path = pathToFullImgs + filename; 
        BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(photo));
        ImageIO.write(bufImg, ext, new File(path));
        return filename;
    }

    private String __hashFromTime() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(String.valueOf(System.currentTimeMillis()).getBytes());
        String md5 = new BigInteger(1, md.digest()).toString(16);

        return md5;
    }

    private JsonSerializable setLike(ObjectNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
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

        return ret;
    }

    private JsonSerializable setComment(ObjectNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
        response.put("type", RESPONSE_TYPE.SET_COMMENT_BACK.toString());



        return ret;
    }

    

    private JsonSerializable subscribe(ObjectNode req) {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
        response.put("type", RESPONSE_TYPE.SUBSCRIBE_BACK.toString());



        return ret;
    }
}
