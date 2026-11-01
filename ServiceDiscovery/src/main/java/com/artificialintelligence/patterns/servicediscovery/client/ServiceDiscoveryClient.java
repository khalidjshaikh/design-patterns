package com.artificialintelligence.patterns.servicediscovery.client;

import com.artificialintelligence.patterns.servicediscovery.loadbalancer.LoadBalancer;
import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;
import com.artificialintelligence.patterns.servicediscovery.registry.ServiceRegistry;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client-side service discovery helper.
 *
 * <p>A service instance uses this client to register itself, send periodic
 * heartbeats and deregister on shutdown. Consumers use it to discover and
 * choose instances of other services.</p>
 */
public class ServiceDiscoveryClient implements AutoCloseable {

    private final ServiceRegistry registry;
    private final String serviceId;
    private final String serviceName;
    private final String host;
    private final int port;

    private final ScheduledExecutorService heartbeatExecutor;
    private final AtomicBoolean registered = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public ServiceDiscoveryClient(ServiceRegistry registry, String serviceId, String serviceName,
                                 String host, int port) {
        this(registry, serviceId, serviceName, host, port, 10_000L);
    }

    public ServiceDiscoveryClient(ServiceRegistry registry, String serviceId, String serviceName,
                                 String host, int port, long heartbeatIntervalMillis) {
        if (registry == null) throw new IllegalArgumentException("registry must not be null");
        this.registry = registry;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "heartbeat-" + serviceId);
            t.setDaemon(true);
            return t;
        });
        this.heartbeatExecutor.scheduleAtFixedRate(this::sendHeartbeat,
                heartbeatIntervalMillis, heartbeatIntervalMillis, TimeUnit.MILLISECONDS);
    }

    public ServiceInstance register() {
        if (closed.get()) {
            throw new IllegalStateException("client is closed");
        }
        ServiceInstance instance = new ServiceInstance(serviceId, serviceName, host, port);
        registry.register(instance);
        registered.set(true);
        return instance;
    }

    public void register(ServiceInstance instance) {
        if (closed.get()) {
            throw new IllegalStateException("client is closed");
        }
        registry.register(instance);
        registered.set(true);
    }

    private void sendHeartbeat() {
        if (registered.get() && !closed.get()) {
            registry.heartbeat(serviceId);
        }
    }

    public void deregister() {
        if (registered.compareAndSet(true, false)) {
            registry.deregister(serviceId);
        }
    }

    public List<ServiceInstance> discover(String serviceName) {
        return registry.discover(serviceName);
    }

    public ServiceInstance choose(String serviceName, LoadBalancer loadBalancer) {
        return loadBalancer.chooseOrNull(registry.discover(serviceName));
    }

    public ServiceRegistry getRegistry() {
        return registry;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            deregister();
            heartbeatExecutor.shutdownNow();
        }
    }
}
