package com.artificialintelligence.patterns.apigateway;

/**
 * Mock user service. Returns a user payload based on the trailing path segment.
 */
public final class UserService implements DownstreamService {

    @Override
    public String name() {
        return "user-service";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String id = lastSegment(request.path());
        return HttpResponse.ok("{\"service\":\"user-service\",\"id\":\"" + id + "\",\"name\":\"Alice\"}");
    }

    private static String lastSegment(String path) {
        String trimmed = path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
        int idx = trimmed.lastIndexOf('/');
        return idx >= 0 ? trimmed.substring(idx + 1) : trimmed;
    }
}
