package com.artificialintelligence.patterns.singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class SingletonTest {

    @Test
    void eagerIsSingleton() {
        assertSame(EagerSingleton.getInstance(), EagerSingleton.getInstance());
    }

    @Test
    void lazyIsSingleton() {
        assertSame(LazySingleton.getInstance(), LazySingleton.getInstance());
    }

    @Test
    void synchronizedIsSingleton() {
        assertSame(SynchronizedSingleton.getInstance(), SynchronizedSingleton.getInstance());
    }

    @Test
    void doubleCheckedIsSingleton() {
        assertSame(DoubleCheckedSingleton.getInstance(), DoubleCheckedSingleton.getInstance());
    }

    @Test
    void billPughIsSingleton() {
        assertSame(BillPughSingleton.getInstance(), BillPughSingleton.getInstance());
    }

    @Test
    void enumIsSingleton() {
        assertSame(EnumSingleton.INSTANCE, EnumSingleton.INSTANCE);
    }
}
