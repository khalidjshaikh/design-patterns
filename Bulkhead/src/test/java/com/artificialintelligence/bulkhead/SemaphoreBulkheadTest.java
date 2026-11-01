package com.artificialintelligence.bulkhead;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SemaphoreBulkheadTest {

    @Test
    void allowsUpToMaxConcurrentCalls() {
        BulkheadConfig config = BulkheadConfig.builder().maxConcurrentCalls(2).build();
        SemaphoreBulkhead bh = new SemaphoreBulkhead("s", config);

        assertEquals(2, bh.getMaxAllowedConcurrentCalls());
        assertEquals(2, bh.getAvailableConcurrentCalls());

        assertTrue(bh.tryAcquirePermission());
        assertEquals(1, bh.getAvailableConcurrentCalls());
        bh.releasePermission();
        assertEquals(2, bh.getAvailableConcurrentCalls());
    }

    @Test
    void rejectsWhenFull() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.builder().maxConcurrentCalls(1).maxWaitDuration(Duration.ZERO).build();
        SemaphoreBulkhead bh = new SemaphoreBulkhead("s", config);

        assertTrue(bh.tryAcquirePermission());
        assertFalse(bh.tryAcquirePermission());

        bh.releasePermission();
        assertTrue(bh.tryAcquirePermission());
        bh.releasePermission();
    }

    @Test
    void executeSupplierReturnsResult() {
        SemaphoreBulkhead bh = new SemaphoreBulkhead("s");
        String result = bh.executeSupplier(() -> "ok");
        assertEquals("ok", result);
    }

    @Test
    void executeSupplierThrowsWhenFull() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.builder().maxConcurrentCalls(1).maxWaitDuration(Duration.ZERO).build();
        SemaphoreBulkhead bh = new SemaphoreBulkhead("s", config);

        CountDownLatch held = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        Thread t = new Thread(() -> bh.executeRunnable(() -> {
            try { held.countDown(); release.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }));
        t.start();
        held.await();

        assertThrows(BulkheadFullException.class, () -> bh.executeSupplier(() -> "x"));

        release.countDown();
        t.join();
    }

    @Test
    void releasesPermissionOnException() {
        SemaphoreBulkhead bh = new SemaphoreBulkhead("s", BulkheadConfig.builder().maxConcurrentCalls(1).build());
        assertThrows(IllegalStateException.class, () -> bh.executeSupplier(() -> { throw new IllegalStateException("boom"); }));
        assertEquals(1, bh.getAvailableConcurrentCalls());
    }
}
