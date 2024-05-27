package frameforge.server;

import java.util.concurrent.CountDownLatch;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.*;

public final class SubscriberHelper {
    
    public static class CountSubscriber<T> 
    implements Subscriber<T> {
        private int[] count;
        private volatile Subscription subscription;
        private final CountDownLatch latch;

        public CountSubscriber(CountDownLatch latch, int []count) {
            this.latch = latch;
            this.count = count;
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
            subscription.request(1);
            System.out.println("Subscribed");
        }

        @Override
        public void onNext(final T t) {
            count[0]++;
            subscription.request(1);
            System.out.println("Count++");
        }

        @Override
        public void onError(final Throwable t) {
            System.out.println("Error: " + t.getMessage());
            onComplete();
        }

        @Override
        public void onComplete() {
            latch.countDown();
            System.out.println("Completed");
        }
    }

    public static class PrintSubscriber<T> 
    implements Subscriber<T> {
        private volatile Subscription subscription;
        private final CountDownLatch latch;

        public PrintSubscriber(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
            subscription.request(1);
            System.out.println("Subscribe");
        }

        @Override
        public void onNext(final T t) {
            subscription.request(1);
            System.out.println("Inserted");
        }

        @Override
        public void onError(final Throwable t) {
            System.out.println("Error: " + t.getMessage());
            onComplete();
        }

        @Override
        public void onComplete() {
            latch.countDown();
            System.out.println("Completed");
        }
    }

    public static class DocumentSubscriber<Document>
    implements Subscriber<Document> {
        private final ArrayList<Document> receivedDocs;
        private volatile Subscription subscription;
        private final CountDownLatch latch;

        public DocumentSubscriber(CountDownLatch latch, ArrayList<Document> arr) {
            this.receivedDocs = arr;
            this.latch = latch;
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
            subscription.request(1);
        }

        @Override
        public void onNext(final Document t) {
            receivedDocs.add(t);
            System.out.println("adding doc");
            subscription.request(1);
        }

        @Override
        public void onError(final Throwable t) {
            System.out.println("Error: " + t.getMessage());
            onComplete();
        }

        @Override
        public void onComplete() {
            latch.countDown();
        }
    }

    public static class InsertSubscriber<T>
    implements Subscriber<T> {
        private volatile Subscription subscription;

        public InsertSubscriber() {
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
            subscription.request(1);
        }

        @Override
        public void onNext(final T t) {
            subscription.request(1);
        }

        @Override
        public void onError(final Throwable t) {
            System.out.println("Error: " + t.getMessage());
            onComplete();
        }

        @Override
        public void onComplete() {
        }
    }
}
