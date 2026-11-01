package com.artificialintelligence.patterns.servicediscovery.loadbalancer;

import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;

import java.util.List;

/**
 * Strategy for choosing one instance from a list of candidates.
 */
public interface LoadBalancer {

    ServiceInstance choose(List<ServiceInstance> instances);

    default ServiceInstance chooseOrNull(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        return choose(instances);
    }
}
