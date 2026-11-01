package com.artificialintelligence.saga.examples;

import com.artificialintelligence.saga.DefaultSagaContext;
import com.artificialintelligence.saga.SagaContext;

/**
 * Helper for order-related saga context keys.
 */
public final class OrderContext {

    public static final String ORDER_ID = "orderId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String AMOUNT = "amount";
    public static final String PAYMENT_ID = "paymentId";
    public static final String RESERVATION_ID = "reservationId";
    public static final String SHIPMENT_ID = "shipmentId";

    private OrderContext() {
    }

    public static SagaContext newContext(String orderId, String customerId, double amount) {
        DefaultSagaContext ctx = new DefaultSagaContext();
        ctx.put(ORDER_ID, orderId);
        ctx.put(CUSTOMER_ID, customerId);
        ctx.put(AMOUNT, amount);
        return ctx;
    }
}
