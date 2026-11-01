package com.artificialintelligence.patterns.singleton;

/**
 * Enum singleton: the most robust form.
 * Handles serialization and reflection attacks automatically.
 * Recommended by Joshua Bloch (Effective Java).
 */
public enum EnumSingleton {
    INSTANCE;

    public String greet() {
        return "EnumSingleton@" + Integer.toHexString(hashCode());
    }
}
