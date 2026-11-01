package com.artificialintelligence.patterns.servicediscovery.registry;

import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;

import java.util.List;

/**
 * Contract for a service registry.
 */
public interface ServiceRegistry {

    /** Registers a new service instance. */
    void register(ServiceInstance instance);

    /** Removes a service instance by id. */
    void deregister(String serviceId);

    /** Updates the heartbeat timestamp for an instance. */
    void heartbeat(String serviceId);

    /** Returns healthy instances for the given service name. */
    List<ServiceInstance> discover(String serviceName);

    /** Returns all registered service names. */
    List<String> serviceNames();

    /** Returns all instances (healthy or not) for a service name. */
    List<ServiceInstance> allInstances(String serviceName);

    /** Shuts down the registry and any background resources. */
    void shutdown();
}
