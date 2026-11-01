package com.artificialintelligence.patterns.apigateway;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-window rate limiter keyed by client id (header X-Client-Id, else "anonymous").
 */
public final class RateLimitingFilter implements GatewayFilter {

    private final int limit;
    private final long windowMs;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitingFilter(int limit, long windowMs) {
        this.limit = limit;
        this.windowMs = windowMs;
    }

    public RateLimitingFilter(int limit) {
        this(limit, 60_000L);
    }

    @Override
    public HttpResponse filter(HttpRequest request, GatewayFilterChain chain) {
        String clientId = request.header("X-Client-Id");
        if (clientId == null || clientId.isBlank()) clientId = "anonymous";
        Window window = windows.compute(clientId, (k, v) -> {
            long now = System.currentTimeMillis();
            if (v == null || now - v.start >= windowMs) {
                return new Window(now, new AtomicInteger(0));
            }
            return v;
        });
        int count = window.count.incrementAndGet();
        if (count > limit) {
            return HttpResponse.tooManyRequests("{\"error\":\"rate limit exceeded for " + clientId + "\"}");
        }
        return chain.proceed(request);
    }

    private static final class Window {
        final long start;
        final AtomicInteger count;

        Window(long start, AtomicInteger count) {
            this.start = start;
            this.count = count;
        }
    }
}
