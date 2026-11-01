# Service Discovery Pattern

A Java implementation of the **Service Discovery** design pattern.

Services register themselves with a central registry and clients query the registry to find available (healthy) instances.

## Features

- Thread-safe in-memory service registry
- Heartbeat-based health tracking with configurable TTL
- Automatic eviction of stale instances
- Client-side service discovery
- Round-robin load balancing
- Scheduled heartbeat and health checking
- Demo application

## Build & Run

```bash
mvn clean test
mvn exec:java
```

## Structure

```
src/main/java/com/example/servicediscovery/
  model/ServiceInstance.java
  registry/ServiceRegistry.java
  registry/InMemoryServiceRegistry.java
  loadbalancer/LoadBalancer.java
  loadbalancer/RoundRobinLoadBalancer.java
  client/ServiceDiscoveryClient.java
  health/HealthChecker.java
  example/ServiceDiscoveryDemo.java
```
