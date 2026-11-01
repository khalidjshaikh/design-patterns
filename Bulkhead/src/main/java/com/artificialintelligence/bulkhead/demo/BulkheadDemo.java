package com.artificialintelligence.bulkhead.demo;

import com.artificialintelligence.bulkhead.Bulkhead;
import com.artificialintelligence.bulkhead.BulkheadConfig;
import com.artificialintelligence.bulkhead.BulkheadFullException;
import com.artificialintelligence.bulkhead.SemaphoreBulkhead;
import com.artificialintelligence.bulkhead.ThreadPoolBulkhead;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class BulkheadDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Semaphore Bulkhead Demo ===");
        runSemaphoreDemo();

        System.out.println();
        System.out.println("=== Thread Pool Bulkhead Demo ===");
        runThreadPoolDemo();
    }

    private static void runSemaphoreDemo() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.builder()
                .maxConcurrentCalls(2)
                .maxWaitDuration(Duration.ofMillis(50))
                .build();
        Bulkhead bulkhead = new SemaphoreBulkhead("sem-demo", config);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(4);
        AtomicInteger rejected = new AtomicInteger(0);
        AtomicInteger completed = new AtomicInteger(0);

        for (int i = 0; i < 4; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    start.await();
                    bulkhead.executeRunnable(() -> {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("  task " + id + " completed");
                    });
                    completed.incrementAndGet();
                } catch (BulkheadFullException e) {
                    rejected.incrementAndGet();
                    System.out.println("  task " + id + " rejected: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        start.countDown();
        try { done.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println("completed=" + completed.get() + " rejected=" + rejected.get());
    }

    private static void runThreadPoolDemo() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.builder()
                .coreThreadPoolSize(2)
                .maxThreadPoolSize(2)
                .queueCapacity(1)
                .build();
        ThreadPoolBulkhead bulkhead = new ThreadPoolBulkhead("tp-demo", config);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(4);
        AtomicInteger rejected = new AtomicInteger(0);
        AtomicInteger completed = new AtomicInteger(0);

        for (int i = 0; i < 4; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    start.await();
                    bulkhead.executeRunnable(() -> {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("  task " + id + " completed");
                    });
                    completed.incrementAndGet();
                } catch (BulkheadFullException e) {
                    rejected.incrementAndGet();
                    System.out.println("  task " + id + " rejected: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        start.countDown();
        try { done.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        bulkhead.shutdown();
        bulkhead.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("completed=" + completed.get() + " rejected=" + rejected.get());
    }
}
