package com.patterns.saga;

/**
 * A participant in a choreographed (event-driven) saga.
 */
public interface SagaParticipant {

    String getName();

    void register(ChoreographySaga saga, SagaEventBus bus, SagaContext context);
}
