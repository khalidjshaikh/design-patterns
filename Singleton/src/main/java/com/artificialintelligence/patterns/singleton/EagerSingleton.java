package com.artificialintelligence.patterns.singleton;

/**
 * Eager initialization: instance created at class-loading time.
 * Thread-safe by default (class loading is synchronized by the JVM).
 * Downside: instance created even if never used.
 */
public final class EagerSingleton {

    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {
        // Prevent instantiation from outside.
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    public String greet() {
        return "EagerSingleton@" + Integer.toHexString(hashCode());
    }
}
