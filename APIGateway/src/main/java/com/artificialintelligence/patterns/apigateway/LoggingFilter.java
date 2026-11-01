package com.artificialintelligence.patterns.apigateway;

/**
 * Logs each request and its resulting status code / latency.
 */
public final class LoggingFilter implements GatewayFilter {

    @Override
    public HttpResponse filter(HttpRequest request, GatewayFilterChain chain) {
        long start = System.nanoTime();
        HttpResponse response = chain.proceed(request);
        long ms = (System.nanoTime() - start) / 1_000_000L;
        System.out.println("[gateway] " + request.method() + " " + request.path()
                + " -> " + response.statusCode() + " (" + ms + " ms)");
        return response;
    }
}
