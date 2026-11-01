package com.patterns.saga.examples;

import com.patterns.saga.SagaContext;
import com.patterns.saga.SagaStep;

public class ReserveInventoryStep implements SagaStep {

    private final boolean shouldFail;

    public ReserveInventoryStep() {
        this(false);
    }

    public ReserveInventoryStep(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "reserve-inventory";
    }

    @Override
    public void execute(SagaContext context) throws Exception {
        if (shouldFail) {
            throw new IllegalStateException("out of stock");
        }
        String orderId = context.get(OrderContext.ORDER_ID, String.class);
        String reservationId = "RES-" + orderId;
        context.put(OrderContext.RESERVATION_ID, reservationId);
        System.out.println("  [reserve-inventory] reserved stock -> " + reservationId);
    }

    @Override
    public void compensate(SagaContext context) throws Exception {
        String reservationId = context.get(OrderContext.RESERVATION_ID, String.class);
        System.out.println("  [reserve-inventory] releasing reservation " + reservationId);
    }
}
