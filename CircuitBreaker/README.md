# Circuit Breaker Pattern (Java)

A Java implementation of the **Circuit Breaker** resilience pattern.

The circuit breaker protects downstream calls by tracking failures. After a configurable
number of consecutive failures it *opens* and rejects calls (throwing
`CircuitBreakerOpenException` or invoking a fallback) until a wait duration elapses,
then moves to *half-open* to probe recovery.

## Features

- Three states: CLOSED, OPEN, HALF_OPEN
- Configurable failure threshold and open-state wait duration
- Configurable number of permitted trial calls in half-open state
- Fallback support via `call(action, fallback)`
- Exception-propagating `executeSupplier` / `executeRunnable`
- Metrics (successes, failures, not-permitted, failure rate)
- Thread-safe
- Registry for named circuit breakers
- Demo application

## Build & Run

```bash
mvn clean test
mvn exec:java
```

## Structure

```
src/main/java/com/patterns/circuitbreaker/
  CircuitBreaker.java
  CircuitBreakerConfig.java
  CircuitBreakerException.java
  CircuitBreakerOpenException.java
  CallNotPermittedException.java
  CircuitBreakerMetrics.java
  CircuitBreakerRegistry.java
  CircuitBreakerState.java
  DefaultCircuitBreaker.java
  ... (alias implementations)
  demo/CircuitBreakerDemo.java
```