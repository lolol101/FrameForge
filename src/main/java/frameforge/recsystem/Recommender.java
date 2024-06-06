package frameforge.recsystem;

import java.util.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.concurrent.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.*;
import frameforge.server.Constants;
import frameforge.server.SubscriberHelper;
import frameforge.server.Constants.REACTION;
import frameforge.server.SubscriberHelper.*;

public final class Recommender {
    /*
     * Singleton
     * MongoDB: statistics (Collection)
     */
    private static Recommender instance = null;

    private MongoDatabase db = null;
    private static double initValue = 1. / Constants.TAGS.size();
    private static double minBoarder = 1. / (Constants.TAGS.size() * 2);

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
        List<Double> likes = new ArrayList<>();
        for (int i = 0; i < Constants.TAGS.size(); ++i)
            stat.add(initValue);
        for (int i = 0; i < Constants.TAGS.size(); ++i) 
            likes.add(0.0);
        Document newUser = new Document()
                                .append("username", username)
                                .append("stat", stat)
                                .append("likes", likes);
        stats.insertOne(newUser)
            .subscribe(new InsertSubscriber<InsertOneResult>());
    }
    
    public void updateStatByUsername(String username, List<String> tags, REACTION reaction) {
        /*
         * Username set like -> stat should be updated
         */
        //get array
        ArrayList<Double> statArray = getArrayByUsername(username, "stat");
        ArrayList<Double> likes = getArrayByUsername(username, "likes");
        
        // apply algorithm
        changeWeightsInplace(statArray, likes, tags, reaction);

        // write array into bd (changing)
        Bson filter = Filters.eq("username", username);
        MongoCollection<Document> stats = db.getCollection("statistics");
        stats.updateOne(filter, Updates.set("stat", statArray))
            .subscribe(new SubscriberHelper.PrintSubscriber<>(new CountDownLatch(0)));
        // We don't wait here, callback is unuseful
    }

    private void changeWeightsInplace(ArrayList<Double> statArray,
     ArrayList<Double> likes, List<String> tags, REACTION reaction) {
        Double sum = 1.0;
        for (String tag: tags) {
            int index = getIndexByTag(tag);
            int delta = (reaction == REACTION.LIKE) ? 1 : -1;
            likes.set(index, likes.get(index) + delta);
        }
        for (int i = 0; i < likes.size(); ++i) {
            sum += likes.get(i);
        }

        int maxIndex = 0;
        for (int i = 0; i < statArray.size(); ++i) {
            statArray.set(i, likes.get(i) / sum);
            if (statArray.get(i) > statArray.get(maxIndex)) {
                maxIndex = i;
            }
        }
        for (int i = 0; i < statArray.size(); ++i) {
            if (statArray.get(i) < minBoarder) {
                Double delta = minBoarder - statArray.get(i);
                statArray.set(i, statArray.get(i) + delta);
                statArray.set(maxIndex, statArray.get(maxIndex) - delta);
            }
        }
    }

    public List<String> getTopNTagsByUsername(String username, int N) {
        /*
         * get the most relevant tags for Username
         * N - randomized N tags with respect of probabilities
         */
        // get array
        ArrayList<Double> statArray = getArrayByUsername(username, "stat");
        Set<String> mset = new HashSet<>();

        for (int i = 0; i < N; i++) {
            Double p = Math.random();
 
            Double sum = 0.0;
            for (int j = 0; j < statArray.size(); ++j) {
                if (sum + statArray.get(j) > p) {
                    mset.add(getTagByIndex(j));
                    break;
                }
                sum += statArray.get(i);
            }
        }
        return new ArrayList<>(mset);
    }

    private int getIndexByTag(String tag) {
        return Constants.tagToIndex.get(tag);
    }

    private String getTagByIndex(int index) {
        return Constants.indexToTag.get(index);
    }

    private ArrayList<Double> getArrayByUsername(String username, String field) {
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
        return (ArrayList<Double>) receivedDoc.get(0).get(field);
    }
}
