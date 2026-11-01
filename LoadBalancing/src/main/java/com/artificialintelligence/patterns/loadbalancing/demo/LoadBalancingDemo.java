package com.artificialintelligence.patterns.loadbalancing.demo;

import com.artificialintelligence.patterns.loadbalancing.IpHashLoadBalancer;
import com.artificialintelligence.patterns.loadbalancing.LeastConnectionsLoadBalancer;
import com.artificialintelligence.patterns.loadbalancing.LoadBalancer;
import com.artificialintelligence.patterns.loadbalancing.LoadBalancerFactory;
import com.artificialintelligence.patterns.loadbalancing.RandomLoadBalancer;
import com.artificialintelligence.patterns.loadbalancing.RoundRobinLoadBalancer;
import com.artificialintelligence.patterns.loadbalancing.Server;
import com.artificialintelligence.patterns.loadbalancing.WeightedRoundRobinLoadBalancer;

import java.util.List;

public class LoadBalancingDemo {

    public static void main(String[] args) {
        List<Server> servers = List.of(
                new Server("s1", "host1", 8080, 3),
                new Server("s2", "host2", 8080, 1),
                new Server("s3", "host3", 8080, 1));

        System.out.println("=== Round Robin ===");
        RoundRobinLoadBalancer rr = new RoundRobinLoadBalancer(servers);
        for (int i = 0; i < 6; i++) {
            System.out.println("  " + rr.choose(servers).getId());
        }

        System.out.println("=== Weighted Round Robin ===");
        WeightedRoundRobinLoadBalancer wrr = new WeightedRoundRobinLoadBalancer(servers);
        for (int i = 0; i < 8; i++) {
            System.out.println("  " + wrr.choose(servers).getId());
        }

        System.out.println("=== Least Connections ===");
        LeastConnectionsLoadBalancer lc = new LeastConnectionsLoadBalancer(servers);
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + lc.choose(servers).getId());
        }

        System.out.println("=== Random ===");
        RandomLoadBalancer rnd = new RandomLoadBalancer(servers, 42L);
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + rnd.choose(servers).getId());
        }

        System.out.println("=== IP Hash ===");
        IpHashLoadBalancer ip = new IpHashLoadBalancer(servers);
        for (String client : List.of("10.0.0.1", "10.0.0.2", "10.0.0.1")) {
            System.out.println("  " + client + " -> " + ip.choose(servers, client).getId());
        }

        System.out.println("=== Factory ===");
        LoadBalancer lb = LoadBalancerFactory.create("round-robin", servers);
        System.out.println("  " + lb.choose(servers).getId());
    }
}