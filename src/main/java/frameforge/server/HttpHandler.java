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

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.*;
import java.awt.Image;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Updates.set;

import java.security.*;
import java.math.*;

import frameforge.recsystem.Recommender;
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
    public static Recommender recommender;
    private static ObjectMapper jsMapper = new ObjectMapper();

    private static String pathToFullImgs;
    private static String pathToScaledImgs;
    private static String pathToAvatarImgs;
    private static String localPath;

    static {
        localPath = "/home/igorstovba/Documents/Test/";
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
            System.out.println("start run");
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
                System.out.println("Case SET_MAIN_POST");
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
        String filename = arrayPhotos.get(pos); 
        BufferedImage bytes = ImageIO.read(new File(pathToFullImgs + filename));
        
        ret.setJson(response);
        ret.setOnePhoto(convertBufferImageToByteArray(bytes, getExtensionFomFilename(filename)));
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
        
        Document newUser = new Document()
                            .append("username", username)
                            .append("password", password);
    
        recommender.initUserByUsername(username); // Recommender init user
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

        ImgType typeImg = ImgType.valueOf(req.get("typeImage").textValue());
        String username = req.get("username").textValue();

        // get the most relevant post from Recommender
        ArrayList<Document> receivedDoc = getTheMostRevelantPost(username);
        
        if (receivedDoc.isEmpty()) {
            response.put("status", STATUS.ERROR.toString());
            ret.setJson(response);
            return ret;
        } 
        Document post = receivedDoc.get(0);
        String usernamePost = post.getString("username");
        ArrayList<String> photos_names = (ArrayList<String>)post.get("arrayPhotos");
        ArrayList<String> commentsPost = (ArrayList<String>)post.get("arrayComments");
        // ArrayList<String> tagsPost = (ArrayList<String>)post.get("tags");

        Integer likes = post.getInteger("likes");
        ObjectId id = post.getObjectId("_id");

        ArrayList<byte[]> images = new ArrayList<>();
        String path = (typeImg == ImgType.FULL) ? pathToFullImgs : pathToScaledImgs;
        
        for (String fname: photos_names) {
            BufferedImage tmp = ImageIO.read(new File(path + fname));
            String ext = getExtensionFomFilename(fname);
            byte[] b = convertBufferImageToByteArray(tmp, ext);
            images.add(b);
        }
        response.put("username",usernamePost);
        response.put("likes", likes);
        response.put("id", id.toString());
        ArrayNode arrNode = response.putArray("comments");
        for (var item: commentsPost) 
            arrNode.add(item);
        

        ret.setJson(response);
        ret.setManyPhotos(images);
        return ret;
    }

    private ArrayList<Document> getTheMostRevelantPost(String username) {
        /*
         * Try to return the post among top-three (???) tags. If it fails, 
         * any post will be returned, since there is no suitable posts in Database
         */
        Integer top = 3;
        List<String> tags = recommender.getTopNTagsByUsername(username, top);
        Document query = new Document("tags", new Document("$in", tags));
        MongoCollection<Document> posts = db.getCollection("posts");
        ArrayList<Document> receivedDocs = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        posts.find(query)
            .subscribe(new DocumentSubscriber<Document>(latch, receivedDocs));
        
        try {
            latch.await();
        } catch (Exception e) {}

        return receivedDocs;
    }

    private JsonSerializable setMainPost(ObjectNode req, ArrayList<byte[]> byteArrays)
     throws IOException, NoSuchAlgorithmException {
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
        response.put("type", RESPONSE_TYPE.SET_MAIN_POST_BACK.toString());
        response.put("status", STATUS.OK.toString());

        MongoCollection<Document> posts = db.getCollection("posts");
        
        String username = req.get("username").textValue();
        ArrayNode arrayOfExtensions = req.withArrayProperty("extensionOfImage");
        ArrayNode tagsPostNode = req.withArrayProperty("tags");
        ArrayList<String> tagsPostArray = new ArrayList<>();
        for (int i = 0; i < tagsPostNode.size(); ++i) 
            tagsPostArray.add(tagsPostNode.get(i).textValue());

        ArrayList<BufferedImage> images = new ArrayList<>();
        for (byte[] byteArray: byteArrays)
            images.add(ImageIO.read(new ByteArrayInputStream(byteArray)));

        // Writing FULL and SCALED vs on FS
        ArrayList<String> namesPhotos = new ArrayList<>();
        for (int i = 0; i < byteArrays.size(); ++i) {
            String ext = arrayOfExtensions.get(i).textValue();
            String filename = saveImage(images.get(i), ext, ImgType.FULL);
            saveImage(images.get(i), ext, ImgType.SCALED);
            namesPhotos.add(filename);
        }

        Document newPost = new Document()
                        .append("username", username)
                        .append("arrayPhotos", namesPhotos)
                        .append("arrayComments", new ArrayList<String>())
                        .append("tags", tagsPostArray)
                        .append("likes", 0);
        posts.insertOne(newPost)
            .subscribe(new InsertSubscriber<InsertOneResult>());
        ret.setJson(response);
        return ret;
    }

    public static BufferedImage resizeImage(
    BufferedImage originalImage, int width, int height) {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return resizedImage;
    }

    private String saveImage(BufferedImage bufImage, String ext, ImgType type)
    throws NoSuchAlgorithmException {
        String filename = "photo_" + hashFromTime() + "." + ext;
        String path = ((type == ImgType.FULL) ? pathToFullImgs : pathToScaledImgs) + filename; 
        writeImageIntoFS(path, bufImage, ext);
        return filename;
    }

    private boolean writeImageIntoFS(String path, BufferedImage bufImage, String ext) {
        boolean status = true;
        try {
            ImageIO.write(bufImage, ext, new File(path));
        } catch (IOException e) {
            status = false;
        }
        return status;
    }

    private String hashFromTime() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(String.valueOf(System.currentTimeMillis()).getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }

    private JsonSerializable setLike(ObjectNode req) {
        /*
         * Increases counter of likes in post 
         * Increases stat in recommender system for user who liked the post
         */
        ObjectNode response = jsMapper.createObjectNode();
        JsonSerializable ret = new JsonSerializable();
        response.put("type", RESPONSE_TYPE.SET_LIKE_BACK.toString());
        response.put("status", STATUS.OK.toString());
        MongoCollection<Document> posts = db.getCollection("posts");

        CountDownLatch latch = new CountDownLatch(1);
        String username = req.get("username").textValue();
        REACTION reaction = REACTION.valueOf(req.get("reaction").textValue());
        ObjectId postId = new ObjectId(req.get("id").textValue());

        ArrayList<Document> receivedDoc = new ArrayList<>();
        // get tags from post
        posts.find(Filters.eq("_id", postId))
            .subscribe(new DocumentSubscriber<Document>(latch, receivedDoc));
        try {
            latch.await();
        } catch (Exception e) {
            System.err.println("EXCEPTION setLike");
        }
        Document doc = receivedDoc.get(0);
        ArrayList<String> tagsPost = (ArrayList<String>)doc.get("tags");
        int delta = (reaction == REACTION.LIKE) ? 1 : -1;
        Integer counterLikes = (Integer) doc.get("likes") + delta;

        //update document
        posts.updateOne(
            eq("_id", postId),
            set("likes", counterLikes))
        .subscribe(new SubscriberHelper.PrintSubscriber<>(new CountDownLatch(0)));

        // We don't wait here 
        recommender.updateStatByUsername(username, tagsPost, reaction); // Update stat in Recommender for user

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
    public byte[] convertBufferImageToByteArray(BufferedImage bufferedImage, String ext) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, ext, baos);
        return baos.toByteArray();
    }

    public String getExtensionFomFilename(String fname) {
        String[] tmp = fname.split("\\.");
        return tmp[tmp.length-1];
    }
}