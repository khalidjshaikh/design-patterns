package com.artificialintelligence.saga.demo;

import com.artificialintelligence.saga.ChoreographySaga;
import com.artificialintelligence.saga.DefaultSagaContext;
import com.artificialintelligence.saga.SagaContext;
import com.artificialintelligence.saga.SagaResult;
import com.artificialintelligence.saga.examples.InventoryServiceParticipant;
import com.artificialintelligence.saga.examples.OrderServiceParticipant;
import com.artificialintelligence.saga.examples.PaymentServiceParticipant;
import com.artificialintelligence.saga.examples.ShippingServiceParticipant;

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
