package com.artificialintelligence.patterns.saga.examples;

import com.artificialintelligence.patterns.saga.SagaContext;
import com.artificialintelligence.patterns.saga.SagaStep;

public class ProcessPaymentStep implements SagaStep {

    private final boolean shouldFail;

    public ProcessPaymentStep() {
        this(false);
    }

    public ProcessPaymentStep(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "process-payment";
    }

    @Override
    public void execute(SagaContext context) throws Exception {
        if (shouldFail) {
            throw new IllegalStateException("payment declined");
        }
        String orderId = context.get(OrderContext.ORDER_ID, String.class);
        String paymentId = "PAY-" + orderId;
        context.put(OrderContext.PAYMENT_ID, paymentId);
        System.out.println("  [process-payment] charged " + context.get(OrderContext.AMOUNT) + " -> " + paymentId);
    }

    @Override
    public void compensate(SagaContext context) throws Exception {
        String paymentId = context.get(OrderContext.PAYMENT_ID, String.class);
        System.out.println("  [process-payment] refunding " + paymentId);
    }
}
