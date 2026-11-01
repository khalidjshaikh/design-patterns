package com.patterns.saga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Saga orchestrator: executes a list of {@link SagaStep}s in order and, on
 * failure, compensates the steps that completed successfully in reverse order.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * Saga saga = new Saga("order-saga");
 * saga.addStep(new CreateOrderStep());
 * saga.addStep(new ReserveInventoryStep());
 * SagaResult result = saga.execute(new DefaultSagaContext());
 * }</pre>
 */
public class Saga {

    private final String name;
    private final List<SagaStep> steps = new ArrayList<>();
    private final List<SagaListener> listeners = new ArrayList<>();
    private volatile SagaStatus status = SagaStatus.NOT_STARTED;
    private volatile SagaResult lastResult;

    public Saga() {
        this("saga");
    }

    public Saga(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getName() {
        return name;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public SagaResult getLastResult() {
        return lastResult;
    }

    public List<SagaStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public List<SagaListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public Saga addStep(SagaStep step) {
        steps.add(Objects.requireNonNull(step, "step"));
        return this;
    }

    public void add(SagaStep step) {
        addStep(step);
    }

    public Saga addListener(SagaListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
        return this;
    }

    public void removeListener(SagaListener listener) {
        listeners.remove(listener);
    }

    public SagaResult execute() {
        return execute(new DefaultSagaContext());
    }

    public SagaResult execute(SagaContext context) {
        Objects.requireNonNull(context, "context");
        status = SagaStatus.STARTED;
        notifySagaStarted(context);

        List<SagaStep> completed = new ArrayList<>();
        int total = steps.size();

        for (int i = 0; i < steps.size(); i++) {
            SagaStep step = steps.get(i);
            status = SagaStatus.RUNNING;
            notifyStepStarted(step, context);
            try {
                step.execute(context);
                completed.add(step);
                notifyStepCompleted(step, context);
            } catch (Throwable t) {
                status = SagaStatus.COMPENSATING;
                notifyStepFailed(step, context, t);
                boolean compensationOk = compensate(completed, context, true);
                SagaStatus finalStatus = compensationOk ? SagaStatus.COMPENSATED : SagaStatus.FAILED;
                status = finalStatus;
                SagaResult result = new SagaResult(false, finalStatus, name, step.getName(), t,
                        completed.size(), total);
                lastResult = result;
                notifySagaCompleted(context, result);
                return result;
            }
        }

        status = SagaStatus.COMPLETED;
        SagaResult result = new SagaResult(true, SagaStatus.COMPLETED, name, null, null, completed.size(), total);
        lastResult = result;
        notifySagaCompleted(context, result);
        return result;
    }

    public SagaResult execute(java.util.Map<String, Object> initial) {
        return execute(new DefaultSagaContext(initial));
    }

    /**
     * Manually compensate the given steps in reverse order.
     */
    public boolean compensate(List<SagaStep> stepsToCompensate, SagaContext context) {
        return compensate(new ArrayList<>(stepsToCompensate), context, false);
    }

    private boolean compensate(List<SagaStep> completed, SagaContext context, boolean reverse) {
        List<SagaStep> order = new ArrayList<>(completed);
        if (reverse) {
            Collections.reverse(order);
        }
        boolean allOk = true;
        for (SagaStep step : order) {
            notifyCompensationStarted(step, context);
            try {
                step.compensate(context);
                notifyCompensationCompleted(step, context);
            } catch (Throwable t) {
                allOk = false;
                notifyCompensationFailed(step, context, t);
            }
        }
        return allOk;
    }

    // ------------------------------------------------------------------
    // Notifications
    // ------------------------------------------------------------------
    private void notifySagaStarted(SagaContext context) {
        for (SagaListener l : listeners) {
            try {
                l.onSagaStarted(name, context);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyStepStarted(SagaStep step, SagaContext context) {
        for (SagaListener l : listeners) {
            try {
                l.onStepStarted(name, step.getName(), context);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyStepCompleted(SagaStep step, SagaContext context) {
        for (SagaListener l : listeners) {
            try {
                l.onStepCompleted(name, step.getName(), context);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyStepFailed(SagaStep step, SagaContext context, Throwable cause) {
        for (SagaListener l : listeners) {
            try {
                l.onStepFailed(name, step.getName(), context, cause);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyCompensationStarted(SagaStep step, SagaContext context) {
        for (SagaListener l : listeners) {
            try {
                l.onCompensationStarted(name, step.getName(), context);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyCompensationCompleted(SagaStep step, SagaContext context) {
        for (SagaListener l : listeners) {
            try {
                l.onCompensationCompleted(name, step.getName(), context);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyCompensationFailed(SagaStep step, SagaContext context, Throwable cause) {
        for (SagaListener l : listeners) {
            try {
                l.onCompensationFailed(name, step.getName(), context, cause);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifySagaCompleted(SagaContext context, SagaResult result) {
        for (SagaListener l : listeners) {
            try {
                l.onSagaCompleted(name, context, result);
            } catch (Throwable ignored) {
            }
        }
    }
}
