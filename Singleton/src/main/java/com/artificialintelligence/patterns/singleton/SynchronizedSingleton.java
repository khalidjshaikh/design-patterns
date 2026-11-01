package com.artificialintelligence.patterns.singleton;

/**
 * Thread-safe lazy initialization using a synchronized method.
 * Simple and correct, but synchronizes on every call (performance cost).
 */
public final class SynchronizedSingleton {

    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {
    }

    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }

    public String greet() {
        return "SynchronizedSingleton@" + Integer.toHexString(hashCode());
    }
}
