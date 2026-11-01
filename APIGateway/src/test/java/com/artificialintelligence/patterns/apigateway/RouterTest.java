package com.artificialintelligence.patterns.apigateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RouterTest {

    @Test
    void resolvesExactPrefix() {
        Router router = new Router();
        router.addRoute("/api/users", "user-service");
        assertEquals("user-service", router.resolve("/api/users/1"));
    }

    @Test
    void longestPrefixWins() {
        Router router = new Router();
        router.addRoute("/api", "generic");
        router.addRoute("/api/users", "user-service");
        assertEquals("user-service", router.resolve("/api/users/1"));
        assertEquals("generic", router.resolve("/api/other"));
    }

    @Test
    void unknownPathReturnsNull() {
        Router router = new Router();
        router.addRoute("/api/users", "user-service");
        assertNull(router.resolve("/health"));
    }
}
