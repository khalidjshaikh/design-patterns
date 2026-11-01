package com.artificialintelligence.patterns.messaging;

import java.util.List;
import java.util.Map;

public final class Demo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Point-to-point channel ===");
        PointToPointChannel queue = new PointToPointChannel("orders");
        queue.send(Message.of("order", Map.of("id", "1")));
        queue.send(Message.of("order", Map.of("id", "2")));
        System.out.println(queue.receive().payload());
        System.out.println(queue.receive(100));

        System.out.println("\n=== Publish/subscribe channel ===");
        TopicChannel topic = new TopicChannel("events");
        MessageChannel sub1 = topic.subscribeChannel();
        MessageChannel sub2 = topic.subscribeChannel();
        topic.publish(Message.of("event", "hello"));
        System.out.println("sub1: " + sub1.receive(100).payload());
        System.out.println("sub2: " + sub2.receive(100).payload());

        System.out.println("\n=== Broker ===");
        InMemoryMessageBroker broker = new InMemoryMessageBroker();
        broker.createQueue("tasks");
        broker.send("tasks", Message.of("task", "do-work"));
        System.out.println(broker.receive("tasks", 100).payload());
        broker.shutdown();

        System.out.println("\n=== Content-based router ===");
        PointToPointChannel high = new PointToPointChannel("high");
        PointToPointChannel low = new PointToPointChannel("low");
        ContentBasedRouter router = new ContentBasedRouter();
        router.addRoute(m -> "high".equals(m.header("priority")), high);
        router.setDefaultChannel(low);
        router.route(Message.builder().payload("urgent").header("priority", "high").build());
        router.route(Message.of("normal"));
        System.out.println("high: " + high.receive(100).payload());
        System.out.println("low: " + low.receive(100).payload());

        System.out.println("\n=== Filter & translator ===");
        PointToPointChannel target = new PointToPointChannel("target");
        DeadLetterChannel dlq = new DeadLetterChannel("dlq");
        FilteringChannel filtered = new FilteringChannel(target, m -> m.payload() instanceof String, dlq);
        filtered.send(Message.of("hello"));
        filtered.send(Message.of(42));
        System.out.println("target: " + target.receive(100).payload());
        System.out.println("dlq: " + dlq.receive(100).payload());

        PointToPointChannel target2 = new PointToPointChannel("target2");
        TranslatingChannel translating = new TranslatingChannel(target2, m ->
                Message.builder().payload(String.valueOf(m.payload()).toUpperCase()).headers(m.headers()).build());
        translating.send(Message.of("hello"));
        System.out.println("translated: " + target2.receive(100).payload());

        System.out.println("\n=== Request-reply ===");
        PointToPointChannel requests = new PointToPointChannel("requests");
        InMemoryMessageBroker b2 = new InMemoryMessageBroker();
        b2.createQueue("requests");
        Thread responder = RequestReplyTemplate.startResponder(requests, req -> Message.of("reply", "pong:" + req.payload()), b2);
        // ensure request channel registered in broker
        b2.createQueue("requests");
        RequestReplyTemplate client = new RequestReplyTemplate(requests, b2);
        Message reply = client.request(Message.of("ping"), 2000);
        System.out.println("reply: " + (reply == null ? "null" : reply.payload()));
        responder.interrupt();

        System.out.println("\n=== Splitter & aggregator ===");
        MessageSplitter splitter = new MessageSplitter();
        List<Message> parts = splitter.splitCollection(Message.of(List.of("a", "b", "c")));
        System.out.println("split count: " + parts.size());
        MessageAggregator aggregator = new MessageAggregator(2);
        aggregator.add(Message.builder().payload("x").correlationId("g1").build());
        Message aggregated = aggregator.add(Message.builder().payload("y").correlationId("g1").build());
        System.out.println("aggregated: " + (aggregated == null ? "null" : aggregated.payload()));

        System.out.println("\n=== Idempotent receiver ===");
        IdempotentReceiver idempotent = new IdempotentReceiver();
        Message m = Message.of("once");
        System.out.println("first: " + idempotent.process(m, x -> System.out.println("processing " + x.payload())));
        System.out.println("second: " + idempotent.process(m, x -> System.out.println("should not run")));

        System.out.println("\nDone.");
    }
}