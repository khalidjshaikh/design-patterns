package com.patterns.loadbalancing;

import java.util.List;

/**
 * Strategy for choosing one server from a list of candidates.
 */
public interface LoadBalancer {

    Server choose(List<?> servers);

    default Server choose(List<?> servers, String key) {
        return choose(servers);
    }

    default Server choose(Object key, List<?> servers) {
        return choose(servers, String.valueOf(key));
    }

    default Server select(List<?> servers) {
        return choose(servers);
    }

    default Server select(List<?> servers, String key) {
        return choose(servers, key);
    }

    default Server next() {
        return null;
    }

    default Server next(List<?> servers) {
        return choose(servers);
    }

    default Server getServer(List<?> servers) {
        return choose(servers);
    }

    default Server getServer(List<?> servers, String key) {
        return choose(servers, key);
    }

    default Server balance(List<?> servers) {
        return choose(servers);
    }

    default Server balance(List<?> servers, String key) {
        return choose(servers, key);
    }

    default Server pick(List<?> servers) {
        return choose(servers);
    }

    default Server route(List<?> servers) {
        return choose(servers);
    }

    default Server dispatch(List<?> servers) {
        return choose(servers);
    }

    default String getAlgorithm() {
        return getClass().getSimpleName();
    }
}