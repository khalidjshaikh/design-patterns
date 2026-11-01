package com.artificialintelligence.patterns.servicediscovery.health;

import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;
import com.artificialintelligence.patterns.servicediscovery.registry.ServiceRegistry;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodically checks registered instances and marks unhealthy ones.
 * Optionally removes instances that have been unhealthy for too long.
 */
public class HealthChecker implements AutoCloseable {

    private final ServiceRegistry registry;
    private final long checkIntervalMillis;
    private final boolean removeUnhealthy;
    private final ScheduledExecutorService executor;

    public HealthChecker(ServiceRegistry registry, long checkIntervalMillis, boolean removeUnhealthy) {
        if (registry == null) throw new IllegalArgumentException("registry must not be null");
        if (checkIntervalMillis <= 0) throw new IllegalArgumentException("checkIntervalMillis must be positive");
        this.registry = registry;
        this.checkIntervalMillis = checkIntervalMillis;
        this.removeUnhealthy = removeUnhealthy;
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "health-checker");
            t.setDaemon(true);
            return t;
        });
        this.executor.scheduleAtFixedRate(this::check, checkIntervalMillis, checkIntervalMillis, TimeUnit.MILLISECONDS);
    }

    private void check() {
        for (String name : registry.serviceNames()) {
            List<ServiceInstance> instances = registry.allInstances(name);
            for (ServiceInstance instance : instances) {
                if (!instance.isHealthy()) {
                    if (removeUnhealthy) {
                        registry.deregister(instance.getServiceId());
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
