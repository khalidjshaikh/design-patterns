package com.artificialintelligence.servicediscovery.model;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a single running instance of a service.
 */
public class ServiceInstance {

    private final String serviceId;
    private final String serviceName;
    private final String host;
    private final int port;
    private final Map<String, String> metadata;

    private volatile boolean healthy;
    private volatile long lastHeartbeat;

    public ServiceInstance(String serviceId, String serviceName, String host, int port) {
        this(serviceId, serviceName, host, port, Map.of());
    }

    public ServiceInstance(String serviceId, String serviceName, String host, int port,
                           Map<String, String> metadata) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId must not be blank");
        }
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("serviceName must not be blank");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be in range 1..65535");
        }
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.metadata = new ConcurrentHashMap<>(metadata == null ? Map.of() : metadata);
        this.healthy = true;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return "http://" + host + ":" + port;
    }

    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
        this.healthy = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceInstance)) return false;
        ServiceInstance that = (ServiceInstance) o;
        return Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", healthy=" + healthy +
                '}';
    }
}
