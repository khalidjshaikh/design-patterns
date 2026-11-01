# Messaging Pattern (Java)

This module demonstrates enterprise **messaging patterns** in pure Java:

- `Message` — immutable message model (id, type, headers, payload, correlation id, reply-to).
- `PointToPointChannel` / `QueueChannel` / `MessageQueue` — point-to-point channels.
- `TopicChannel` / `PublishSubscribeChannel` / `MessageTopic` — publish/subscribe channels.
- `InMemoryMessageBroker` / `Broker` / `MessageBrokerImpl` — in-memory broker managing queues and topics.
- `MessageProducer` / `Producer` and `MessageConsumer` / `Consumer` — endpoints.
- `ContentBasedRouter` / `MessageRouter` / `Router` — content-based routing.
- `MessageFilter` + `FilteringChannel` — message filtering with dead-letter support.
- `MessageTranslator` + `TranslatingChannel` — message translation.
- `RequestReplyTemplate` / `RequestReply` — request-reply pattern.
- `MessageSplitter`, `MessageAggregator` — splitter and aggregator.
- `IdempotentReceiver`, `MessageStore`, `DeadLetterChannel`.

## Scripts

```bash
./scripts/build.sh
./scripts/test.sh
./scripts/run.sh
```