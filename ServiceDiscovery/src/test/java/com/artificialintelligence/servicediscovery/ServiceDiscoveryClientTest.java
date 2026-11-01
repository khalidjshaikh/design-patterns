package com.artificialintelligence.servicediscovery;

import com.artificialintelligence.servicediscovery.client.ServiceDiscoveryClient;
import com.artificialintelligence.servicediscovery.loadbalancer.RoundRobinLoadBalancer;
import com.artificialintelligence.servicediscovery.model.ServiceInstance;
import com.artificialintelligence.servicediscovery.registry.InMemoryServiceRegistry;
import com.artificialintelligence.servicediscovery.registry.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDiscoveryClientTest {

    private ServiceRegistry registry;

    @AfterEach
    void tearDown() {
        // registry shutdown handled by clients close; ensure cleanup
    }

    @Test
    void clientRegistersAndDeregisters() {
        registry = new InMemoryServiceRegistry(10_000L);
        try (ServiceDiscoveryClient client = new ServiceDiscoveryClient(registry, "svc-1", "svc", "localhost", 9000, 100_000L)) {
            client.register();
            assertEquals(1, registry.discover("svc").size());
        }
        assertTrue(registry.discover("svc").isEmpty());
        registry.shutdown();
    }

    @Test
    void clientDiscoversOtherServices() {
        registry = new InMemoryServiceRegistry(10_000L);
        try (ServiceDiscoveryClient provider = new ServiceDiscoveryClient(registry, "p1", "provider", "localhost", 9001, 100_000L)) {
            provider.register();
            try (ServiceDiscoveryClient consumer = new ServiceDiscoveryClient(registry, "c1", "consumer", "localhost", 9002, 100_000L)) {
                consumer.register();
                List<ServiceInstance> found = consumer.discover("provider");
                assertEquals(1, found.size());
                ServiceInstance chosen = consumer.choose("provider", new RoundRobinLoadBalancer());
                assertNotNull(chosen);
                assertEquals("p1", chosen.getServiceId());
            }
        }
        registry.shutdown();
    }
}
