package com.artificialintelligence.patterns.saga.examples;

import com.artificialintelligence.patterns.saga.ChoreographySaga;
import com.artificialintelligence.patterns.saga.SagaContext;
import com.artificialintelligence.patterns.saga.SagaEvent;
import com.artificialintelligence.patterns.saga.SagaEventBus;
import com.artificialintelligence.patterns.saga.SagaParticipant;

public class OrderServiceParticipant implements SagaParticipant {

    private final boolean shouldFail;

    public OrderServiceParticipant() {
        this(false);
    }

    public OrderServiceParticipant(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "order-service";
    }

    @Override
    public void register(ChoreographySaga saga, SagaEventBus bus, SagaContext context) {
        bus.subscribe(ChoreographySaga.START, event -> {
            if (shouldFail) {
                System.out.println("  [order] creation failed");
                saga.markFailed(new IllegalStateException("order creation failed"));
                bus.publish(new SagaEvent(ChoreographySaga.FAILED, saga.getName(), context));
                return;
            }
            System.out.println("  [order] created");
            saga.markStepCompleted(getName());
            context.put("orderCreated", true);
            bus.publish(new SagaEvent("PAYMENT_REQUESTED", saga.getName(), context));
        });

        bus.subscribe("COMPENSATE_ORDER", event -> {
            System.out.println("  [order] compensating (cancelling order)");
            saga.markStepCompensated(getName());
            bus.publish(new SagaEvent(ChoreographySaga.FAILED, saga.getName(), context));
        });
    }
}
