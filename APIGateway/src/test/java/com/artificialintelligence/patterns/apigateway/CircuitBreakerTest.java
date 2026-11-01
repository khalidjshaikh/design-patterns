package com.artificialintelligence.patterns.apigateway;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircuitBreakerTest {

    @Test
    void opensAfterThreshold() {
        CircuitBreaker cb = new CircuitBreaker(2, 10_000L);
        Supplier<HttpResponse> failing = () -> { throw new IllegalStateException("boom"); };
        Supplier<HttpResponse> fallback = () -> HttpResponse.serviceUnavailable("fb");
        cb.call(failing, fallback);
        cb.call(failing, fallback);
        assertEquals(CircuitBreaker.State.OPEN, cb.state());
        // while open, action not invoked
        boolean[] invoked = {false};
        Supplier<HttpResponse> action = () -> { invoked[0] = true; return HttpResponse.ok("ok"); };
        HttpResponse r = cb.call(action, fallback);
        assertEquals(503, r.statusCode());
        assertTrue(!invoked[0]);
    }

    @Test
    void resetsOnSuccess() {
        CircuitBreaker cb = new CircuitBreaker(2, 10_000L);
        cb.call(() -> { throw new IllegalStateException("boom"); }, () -> HttpResponse.serviceUnavailable("fb"));
        cb.call(() -> HttpResponse.ok("ok"), () -> HttpResponse.serviceUnavailable("fb"));
        assertEquals(CircuitBreaker.State.CLOSED, cb.state());
    }
}
