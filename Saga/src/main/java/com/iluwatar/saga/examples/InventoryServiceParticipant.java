package com.iluwatar.saga.examples;

import com.iluwatar.saga.ChoreographySaga;
import com.iluwatar.saga.SagaContext;
import com.iluwatar.saga.SagaEvent;
import com.iluwatar.saga.SagaEventBus;
import com.iluwatar.saga.SagaParticipant;

public class InventoryServiceParticipant implements SagaParticipant {

    private final boolean shouldFail;

    public InventoryServiceParticipant() {
        this(false);
    }

    public InventoryServiceParticipant(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getName() {
        return "inventory-service";
    }

    @Override
    public void register(ChoreographySaga saga, SagaEventBus bus, SagaContext context) {
        bus.subscribe("INVENTORY_REQUESTED", event -> {
            if (shouldFail) {
                System.out.println("  [inventory] out of stock");
                saga.markFailed(new IllegalStateException("out of stock"));
                bus.publish(new SagaEvent("COMPENSATE_PAYMENT", saga.getName(), context));
                return;
            }
            String reservationId = "RES-" + context.get("orderId");
            context.put("reservationId", reservationId);
            System.out.println("  [inventory] reserved -> " + reservationId);
            saga.markStepCompleted(getName());
            bus.publish(new SagaEvent("SHIPPING_REQUESTED", saga.getName(), context));
        });

        bus.subscribe("COMPENSATE_INVENTORY", event -> {
            System.out.println("  [inventory] releasing " + context.get("reservationId"));
            saga.markStepCompensated(getName());
            bus.publish(new SagaEvent("COMPENSATE_PAYMENT", saga.getName(), context));
        });
    }
}
