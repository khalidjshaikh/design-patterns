package com.artificialintelligence.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Choreographed saga: participants react to events published on a shared
 * {@link SagaEventBus}. The saga completes when a participant calls
 * {@link #markCompleted()} or {@link #markFailed(Throwable)}.
 */
public class ChoreographySaga {

    public static final String START = "SAGA_START";
    public static final String COMPLETED = "SAGA_COMPLETED";
    public static final String FAILED = "SAGA_FAILED";

    private final String name;
    private final SagaEventBus bus = new SagaEventBus();
    private final SagaContext context = new DefaultSagaContext();
    private final List<SagaParticipant> participants = new ArrayList<>();

    private volatile SagaStatus status = SagaStatus.NOT_STARTED;
    private volatile boolean completed;
    private volatile Throwable failureCause;
    private final List<String> completedSteps = new ArrayList<>();
    private final List<String> compensatedSteps = new ArrayList<>();

    public ChoreographySaga(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getName() {
        return name;
    }

    public SagaEventBus getBus() {
        return bus;
    }

    public SagaContext getContext() {
        return context;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public void addParticipant(SagaParticipant participant) {
        Objects.requireNonNull(participant, "participant");
        participants.add(participant);
        participant.register(this, bus, context);
    }

    public synchronized void markStepCompleted(String stepName) {
        completedSteps.add(stepName);
    }

    public synchronized void markStepCompensated(String stepName) {
        compensatedSteps.add(stepName);
    }

    public void markCompleted() {
        this.completed = true;
        this.status = SagaStatus.COMPLETED;
    }

    public void markFailed(Throwable cause) {
        this.failureCause = cause;
        this.status = SagaStatus.FAILED;
    }

    public SagaResult execute() {
        status = SagaStatus.STARTED;
        bus.publish(new SagaEvent(START, name, context));
        if (completed) {
            status = SagaStatus.COMPLETED;
        } else if (failureCause != null) {
            status = SagaStatus.FAILED;
        } else if (status == SagaStatus.STARTED) {
            // No terminal signal was emitted; treat as completed if no failure.
            status = SagaStatus.COMPLETED;
            completed = true;
        }
        SagaStatus finalStatus = status;
        boolean success = (finalStatus == SagaStatus.COMPLETED);
        return new SagaResult(success, finalStatus, name, null, failureCause,
                completedSteps.size(), participants.size());
    }

    public List<String> getCompletedSteps() {
        return completedSteps;
    }

    public List<String> getCompensatedSteps() {
        return compensatedSteps;
    }
}
