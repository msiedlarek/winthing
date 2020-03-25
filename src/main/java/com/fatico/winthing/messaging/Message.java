package com.fatico.winthing.messaging;

import com.google.gson.JsonElement;
import java.util.Objects;
import java.util.Optional;

public class Message {

    private static final JsonElement DEFAULT_PAYLOAD = null;
    private static final QualityOfService DEFAULT_QOS = QualityOfService.AT_MOST_ONCE;
    private static final boolean DEFAULT_RETAINMENT = false;

    private final String topic;
    private final JsonElement payload;
    private final QualityOfService qos;
    private final boolean retained;

    public Message(final String topic) {
        this.topic = Objects.requireNonNull(topic);
        this.payload = DEFAULT_PAYLOAD;
        this.qos = DEFAULT_QOS;
        this.retained = DEFAULT_RETAINMENT;
    }

    public Message(final String topic, final JsonElement payload) {
        this.topic = Objects.requireNonNull(topic);
        this.payload = payload;
        this.qos = DEFAULT_QOS;
        this.retained = DEFAULT_RETAINMENT;
    }

    public Message(final String topic, final JsonElement payload, final QualityOfService qos) {
        this.topic = Objects.requireNonNull(topic);
        this.payload = payload;
        this.qos = Objects.requireNonNull(qos);
        this.retained = DEFAULT_RETAINMENT;
    }

    public Message(final String topic, final JsonElement payload, final QualityOfService qos,
            boolean retained) {
        this.topic = Objects.requireNonNull(topic);
        this.payload = payload;
        this.qos = Objects.requireNonNull(qos);
        this.retained = retained;
    }

    public String getTopic() {
        return topic;
    }

    public Optional<JsonElement> getPayload() {
        return Optional.ofNullable(payload);
    }

    public QualityOfService getQualityOfService() {
        return qos;
    }

    public boolean isRetained() {
        return retained;
    }

}
