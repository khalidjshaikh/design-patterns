package com.artificialintelligence.patterns.bulkhead;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolBulkheadTest {

    @Test
    void executeSupplierRunsOnPool() {
        ThreadPoolBulkhead bh = new ThreadPoolBulkhead("tp", BulkheadConfig.builder()
                .coreThreadPoolSize(1).maxThreadPoolSize(1).queueCapacity(1).build());
        try {
            String r = bh.executeSupplier(() -> "ok");
            assertEquals("ok", r);
        } finally {
            bh.shutdown();
        }
    }

    @Test
    void rejectsWhenSaturated() throws InterruptedException {
        ThreadPoolBulkhead bh = new ThreadPoolBulkhead("tp", BulkheadConfig.builder()
                .coreThreadPoolSize(1).maxThreadPoolSize(1).queueCapacity(0).maxWaitDuration(java.time.Duration.ZERO).build());
        CountDownLatch held = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        Thread submitter = new Thread(() -> {
            try {
                bh.executeRunnable(() -> {
                    try { held.countDown(); release.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            } catch (Exception ignored) { }
        });
        submitter.start();
        held.await();

        assertThrows(BulkheadFullException.class, () -> bh.executeSupplier(() -> "x"));

        release.countDown();
        submitter.join();
        bh.shutdown();
        bh.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    void propagatesRuntimeException() {
        ThreadPoolBulkhead bh = new ThreadPoolBulkhead("tp");
        try {
            assertThrows(IllegalStateException.class, () -> bh.executeSupplier(() -> { throw new IllegalStateException("boom"); }));
        } finally {
            bh.shutdown();
        }
    }
}
