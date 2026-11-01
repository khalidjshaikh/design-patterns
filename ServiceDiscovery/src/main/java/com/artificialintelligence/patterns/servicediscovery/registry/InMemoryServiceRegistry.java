package com.artificialintelligence.patterns.servicediscovery.registry;

import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread-safe in-memory implementation of {@link ServiceRegistry}.
 *
 * <p>Instances are considered healthy only while their heartbeat is fresh
 * (within {@code heartbeatTimeoutMillis}). A background task periodically
 * evicts instances whose heartbeat has expired.</p>
 */
public class InMemoryServiceRegistry implements ServiceRegistry {

    private final long heartbeatTimeoutMillis;
    private final ConcurrentHashMap<String, ServiceInstance> instances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> byService = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleaner;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public InMemoryServiceRegistry() {
        this(30_000L);
    }

    public InMemoryServiceRegistry(long heartbeatTimeoutMillis) {
        if (heartbeatTimeoutMillis <= 0) {
            throw new IllegalArgumentException("heartbeatTimeoutMillis must be positive");
        }
        this.heartbeatTimeoutMillis = heartbeatTimeoutMillis;
        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "service-registry-cleaner");
            t.setDaemon(true);
            return t;
        });
        this.cleaner.scheduleAtFixedRate(this::evictStale, heartbeatTimeoutMillis,
                heartbeatTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void register(ServiceInstance instance) {
        if (instance == null) {
            throw new IllegalArgumentException("instance must not be null");
        }
        instances.put(instance.getServiceId(), instance);
        byService.computeIfAbsent(instance.getServiceName(), k -> ConcurrentHashMap.newKeySet())
                .add(instance.getServiceId());
        instance.heartbeat();
    }

    @Override
    public void deregister(String serviceId) {
        ServiceInstance removed = instances.remove(serviceId);
        if (removed != null) {
            Set<String> ids = byService.get(removed.getServiceName());
            if (ids != null) {
                ids.remove(serviceId);
                if (ids.isEmpty()) {
                    byService.remove(removed.getServiceName(), ids);
                }
            }
        }
    }

    @Override
    public void heartbeat(String serviceId) {
        ServiceInstance instance = instances.get(serviceId);
        if (instance != null) {
            instance.heartbeat();
        }
    }

    @Override
    public List<ServiceInstance> discover(String serviceName) {
        List<ServiceInstance> result = new ArrayList<>();
        Set<String> ids = byService.get(serviceName);
        if (ids == null) {
            return result;
        }
        long now = System.currentTimeMillis();
        for (String id : ids) {
            ServiceInstance instance = instances.get(id);
            if (instance == null) {
                continue;
            }
            if (now - instance.getLastHeartbeat() > heartbeatTimeoutMillis) {
                instance.setHealthy(false);
            }
            if (instance.isHealthy()) {
                result.add(instance);
            }
        }
        return result;
    }

    @Override
    public List<String> serviceNames() {
        return new ArrayList<>(byService.keySet());
    }

    @Override
    public List<ServiceInstance> allInstances(String serviceName) {
        List<ServiceInstance> result = new ArrayList<>();
        Set<String> ids = byService.get(serviceName);
        if (ids == null) {
            return result;
        }
        for (String id : ids) {
            ServiceInstance instance = instances.get(id);
            if (instance != null) {
                result.add(instance);
            }
        }
        return result;
    }

    private void evictStale() {
        if (shutdown.get()) {
            return;
        }
        long now = System.currentTimeMillis();
        for (ServiceInstance instance : new ArrayList<>(instances.values())) {
            if (now - instance.getLastHeartbeat() > heartbeatTimeoutMillis) {
                instance.setHealthy(false);
                // Evict after 2x timeout to give clients time to react.
                if (now - instance.getLastHeartbeat() > heartbeatTimeoutMillis * 2) {
                    deregister(instance.getServiceId());
                }
            }
        }
    }

    @Override
    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            cleaner.shutdownNow();
            instances.clear();
            byService.clear();
        }
    }

    public long getHeartbeatTimeoutMillis() {
        return heartbeatTimeoutMillis;
    }
}
