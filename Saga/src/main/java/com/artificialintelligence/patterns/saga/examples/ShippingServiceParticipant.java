package com.artificialintelligence.patterns.saga.examples;

import com.artificialintelligence.patterns.saga.ChoreographySaga;
import com.artificialintelligence.patterns.saga.SagaContext;
import com.artificialintelligence.patterns.saga.SagaEvent;
import com.artificialintelligence.patterns.saga.SagaEventBus;
import com.artificialintelligence.patterns.saga.SagaParticipant;

public class ShippingServiceParticipant implements SagaParticipant {

    private final boolean shouldFail;

    public ShippingServiceParticipant() {
        this(false);
    }

    public ShippingServiceParticipant(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "shipping-service";
    }

    @Override
    public void register(ChoreographySaga saga, SagaEventBus bus, SagaContext context) {
        bus.subscribe("SHIPPING_REQUESTED", event -> {
            if (shouldFail) {
                System.out.println("  [shipping] no carrier available");
                saga.markFailed(new IllegalStateException("no carrier available"));
                bus.publish(new SagaEvent("COMPENSATE_INVENTORY", saga.getName(), context));
                return;
            }
            String shipmentId = "SHIP-" + context.get("orderId");
            context.put("shipmentId", shipmentId);
            System.out.println("  [shipping] booked -> " + shipmentId);
            saga.markStepCompleted(getName());
            saga.markCompleted();
            bus.publish(new SagaEvent(ChoreographySaga.COMPLETED, saga.getName(), context));
        });
    }
}
