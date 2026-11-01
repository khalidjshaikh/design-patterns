package com.patterns.saga.demo;

import com.patterns.saga.ChoreographySaga;
import com.patterns.saga.DefaultSagaContext;
import com.patterns.saga.SagaContext;
import com.patterns.saga.SagaResult;
import com.patterns.saga.examples.InventoryServiceParticipant;
import com.patterns.saga.examples.OrderServiceParticipant;
import com.patterns.saga.examples.PaymentServiceParticipant;
import com.patterns.saga.examples.ShippingServiceParticipant;

public class ChoreographyDemo {

    public static void main(String[] args) {
        System.out.println("=== Choreography: success ===");
        run(false, false, false, false);

        System.out.println();
        System.out.println("=== Choreography: shipping fails ===");
        run(false, false, false, true);
    }

    private static void run(boolean failOrder, boolean failPayment, boolean failInventory, boolean failShipping) {
        ChoreographySaga saga = new ChoreographySaga("order-choreography");
        saga.getContext().put("orderId", "ORD-1");
        saga.addParticipant(new OrderServiceParticipant(failOrder));
        saga.addParticipant(new PaymentServiceParticipant(failPayment));
        saga.addParticipant(new InventoryServiceParticipant(failInventory));
        saga.addParticipant(new ShippingServiceParticipant(failShipping));

        SagaResult result = saga.execute();
        System.out.println("result=" + result);
        System.out.println("completedSteps=" + saga.getCompletedSteps());
        System.out.println("compensatedSteps=" + saga.getCompensatedSteps());
    }
}
