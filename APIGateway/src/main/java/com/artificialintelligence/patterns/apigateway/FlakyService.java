package com.artificialintelligence.patterns.apigateway;

/**
 * A downstream service that fails the first few calls, used to demonstrate the circuit breaker.
 */
public final class FlakyService implements DownstreamService {

    private int calls = 0;
    private final int failuresBeforeSuccess;

    public FlakyService(int failuresBeforeSuccess) {
        this.failuresBeforeSuccess = failuresBeforeSuccess;
    }

    @Override
    public String name() {
        return "flaky-service";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        calls++;
        if (calls <= failuresBeforeSuccess) {
            throw new IllegalStateException("flaky service unavailable (call " + calls + ")");
        }
        return HttpResponse.ok("{\"service\":\"flaky-service\",\"call\":" + calls + "}");
    }

    public void reset() {
        calls = 0;
    }
}
