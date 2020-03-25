package com.fatico.winthing.messaging;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

class SimpleRegistry implements Registry {

    private static final QualityOfService DEFAULT_QOS = QualityOfService.EXACTLY_ONCE;

    private final Map<String, QualityOfService> topicQos = new HashMap<>();
    private final Multimap<String, Consumer<Message>> consumers = HashMultimap.create();
    private final Queue<Message> initialMessages = new LinkedList<>();
    private Message will = null;

    @Override
    public void subscribe(final String topic, final Consumer<Message> consumer) {
        subscribe(topic, consumer, DEFAULT_QOS);
    }

    @Override
    public void subscribe(final String topic, final Consumer<Message> consumer,
            final QualityOfService qos) {
        topicQos.compute(topic, (currentTopic, currentQos) -> {
            if (currentQos == null || qos.compareTo(currentQos) > 0) {
                return qos;
            } else {
                return currentQos;
            }
        });
        consumers.put(topic, consumer);
    }

    @Override
    public Map<String, QualityOfService> getSubscriptions() {
        return ImmutableMap.copyOf(topicQos);
    }

    @Override
    public Collection<Consumer<Message>> getConsumers(final String topic) {
        return consumers.get(topic);
    }

    @Override
    public void queueInitialMessage(final Message message) {
        initialMessages.add(message);
    }

    @Override
    public Queue<Message> getInitialMessages() {
        return initialMessages;
    }

    @Override
    public void setWill(final Message message) {
        this.will = message;
    }

    @Override
    public Optional<Message> getWill() {
        return Optional.ofNullable(will);
    }

}
