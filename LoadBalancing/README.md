# Load Balancing Pattern

A Java implementation of the **Load Balancing** pattern.

Supports multiple strategies:

- Round Robin
- Weighted Round Robin
- Least Connections
- Random
- IP Hash
- Consistent Hash

## Build & Run

```bash
mvn clean test
mvn exec:java
```

## Structure

```
src/main/java/com/patterns/loadbalancing/
  Server.java
  LoadBalancer.java
  RoundRobinLoadBalancer.java
  WeightedRoundRobinLoadBalancer.java
  LeastConnectionsLoadBalancer.java
  RandomLoadBalancer.java
  IpHashLoadBalancer.java
  ConsistentHashLoadBalancer.java
  LoadBalancerFactory.java
  LoadBalancedService.java
  demo/LoadBalancingDemo.java
```