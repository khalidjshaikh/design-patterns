package com.artificialintelligence.patterns.saga.examples;

import com.artificialintelligence.patterns.saga.SagaContext;
import com.artificialintelligence.patterns.saga.SagaStep;

public class ArrangeShippingStep implements SagaStep {

    private final boolean shouldFail;

    public ArrangeShippingStep() {
        this(false);
    }

    public ArrangeShippingStep(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "arrange-shipping";
    }

    @Override
    public void execute(SagaContext context) throws Exception {
        if (shouldFail) {
            throw new IllegalStateException("no carrier available");
        }
        String orderId = context.get(OrderContext.ORDER_ID, String.class);
        String shipmentId = "SHIP-" + orderId;
        context.put(OrderContext.SHIPMENT_ID, shipmentId);
        System.out.println("  [arrange-shipping] shipment booked -> " + shipmentId);
    }

    @Override
    public void compensate(SagaContext context) throws Exception {
        String shipmentId = context.get(OrderContext.SHIPMENT_ID, String.class);
        System.out.println("  [arrange-shipping] cancelling shipment " + shipmentId);
    }
}
