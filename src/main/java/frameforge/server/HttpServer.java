package frameforge.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import org.bson.Document;
import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.*;

import frameforge.recsystem.Recommender;
import frameforge.server.*;
import frameforge.server.SubscriberHelper.PrintSubscriber;

import com.mongodb.client.model.*;


public class HttpServer {

    private static final int NUM_THREADS = 4;
    private static int port = 8080;
    private ServerSocket serverSocket;
    private ConnectionString connString = new ConnectionString("mongodb://localhost:27017");
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static Recommender recommender;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.mongoInit();
        server.connToDB("project");
        //server.configureDB();
        HttpHandler.db = mongoDatabase;
        recommender = Recommender.getInstance(mongoDatabase);
        HttpHandler.recommender = recommender;
        server.start();
    }

    HttpServer() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        
    }

    public void mongoInit() {
        mongoClient = MongoClients.create(connString);
    }

    public static void connToDB(String DbName) {
        mongoDatabase = mongoClient.getDatabase(DbName);
    }

    public void configureDB() {
        mongoDatabase.createCollection("users");
        mongoDatabase.createCollection("posts");
        mongoDatabase.createCollection("albums");
        mongoDatabase.createCollection("stats");

        MongoCollection<Document> users = mongoDatabase.getCollection("users");
        IndexOptions options1 = new IndexOptions().unique(true);
        CountDownLatch latch = new CountDownLatch(1);
        users.createIndex(Indexes.ascending("username"), options1)
                        .subscribe(new PrintSubscriber<String>(latch));

        latch = new CountDownLatch(1);
        MongoCollection<Document> posts = mongoDatabase.getCollection("posts");
        posts.createIndex(Indexes.ascending("createdAt"))
                        .subscribe(new PrintSubscriber<String>(latch));
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("connection established");
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            
            HttpHandler connection = new HttpHandler(socket);
            pool.execute(connection);
        }
    }

    
}
