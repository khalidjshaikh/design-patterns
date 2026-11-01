package com.artificialintelligence.patterns.messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MessagingTest {

    @Test
    void pointToPointSendReceive() {
        PointToPointChannel ch = new PointToPointChannel("q");
        ch.send(Message.of("hello"));
        Message m = ch.receive(100);
        assertNotNull(m);
        assertEquals("hello", m.payload());
        assertTrue(ch.isEmpty());
    }

    @Test
    void pointToPointReceiveTimeout() {
        PointToPointChannel ch = new PointToPointChannel("q");
        assertNull(ch.receive(50));
    }

    @Test
    void topicBroadcastsToSubscribers() throws InterruptedException {
        TopicChannel topic = new TopicChannel("t");
        MessageChannel s1 = topic.subscribeChannel();
        MessageChannel s2 = topic.subscribeChannel();
        topic.publish(Message.of("hi"));
        assertEquals("hi", s1.receive(100).payload());
        assertEquals("hi", s2.receive(100).payload());
    }

    @Test
    void topicPushListener() throws InterruptedException {
        TopicChannel topic = new TopicChannel("t");
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger count = new AtomicInteger();
        topic.subscribe(m -> { count.incrementAndGet(); latch.countDown(); });
        topic.send(Message.of("x"));
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(1, count.get());
    }

    @Test
    void brokerCreateAndSend() {
        InMemoryMessageBroker broker = new InMemoryMessageBroker();
        broker.createQueue("q");
        broker.send("q", Message.of("a"));
        assertEquals("a", broker.receive("q", 100).payload());
        assertTrue(broker.queueNames().contains("q"));
        broker.shutdown();
    }

    @Test
    void routerRoutesByContent() {
        PointToPointChannel high = new PointToPointChannel("high");
        PointToPointChannel low = new PointToPointChannel("low");
        ContentBasedRouter router = new ContentBasedRouter();
        router.addRoute(m -> "high".equals(m.header("p")), high);
        router.setDefaultChannel(low);
        router.route(Message.builder().payload("u").header("p", "high").build());
        router.route(Message.of("n"));
        assertEquals("u", high.receive(100).payload());
        assertEquals("n", low.receive(100).payload());
    }

    @Test
    void filterRejects() {
        PointToPointChannel target = new PointToPointChannel("t");
        DeadLetterChannel dlq = new DeadLetterChannel("dlq");
        FilteringChannel fc = new FilteringChannel(target, m -> m.payload() instanceof String, dlq);
        fc.send(Message.of("ok"));
        fc.send(Message.of(5));
        assertEquals("ok", target.receive(100).payload());
        assertEquals(5, dlq.receive(100).payload());
    }

    @Test
    void translatorTransforms() {
        PointToPointChannel target = new PointToPointChannel("t");
        TranslatingChannel tc = new TranslatingChannel(target, m ->
                Message.builder().payload(String.valueOf(m.payload()).toUpperCase()).build());
        tc.send(Message.of("hi"));
        assertEquals("HI", target.receive(100).payload());
    }

    @Test
    void requestReply() throws InterruptedException {
        PointToPointChannel requests = new PointToPointChannel("req");
        InMemoryMessageBroker broker = new InMemoryMessageBroker();
        Thread responder = RequestReplyTemplate.startResponder(requests, req ->
                Message.of("reply", "pong:" + req.payload()), broker);
        // give thread time
        Thread.sleep(50);
        RequestReplyTemplate client = new RequestReplyTemplate(requests, broker);
        Message reply = client.request(Message.of("ping"), 2000);
        assertNotNull(reply);
        assertEquals("pong:ping", reply.payload());
        responder.interrupt();
        broker.shutdown();
    }

    @Test
    void splitterAndAggregator() {
        MessageSplitter splitter = new MessageSplitter();
        List<Message> parts = splitter.splitCollection(Message.of(List.of("a", "b", "c")));
        assertEquals(3, parts.size());

        MessageAggregator agg = new MessageAggregator(2);
        assertNull(agg.add(Message.builder().payload("x").correlationId("g").build()));
        Message aggregated = agg.add(Message.builder().payload("y").correlationId("g").build());
        assertNotNull(aggregated);
    }

    @Test
    void idempotentReceiver() {
        IdempotentReceiver r = new IdempotentReceiver();
        Message m = Message.of("once");
        assertTrue(r.process(m, x -> {}));
        assertFalse(r.process(m, x -> {}));
    }

    @Test
    void messageStore() {
        MessageStore store = new MessageStore();
        Message m = Message.of("v");
        store.save(m);
        assertEquals(m, store.get(m.id()));
        assertEquals(1, store.size());
    }

    @Test
    void producerConsumer() {
        PointToPointChannel ch = new PointToPointChannel("q");
        MessageProducer p = new MessageProducer(ch);
        MessageConsumer c = new MessageConsumer(ch);
        p.send("hello");
        assertEquals("hello", c.receive(100).payload());
    }

    @Test
    void brokerTopic() {
        InMemoryMessageBroker broker = new InMemoryMessageBroker();
        MessageChannel topic = broker.createTopic("t");
        MessageChannel sub = ((TopicChannel) topic).subscribeChannel();
        broker.send("t", Message.of("e"));
        assertEquals("e", sub.receive(100).payload());
        broker.shutdown();
    }

    @AfterEach
    void cleanup() {
    }
}