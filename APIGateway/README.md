# API Gateway Pattern (Java)

This module demonstrates the **API Gateway** design pattern: a single entry point that routes
client requests to downstream services while applying cross-cutting concerns.

## Components

- `HttpRequest` / `HttpResponse` — immutable request/response models.
- `DownstreamService` — abstraction for a backend service.
- `ServiceRegistry` — in-memory registry of downstream services.
- `Router` — longest-prefix path routing.
- `ApiGateway` — orchestrates the filter chain and routing/aggregation.
- `GatewayFilter` / `GatewayFilterChain` — filter chain (Chain of Responsibility).
- Filters: `LoggingFilter`, `AuthenticationFilter`, `RateLimitingFilter`.
- `DashboardAggregator` — composes multiple service responses.
- `LoadBalancedService` — round-robin load balancing across instances.
- `CircuitBreaker` — protects against cascading failures.
- Mock services: `UserService`, `OrderService`, `ProductService`, `HealthService`, `FlakyService`.

## Scripts

```bash
./scripts/build.sh
./scripts/test.sh
./scripts/run.sh
```
