package com.artificialintelligence.patterns.singleton;

public final class Demo {
    public static void main(String[] args) {
        System.out.println(EagerSingleton.getInstance().greet());
        System.out.println(LazySingleton.getInstance().greet());
        System.out.println(SynchronizedSingleton.getInstance().greet());
        System.out.println(DoubleCheckedSingleton.getInstance().greet());
        System.out.println(BillPughSingleton.getInstance().greet());
        System.out.println(EnumSingleton.INSTANCE.greet());

        System.out.println("Same instance? " + (EagerSingleton.getInstance() == EagerSingleton.getInstance()));
    }
}
