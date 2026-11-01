package com.artificialintelligence.saga;

import com.artificialintelligence.saga.ChoreographySaga;
import com.artificialintelligence.saga.examples.ArrangeShippingStep;
import com.artificialintelligence.saga.examples.CreateOrderStep;
import com.artificialintelligence.saga.examples.OrderContext;
import com.artificialintelligence.saga.examples.ProcessPaymentStep;
import com.artificialintelligence.saga.examples.ReserveInventoryStep;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SagaTest {

    @Test
    void successfulSagaCompletesAllSteps() {
        Saga saga = new Saga("order");
        saga.addStep(new CreateOrderStep());
        saga.addStep(new ProcessPaymentStep());
        saga.addStep(new ReserveInventoryStep());
        saga.addStep(new ArrangeShippingStep());

        SagaContext ctx = OrderContext.newContext("O1", "C1", 10.0);
        SagaResult result = saga.execute(ctx);

        assertTrue(result.isSuccess());
        assertEquals(SagaStatus.COMPLETED, result.getStatus());
        assertEquals(4, result.getCompletedSteps());
        assertEquals(4, result.getTotalSteps());
        assertTrue(ctx.get("orderCreated", Boolean.class));
        assertEquals("PAY-O1", ctx.get("paymentId"));
        assertEquals("RES-O1", ctx.get("reservationId"));
        assertEquals("SHIP-O1", ctx.get("shipmentId"));
    }

    @Test
    void failingPaymentCompensatesOrder() {
        List<String> compensated = new ArrayList<>();
        Saga saga = new Saga("order");
        saga.addStep(step("create-order", compensated, false));
        saga.addStep(new ProcessPaymentStep(true));

        SagaContext ctx = OrderContext.newContext("O2", "C2", 10.0);
        SagaResult result = saga.execute(ctx);

        assertFalse(result.isSuccess());
        assertEquals(SagaStatus.COMPENSATED, result.getStatus());
        assertEquals("process-payment", result.getFailedStep());
        assertEquals(1, result.getCompletedSteps());
        assertTrue(compensated.contains("create-order"));
    }

    @Test
    void compensationOrderIsReverse() {
        List<String> compensated = new ArrayList<>();
        Saga saga = new Saga("order");
        saga.addStep(step("s1", compensated, false));
        saga.addStep(step("s2", compensated, false));
        saga.addStep(failingStep("s3"));

        SagaContext ctx = new DefaultSagaContext();
        saga.execute(ctx);

        assertEquals(List.of("s2", "s1"), compensated);
    }

    @Test
    void failingLastStepCompensatesAllPrevious() {
        List<String> compensated = new ArrayList<>();
        Saga saga = new Saga("order");
        saga.addStep(step("s1", compensated, false));
        saga.addStep(step("s2", compensated, false));
        saga.addStep(failingStep("s3"));

        SagaContext ctx = new DefaultSagaContext();
        SagaResult result = saga.execute(ctx);

        assertFalse(result.isSuccess());
        assertEquals(2, result.getCompletedSteps());
        assertEquals(List.of("s2", "s1"), compensated);
    }

    @Test
    void listenerReceivesEvents() {
        List<String> events = new ArrayList<>();
        Saga saga = new Saga("order");
        saga.addListener(new SagaListener() {
            @Override
            public void onStepCompleted(String sagaName, String stepName, SagaContext context) {
                events.add("completed:" + stepName);
            }

            @Override
            public void onSagaCompleted(String sagaName, SagaContext context, SagaResult result) {
                events.add("saga:" + result.getStatus());
            }
        });
        saga.addStep(new CreateOrderStep());

        saga.execute(OrderContext.newContext("O3", "C3", 1.0));
        assertTrue(events.contains("completed:create-order"));
        assertTrue(events.contains("saga:COMPLETED"));
    }

    @Test
    void manualCompensation() {
        List<String> compensated = new ArrayList<>();
        Saga saga = new Saga("manual");
        SagaStep a = step("a", compensated, false);
        SagaStep b = step("b", compensated, false);
        saga.addStep(a);
        saga.addStep(b);
        saga.execute(new DefaultSagaContext());

        List<SagaStep> toCompensate = new ArrayList<>(saga.getSteps());
        boolean ok = saga.compensate(toCompensate, new DefaultSagaContext());
        assertTrue(ok);
        assertEquals(2, compensated.size());
    }

    @Test
    void choreographySuccess() {
        ChoreographySaga saga = new ChoreographySaga("choreo");
        saga.getContext().put("orderId", "O9");
        saga.addParticipant(new com.artificialintelligence.saga.examples.OrderServiceParticipant());
        saga.addParticipant(new com.artificialintelligence.saga.examples.PaymentServiceParticipant());
        saga.addParticipant(new com.artificialintelligence.saga.examples.InventoryServiceParticipant());
        saga.addParticipant(new com.artificialintelligence.saga.examples.ShippingServiceParticipant());

        SagaResult result = saga.execute();
        assertTrue(result.isSuccess());
        assertEquals(4, saga.getCompletedSteps().size());
    }

    @Test
    void choreographyFailureCompensates() {
        ChoreographySaga saga = new ChoreographySaga("choreo2");
        saga.getContext().put("orderId", "O10");
        saga.addParticipant(new com.artificialintelligence.saga.examples.OrderServiceParticipant());
        saga.addParticipant(new com.artificialintelligence.saga.examples.PaymentServiceParticipant());
        saga.addParticipant(new com.artificialintelligence.saga.examples.InventoryServiceParticipant());
        saga.addParticipant(new com.artificialintelligence.saga.examples.ShippingServiceParticipant(true));

        SagaResult result = saga.execute();
        assertFalse(result.isSuccess());
        // shipping + inventory + payment + order compensated
        assertTrue(saga.getCompensatedSteps().size() >= 3);
    }

    private static SagaStep step(String name, List<String> compensated, boolean fail) {
        return new SagaStep() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void execute(SagaContext context) {
                // no-op
            }

            @Override
            public void compensate(SagaContext context) {
                compensated.add(name);
            }
        };
    }

    private static SagaStep failingStep(String name) {
        return new SagaStep() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void execute(SagaContext context) throws Exception {
                throw new IllegalStateException("boom");
            }

            @Override
            public void compensate(SagaContext context) {
            }
        };
    }
}
