package com.patterns.saga.demo;

import com.patterns.saga.DefaultSagaContext;
import com.patterns.saga.Saga;
import com.patterns.saga.SagaContext;
import com.patterns.saga.SagaResult;
import com.patterns.saga.SagaStatus;
import com.patterns.saga.examples.ArrangeShippingStep;
import com.patterns.saga.examples.CreateOrderStep;
import com.patterns.saga.examples.OrderContext;
import com.patterns.saga.examples.ProcessPaymentStep;
import com.patterns.saga.examples.ReserveInventoryStep;

public class SagaDemo {

    public static void main(String[] args) {
        System.out.println("=== Successful order saga ===");
        runSaga(false, false, false);

        System.out.println();
        System.out.println("=== Failing payment saga (compensates order) ===");
        runSaga(true, false, false);

        System.out.println();
        System.out.println("=== Failing inventory saga (compensates payment + order) ===");
        runSaga(false, true, false);

        System.out.println();
        System.out.println("=== Failing shipping saga (compensates inventory + payment + order) ===");
        runSaga(false, false, true);
    }

    private static void runSaga(boolean failPayment, boolean failInventory, boolean failShipping) {
        Saga saga = new Saga("order-saga");
        saga.addStep(new CreateOrderStep());
        saga.addStep(new ProcessPaymentStep(failPayment));
        saga.addStep(new ReserveInventoryStep(failInventory));
        saga.addStep(new ArrangeShippingStep(failShipping));

        SagaContext context = OrderContext.newContext("ORD-1", "CUST-1", 99.99);
        SagaResult result = saga.execute(context);

        System.out.println("result=" + result);
        System.out.println("status=" + saga.getStatus());
        System.out.println("context=" + context);
    }
}
