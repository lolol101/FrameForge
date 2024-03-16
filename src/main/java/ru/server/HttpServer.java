//package ru.server;

import java.io.*;
import java.net.*;
import ru.server.*;
import java.util.concurrent.*;
import org.bson.Document;
import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.*;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Filters;
import ru.server.SubscriberHelper.PrintSubscriber;

public class HttpServer {

    private static final int NUM_THREADS = 4;
    private static int port = 8080;
    private ServerSocket serverSocket;
    private ConnectionString connString = new ConnectionString("mongodb://localhost:27017");
    private MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.mongoInit();
        server.connToDB("project");
        //server.configureDB();
        HttpHandler.db = mongoDatabase;
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

    public void connToDB(String DbName) {
        mongoDatabase = mongoClient.getDatabase(DbName);
    }

    public void configureDB() {
        mongoDatabase.createCollection("users");
        mongoDatabase.createCollection("posts");
        mongoDatabase.createCollection("albums");

        MongoCollection<Document> users = mongoDatabase.getCollection("users");
        IndexOptions options1 = new IndexOptions().unique(true);
        users.createIndex(Indexes.ascending("username"), options1)
                        .subscribe(new PrintSubscriber<String>());

        MongoCollection<Document> posts = mongoDatabase.getCollection("posts");
        posts.createIndex(Indexes.ascending("createdAt"))
                        .subscribe(new PrintSubscriber<String>());
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            
            HttpHandler connection = new HttpHandler(socket);
            pool.execute(connection);
        }
    }

    
}
