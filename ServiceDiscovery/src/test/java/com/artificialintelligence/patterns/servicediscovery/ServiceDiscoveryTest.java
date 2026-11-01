package com.artificialintelligence.patterns.servicediscovery;

import com.artificialintelligence.patterns.servicediscovery.loadbalancer.RoundRobinLoadBalancer;
import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;
import com.artificialintelligence.patterns.servicediscovery.registry.InMemoryServiceRegistry;
import com.artificialintelligence.patterns.servicediscovery.registry.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDiscoveryTest {

    private ServiceRegistry registry;

    @AfterEach
    void tearDown() {
        if (registry != null) registry.shutdown();
    }

    @Test
    void registerAndDiscover() {
        registry = new InMemoryServiceRegistry(10_000L);
        ServiceInstance a = new ServiceInstance("a", "svc", "host", 8080);
        registry.register(a);
        List<ServiceInstance> found = registry.discover("svc");
        assertEquals(1, found.size());
        assertEquals("a", found.get(0).getServiceId());
    }

    @Test
    void deregisterRemovesInstance() {
        registry = new InMemoryServiceRegistry(10_000L);
        registry.register(new ServiceInstance("a", "svc", "host", 8080));
        registry.deregister("a");
        assertTrue(registry.discover("svc").isEmpty());
    }

    @Test
    void heartbeatKeepsInstanceHealthy() throws InterruptedException {
        registry = new InMemoryServiceRegistry(200L);
        registry.register(new ServiceInstance("a", "svc", "host", 8080));
        Thread.sleep(100);
        registry.heartbeat("a");
        Thread.sleep(100);
        assertEquals(1, registry.discover("svc").size());
    }

    @Test
    void staleInstanceBecomesUnhealthy() throws InterruptedException {
        registry = new InMemoryServiceRegistry(100L);
        registry.register(new ServiceInstance("a", "svc", "host", 8080));
        Thread.sleep(250);
        assertTrue(registry.discover("svc").isEmpty());
    }

    @Test
    void roundRobinCycles() {
        List<ServiceInstance> instances = List.of(
                new ServiceInstance("a", "svc", "h", 1),
                new ServiceInstance("b", "svc", "h", 2),
                new ServiceInstance("c", "svc", "h", 3));
        RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer();
        assertEquals("a", lb.choose(instances).getServiceId());
        assertEquals("b", lb.choose(instances).getServiceId());
        assertEquals("c", lb.choose(instances).getServiceId());
        assertEquals("a", lb.choose(instances).getServiceId());
    }

    @Test
    void chooseReturnsNullForEmpty() {
        RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer();
        assertNull(lb.choose(List.of()));
        assertNull(lb.choose(null));
    }
}
