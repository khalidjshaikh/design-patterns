package com.artificialintelligence.patterns.singleton;

/**
 * Bill Pugh (initialization-on-demand holder) idiom.
 * Lazy, thread-safe, without synchronization overhead on the hot path.
 * Recommended for most use cases.
 */
public final class BillPughSingleton {

    private BillPughSingleton() {
    }

    private static class Holder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public String greet() {
        return "BillPughSingleton@" + Integer.toHexString(hashCode());
    }
}
