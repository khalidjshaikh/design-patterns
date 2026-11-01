package com.artificialintelligence.patterns.singleton;

/**
 * Lazy initialization: instance created on first request.
 * NOT thread-safe. Use only in single-threaded contexts.
 */
public final class LazySingleton {

    private static LazySingleton instance;

    private LazySingleton() {
    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    public String greet() {
        return "LazySingleton@" + Integer.toHexString(hashCode());
    }
}
