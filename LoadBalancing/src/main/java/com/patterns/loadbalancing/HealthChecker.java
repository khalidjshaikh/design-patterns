package com.patterns.loadbalancing;

import java.util.List;
import java.util.function.Predicate;

public class HealthChecker {

    public static void check(List<?> servers, Predicate<Server> healthPredicate) {
        if (servers == null) return;
        for (Object o : servers) {
            if (o instanceof Server) {
                Server s = (Server) o;
                boolean healthy = (healthPredicate == null) ? true : healthPredicate.test(s);
                s.setHealthy(healthy);
            }
        }
    }

    public static List<Server> filterHealthy(List<?> servers) {
        java.util.List<Server> result = new java.util.ArrayList<>();
        if (servers == null) return result;
        for (Object o : servers) {
            if (o instanceof Server && ((Server) o).isHealthy()) {
                result.add((Server) o);
            }
        }
        return result;
    }
}