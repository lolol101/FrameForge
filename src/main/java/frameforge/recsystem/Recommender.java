package frameforge.recsystem;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.concurrent.*;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import frameforge.server.Constants;
import frameforge.server.SubscriberHelper.*;

public final class Recommender {
    /*
     * Singleton
     * MongoDB: statistics (Collection)
     */
    private static Recommender instance = null;

    private MongoDatabase db = null;
    private static double initValue = 1. / Constants.TAGS.size();

    private Recommender(MongoDatabase dbase) {
        db = dbase;
    }

    public static synchronized Recommender getInstance(MongoDatabase dbase) {
        if (instance == null) {
            instance = new Recommender(dbase);
        }
        return instance;
    }

    public void initUserByUsername(String username) {
        /*
         * It invokes in registration method to create new entity
         */
        MongoCollection<Document> stats = db.getCollection("statistics");

        List<Double> stat = new ArrayList<>();
        for (int i = 0; i < Constants.TAGS.size(); ++i)
            stat.add(initValue);
        Document newUser = new Document()
                                .append("username", username)
                                .append("stat", stat);
        stats.insertOne(newUser)
            .subscribe(new InsertSubscriber<InsertOneResult>());
    }
    
    public void updateStatByUsername(String username, List<String> tags) {
        /*
         * Username set like -> stat should be updated
         */
        //get array
        ArrayList<Double> statArray = getStatArrayByUsername(username);
        
        // apply algorithm
        changeWeightsInplace(statArray, tags);

        // write array into bd
        
    }

    private void changeWeightsInplace(ArrayList<Double> statArray, List<String> tags) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeWeightsInplace'");
    }

    public List<String> getTopNTagsByUsername(String username, int N) {
        /*
         * get the most relevant tags for Username
         * N - top N tags
         */
        // get array
        ArrayList<Double> statArray = getStatArrayByUsername(username);

        
        


        return new ArrayList<>();
    }

    private int getIndexByTag(String tag) {
        return Constants.tagToIndex.get(tag);
    }

    private String getTagByIndex(int index) {
        return Constants.indexToTag.get(index);
    }

    private ArrayList<Double> getStatArrayByUsername(String username) {
        Bson filter = Filters.eq("username", username);
        MongoCollection<Document> stats = db.getCollection("statistics");
        ArrayList<Document> receivedDoc = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        stats.find(filter)
            .subscribe(new DocumentSubscriber<Document>(latch, receivedDoc));
        try {
            latch.await();
        } catch (Exception e) {
            System.err.println("EXCEPTION getStatArrayByUsername");
            return new ArrayList<>();
        }
        return (ArrayList<Double>) receivedDoc.get(0).get("stat");
    }
}
