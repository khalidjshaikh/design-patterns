package com.artificialintelligence.circuitbreaker;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerTest {

    @Test
    void startsClosedAndAllowsCalls() {
        CircuitBreaker cb = new CircuitBreaker(2, 10_000L);
        assertEquals(CircuitBreaker.State.CLOSED, cb.state());
        String r = cb.call(() -> "ok", () -> "fallback");
        assertEquals("ok", r);
        assertEquals(1, cb.getNumberOfSuccessfulCalls());
    }

    @Test
    void opensAfterThreshold() {
        CircuitBreaker cb = new CircuitBreaker(2, 10_000L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        assertEquals(CircuitBreaker.State.OPEN, cb.state());
    }

    @Test
    void openRejectsCall() {
        CircuitBreaker cb = new CircuitBreaker(1, 10_000L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        boolean[] invoked = {false};
        String r = cb.call(() -> { invoked[0] = true; return "ok"; }, () -> "fallback");
        assertEquals("fallback", r);
        assertFalse(invoked[0]);
        assertEquals(1, cb.getNumberOfNotPermittedCalls());
    }

    @Test
    void executeSupplierThrowsWhenOpen() {
        CircuitBreaker cb = new CircuitBreaker(1, 10_000L);
        try { cb.executeSupplier(() -> { throw new IllegalStateException("boom"); }); } catch (IllegalStateException ignored) { }
        assertThrows(CircuitBreakerOpenException.class, () -> cb.executeSupplier(() -> "x"));
    }

    @Test
    void transitionsToHalfOpenAfterWait() throws InterruptedException {
        CircuitBreaker cb = new CircuitBreaker(1, 50L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        assertEquals(CircuitBreaker.State.OPEN, cb.state());
        Thread.sleep(80);
        assertEquals(CircuitBreaker.State.HALF_OPEN, cb.state());
    }

    @Test
    void halfOpenClosesOnSuccess() throws InterruptedException {
        CircuitBreaker cb = new CircuitBreaker(1, 50L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        Thread.sleep(80);
        String r = cb.call(() -> "ok", () -> "fb");
        assertEquals("ok", r);
        assertEquals(CircuitBreaker.State.CLOSED, cb.state());
    }

    @Test
    void halfOpenReopensOnFailure() throws InterruptedException {
        CircuitBreaker cb = new CircuitBreaker(1, 50L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        Thread.sleep(80);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        assertEquals(CircuitBreaker.State.OPEN, cb.state());
    }

    @Test
    void resetCloses() {
        CircuitBreaker cb = new CircuitBreaker(1, 10_000L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        assertEquals(CircuitBreaker.State.OPEN, cb.state());
        cb.reset();
        assertEquals(CircuitBreaker.State.CLOSED, cb.state());
    }

    @Test
    void releasesPermissionOnException() {
        CircuitBreaker cb = new CircuitBreaker(2, 10_000L);
        assertThrows(IllegalStateException.class, () -> cb.executeSupplier(() -> { throw new IllegalStateException("boom"); }));
        // should still allow calls
        String r = cb.call(() -> "ok", () -> "fb");
        assertEquals("ok", r);
    }

    @Test
    void metricsFailureRate() {
        CircuitBreaker cb = new CircuitBreaker(5, 10_000L);
        cb.call(() -> "ok", () -> "fb");
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> "fb");
        CircuitBreakerMetrics m = cb.getMetrics();
        assertEquals(2, m.getNumberOfCalls());
        assertEquals(1, m.getNumberOfSuccessfulCalls());
        assertEquals(1, m.getNumberOfFailedCalls());
        assertEquals(50.0, m.getFailureRate());
    }

    @Test
    void registryGetOrCreate() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker a = registry.circuitBreaker("cb1");
        CircuitBreaker b = registry.circuitBreaker("cb1");
        assertSame(a, b);
        assertEquals(1, registry.size());
        assertNotNull(registry.get("cb1"));
    }

    @Test
    void configValidation() {
        assertThrows(IllegalArgumentException.class, () -> CircuitBreakerConfig.builder().failureThreshold(0).build());
        assertThrows(IllegalArgumentException.class, () -> new CircuitBreaker(0, 100));
    }

    @Test
    void aliasClassesWork() {
        DefaultCircuitBreaker d = new DefaultCircuitBreaker(1, 100);
        CountBasedCircuitBreaker c = new CountBasedCircuitBreaker(1, 100);
        assertNotNull(d);
        assertNotNull(c);
    }
}