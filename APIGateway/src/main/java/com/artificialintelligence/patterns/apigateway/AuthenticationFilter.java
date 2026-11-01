package com.artificialintelligence.patterns.apigateway;

import java.util.Set;

/**
 * Requires a valid bearer token for protected paths. Public paths (health, etc.) bypass auth.
 */
public final class AuthenticationFilter implements GatewayFilter {

    public static final String VALID_TOKEN = "token123";

    private final Set<String> publicPrefixes;

    public AuthenticationFilter() {
        this(Set.of("/health", "/public"));
    }

    public AuthenticationFilter(Set<String> publicPrefixes) {
        this.publicPrefixes = publicPrefixes;
    }

    @Override
    public HttpResponse filter(HttpRequest request, GatewayFilterChain chain) {
        if (isPublic(request.path())) {
            return chain.proceed(request);
        }
        String auth = request.header("Authorization");
        if (auth == null || !auth.equals("Bearer " + VALID_TOKEN)) {
            return HttpResponse.unauthorized("{\"error\":\"missing or invalid bearer token\"}");
        }
        return chain.proceed(request);
    }

    private boolean isPublic(String path) {
        for (String prefix : publicPrefixes) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }
}
