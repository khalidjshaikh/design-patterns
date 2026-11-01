# Design Patterns (Java)

A collection of classic and enterprise design/resilience patterns implemented in Java 27.

Each pattern lives in its own Maven module so it can be built and run independently, while the root aggregator POM builds all of them together.

## Modules

| Module | Pattern | Demo main class |
| --- | --- | --- |
| `APIGateway` | API Gateway | `com.artificialintelligence.patterns.apigateway.Demo` |
| `Bulkhead` | Bulkhead | `com.patterns.bulkhead.demo.BulkheadDemo` |
| `CircuitBreaker` | Circuit Breaker | `com.patterns.circuitbreaker.demo.CircuitBreakerDemo` |
| `LoadBalancing` | Load Balancing | `com.patterns.loadbalancing.demo.LoadBalancingDemo` |
| `MessagingPattern` | Enterprise Messaging | `com.artificialintelligence.patterns.messaging.Demo` |
| `Saga` | Saga (orchestration & choreography) | `com.patterns.saga.demo.SagaDemo` |
| `ServiceDiscovery` | Service Discovery | `com.artificialintelligence.servicediscovery.example.ServiceDiscoveryDemo` |
| `Singleton` | Singleton variants | `com.artificialintelligence.patterns.singleton.Demo` |

## Requirements

- Java 27 (or compatible JDK)
- Apache Maven 3.9+

## Build

Build all modules:

```bash
mvn clean install
```

Run every module demo in one pass:

```bash
mvn exec:java
```

Run a single module's demo:

```bash
cd Singleton
mvn exec:java
```

## Tests

Run all tests:

```bash
mvn test
```

## License

This project is provided as-is for educational purposes.
EOF
echo done