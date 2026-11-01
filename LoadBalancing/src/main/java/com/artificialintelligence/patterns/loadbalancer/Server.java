package com.artificialintelligence.patterns.loadbalancer;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a backend server / node that can serve requests.
 */
public class Server {

    private final String id;
    private volatile String host;
    private volatile int port;
    private volatile int weight;
    private volatile boolean healthy;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public Server(String id) {
        this(id, "localhost", 80, 1, true);
    }

    public Server(String id, String host, int port) {
        this(id, host, port, 1, true);
    }

    public Server(String id, String host, int port, int weight) {
        this(id, host, port, weight, true);
    }

    public Server(String id, String host, int port, double weight) {
        this(id, host, port, (int) Math.round(weight), true);
    }

    public Server(String id, String host, int port, int weight, boolean healthy) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be in range 1..65535");
        }
        if (weight <= 0) {
            weight = 1;
        }
        this.id = id;
        this.host = host;
        this.port = port;
        this.weight = weight;
        this.healthy = healthy;
    }

    // convenience: name-based
    public Server(String name, int port) {
        this(name, "localhost", port, 1, true);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return id;
    }

    public String getServerId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getHostname() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight <= 0) {
            weight = 1;
        }
        this.weight = weight;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public boolean getHealthy() {
        return healthy;
    }

    public String getStatus() {
        return healthy ? "UP" : "DOWN";
    }

    public boolean isActive() {
        return healthy;
    }

    public boolean isAlive() {
        return healthy;
    }

    public boolean isUp() {
        return healthy;
    }

    public String getServerName() {
        return id;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public void markHealthy() {
        this.healthy = true;
    }

    public void markUnhealthy() {
        this.healthy = false;
    }

    public int getActiveConnections() {
        return activeConnections.get();
    }

    public int getConnections() {
        return activeConnections.get();
    }

    public void setActiveConnections(int value) {
        activeConnections.set(Math.max(0, value));
    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.updateAndGet(v -> Math.max(0, v - 1));
    }

    public void acquire() {
        incrementConnections();
    }

    public void release() {
        decrementConnections();
    }

    public void connectionStarted() {
        incrementConnections();
    }

    public void connectionFinished() {
        decrementConnections();
    }

    public String getAddress() {
        return host;
    }

    public String getHostPort() {
        return host + ":" + port;
    }

    public String getUrl() {
        return "http://" + host + ":" + port;
    }

    public String getUri() {
        return getUrl();
    }

    @Override
    public String toString() {
        return "Server{id='" + id + "', host='" + host + "', port=" + port +
                ", weight=" + weight + ", healthy=" + healthy +
                ", activeConnections=" + activeConnections.get() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;
        Server that = (Server) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}