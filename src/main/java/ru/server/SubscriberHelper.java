package ru.server;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.mongodb.MongoTimeoutException;

import java.util.*;
import java.util.concurrent.atomic.*;

public final class SubscriberHelper {
    
    public static class OperationSubscriber<T> 
    implements Subscriber<T> {
        private final List<T> receivedDocs;
        private volatile Subscription subscription;
        private final List<Throwable> errors;
        private final CountDownLatch latch;
        private volatile boolean isCompleted;

        OperationSubscriber() {
            this.receivedDocs = new ArrayList<T>();
            this.latch = new CountDownLatch(1);
            this.errors = new ArrayList<Throwable>();
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
            subscription.request(1);
        }

        @Override
        public void onNext(final T t) {
            receivedDocs.add(t);
            System.out.println("Inserted." + ", size: " + receivedDocs.size());
        }

        @Override
        public void onError(final Throwable t) {
            errors.add(t);
            System.out.println("Error: " + t.getMessage());
            onComplete();
        }

        @Override
        public void onComplete() {
            isCompleted = true;
            latch.countDown();
            System.out.println("Completed," + " size: " + receivedDocs.size());
        }

        public List<T> getReceived() {
            return receivedDocs;
        }

        public List<T> get(final long timeout, final TimeUnit unit) throws Throwable {
            return await(timeout, unit).getReceived();
        }

        public OperationSubscriber<T> await() throws Throwable {
            return await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

         public OperationSubscriber<T> await(final long timeout, final TimeUnit unit) throws Throwable {
            subscription.request(Integer.MAX_VALUE);
            if (!latch.await(timeout, unit)) {
                throw new MongoTimeoutException("Publisher onComplete timed out");
            }
            if (!errors.isEmpty()) {
                throw errors.get(0);
            }
            return this;
        }
    }

    public static class PrintSubscriber<T> 
    extends OperationSubscriber {

    }
}
