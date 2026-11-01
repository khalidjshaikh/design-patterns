package com.artificialintelligence.patterns.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Routes messages to channels based on content predicates.
 */
public class ContentBasedRouter {

    public static final class Route {
        private final Predicate<Message> predicate;
        private final MessageChannel target;

        public Route(Predicate<Message> predicate, MessageChannel target) {
            this.predicate = predicate;
            this.target = target;
        }

        public Predicate<Message> predicate() { return predicate; }
        public MessageChannel target() { return target; }
    }

    private final List<Route> routes = new ArrayList<>();
    private MessageChannel defaultChannel;

    public void addRoute(Predicate<Message> predicate, MessageChannel target) {
        if (predicate == null || target == null) {
            throw new IllegalArgumentException("predicate and target must not be null");
        }
        routes.add(new Route(predicate, target));
    }

    public void addRoute(Predicate<Message> predicate, MessageChannel target, int index) {
        if (predicate == null || target == null) {
            throw new IllegalArgumentException("predicate and target must not be null");
        }
        routes.add(index, new Route(predicate, target));
    }

    public void setDefaultChannel(MessageChannel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public void route(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        for (Route route : routes) {
            if (route.predicate().test(message)) {
                route.target().send(message);
                return;
            }
        }
        if (defaultChannel != null) {
            defaultChannel.send(message);
        }
    }

    public List<Route> routes() {
        return new ArrayList<>(routes);
    }

    public int size() {
        return routes.size();
    }
}