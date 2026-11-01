package com.artificialintelligence.patterns.saga.examples;

import com.artificialintelligence.patterns.saga.SagaContext;
import com.artificialintelligence.patterns.saga.SagaStep;

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
