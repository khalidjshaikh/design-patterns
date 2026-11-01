package com.artificialintelligence.loadbalancing;

import java.util.List;

public final class LoadBalancerFactory {

    private LoadBalancerFactory() {
    }

    public static LoadBalancer create(String algorithm) {
        return create(algorithm, null);
    }

    public static LoadBalancer create(String algorithm, List<?> servers) {
        if (algorithm == null) {
            algorithm = "round-robin";
        }
        String a = algorithm.trim().toLowerCase().replace("_", "").replace("-", "").replace(" ", "");
        switch (a) {
            case "roundrobin":
            case "rr":
                return new RoundRobinLoadBalancer(servers);
            case "weightedroundrobin":
            case "weighted":
            case "wrr":
                return new WeightedRoundRobinLoadBalancer(servers);
            case "leastconnections":
            case "leastconn":
            case "lc":
                return new LeastConnectionsLoadBalancer(servers);
            case "random":
                return new RandomLoadBalancer(servers);
            case "iphash":
            case "iphashing":
                return new IpHashLoadBalancer(servers);
            case "consistenthash":
            case "consistenthashing":
                return new ConsistentHashLoadBalancer(servers);
            default:
                return new RoundRobinLoadBalancer(servers);
        }
    }

    public static LoadBalancer get(String algorithm) {
        return create(algorithm);
    }

    public static LoadBalancer getLoadBalancer(String algorithm) {
        return create(algorithm);
    }
}