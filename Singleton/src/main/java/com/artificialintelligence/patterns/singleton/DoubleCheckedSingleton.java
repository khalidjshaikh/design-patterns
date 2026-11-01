package com.artificialintelligence.patterns.singleton;

/**
 * Thread-safe lazy initialization using double-checked locking.
 * Requires the field to be volatile to prevent partial construction visibility.
 */
public final class DoubleCheckedSingleton {

    private static volatile DoubleCheckedSingleton instance;

    private DoubleCheckedSingleton() {
    }

    public static DoubleCheckedSingleton getInstance() {
        DoubleCheckedSingleton result = instance;
        if (result == null) {
            synchronized (DoubleCheckedSingleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = new DoubleCheckedSingleton();
                }
            }
        }
        return result;
    }

    public String greet() {
        return "DoubleCheckedSingleton@" + Integer.toHexString(hashCode());
    }
}
