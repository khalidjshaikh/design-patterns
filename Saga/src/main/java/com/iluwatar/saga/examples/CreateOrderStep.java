package com.iluwatar.saga.examples;

import com.iluwatar.saga.SagaContext;
import com.iluwatar.saga.SagaStep;

public class CreateOrderStep implements SagaStep {

    @Override
    public String getName() {
        return "create-order";
    }

    @Override
    public void execute(SagaContext context) throws Exception {
        String orderId = context.get(OrderContext.ORDER_ID, String.class);
        System.out.println("  [create-order] creating order " + orderId);
        context.put("orderCreated", true);
    }

    @Override
    public void compensate(SagaContext context) throws Exception {
        String orderId = context.get(OrderContext.ORDER_ID, String.class);
        System.out.println("  [create-order] cancelling order " + orderId);
        context.put("orderCreated", false);
    }
}
