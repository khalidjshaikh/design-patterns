# Bulkhead Pattern (Java)

A small, dependency-free Java implementation of the **Bulkhead** resilience pattern.

## Modules

- `SemaphoreBulkhead` – limits concurrent calls with a `Semaphore`.
- `ThreadPoolBulkhead` – isolates calls in a bounded thread pool.
- `BulkheadConfig` – configuration (max concurrent calls, wait duration, pool sizes, queue capacity).
- `BulkheadRegistry` – registry for named bulkheads.

## Build & Test

```bash
mvn test
```

## Run Demo

```bash
mvn exec:java
```

## Usage

```java
BulkheadConfig config = BulkheadConfig.builder()
        .maxConcurrentCalls(5)
        .maxWaitDuration(java.time.Duration.ofMillis(100))
        .build();

Bulkhead bulkhead = new SemaphoreBulkhead("payment-service", config);

String result = bulkhead.executeSupplier(() -> callRemoteService());
```
