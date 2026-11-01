package com.artificialintelligence.patterns.saga.examples;

import com.artificialintelligence.patterns.saga.ChoreographySaga;
import com.artificialintelligence.patterns.saga.SagaContext;
import com.artificialintelligence.patterns.saga.SagaEvent;
import com.artificialintelligence.patterns.saga.SagaEventBus;
import com.artificialintelligence.patterns.saga.SagaParticipant;

public class PaymentServiceParticipant implements SagaParticipant {

    private final boolean shouldFail;

    public PaymentServiceParticipant() {
        this(false);
    }

    public PaymentServiceParticipant(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "payment-service";
    }

    @Override
    public void register(ChoreographySaga saga, SagaEventBus bus, SagaContext context) {
        bus.subscribe("PAYMENT_REQUESTED", event -> {
            if (shouldFail) {
                System.out.println("  [payment] declined");
                saga.markFailed(new IllegalStateException("payment declined"));
                bus.publish(new SagaEvent("COMPENSATE_ORDER", saga.getName(), context));
                return;
            }
            String paymentId = "PAY-" + context.get("orderId");
            context.put("paymentId", paymentId);
            System.out.println("  [payment] charged -> " + paymentId);
            saga.markStepCompleted(getName());
            bus.publish(new SagaEvent("INVENTORY_REQUESTED", saga.getName(), context));
        });

        bus.subscribe("COMPENSATE_PAYMENT", event -> {
            System.out.println("  [payment] refunding " + context.get("paymentId"));
            saga.markStepCompensated(getName());
            bus.publish(new SagaEvent("COMPENSATE_ORDER", saga.getName(), context));
        });
    }
}
