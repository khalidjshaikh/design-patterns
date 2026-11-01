package com.artificialintelligence.servicediscovery.example;

import com.artificialintelligence.servicediscovery.client.ServiceDiscoveryClient;
import com.artificialintelligence.servicediscovery.loadbalancer.RoundRobinLoadBalancer;
import com.artificialintelligence.servicediscovery.model.ServiceInstance;
import com.artificialintelligence.servicediscovery.registry.InMemoryServiceRegistry;
import com.artificialintelligence.servicediscovery.registry.ServiceRegistry;

import java.util.List;

public class ServiceDiscoveryDemo {

    public static void main(String[] args) throws InterruptedException {
        ServiceRegistry registry = new InMemoryServiceRegistry(5_000L);

        // Two instances of "order-service" register themselves.
        try (ServiceDiscoveryClient c1 = new ServiceDiscoveryClient(registry, "order-1", "order-service", "10.0.0.1", 8081);
             ServiceDiscoveryClient c2 = new ServiceDiscoveryClient(registry, "order-2", "order-service", "10.0.0.2", 8082)) {

            c1.register();
            c2.register();

            System.out.println("Registered services: " + registry.serviceNames());
            List<ServiceInstance> found = registry.discover("order-service");
            System.out.println("Discovered " + found.size() + " healthy instance(s):");
            for (ServiceInstance i : found) {
                System.out.println("  " + i);
            }

            RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer();
            System.out.println("\nRound-robin selection:");
            for (int i = 0; i < 4; i++) {
                ServiceInstance chosen = lb.choose(found);
                System.out.println("  request " + (i + 1) + " -> " + chosen.getUrl());
            }

            // Simulate one instance going down (no heartbeat).
            System.out.println("\nStopping heartbeats for order-2 ...");
            // deregister explicitly to simulate shutdown
            c2.deregister();
            System.out.println("After deregistration, healthy instances: " + registry.discover("order-service").size());

            Thread.sleep(1000);
        }

        registry.shutdown();
        System.out.println("Demo finished.");
    }
}
