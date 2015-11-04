package com.fatico.winthing.common;

import com.fatico.winthing.messaging.Message;
import com.fatico.winthing.messaging.QualityOfService;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String prefix;

    public BaseController(String prefix) {
        Objects.requireNonNull(prefix);
        prefix = prefix.replaceFirst("^/+", "");
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix += "/";
        }
        this.prefix = prefix;
    }

    protected Message makeMessage(final String topic) {
        return new Message(prefix + topic);
    }

    protected Message makeMessage(final String topic, final JsonElement payload) {
        return new Message(prefix + topic, payload);
    }

    protected Message makeMessage(final String topic, final JsonElement payload,
        final QualityOfService qos) {
        return new Message(prefix + topic, payload, qos);
    }

    protected Message makeMessage(final String topic, final JsonElement payload,
        final QualityOfService qos, boolean retained) {
        return new Message(prefix + topic, payload, qos, retained);
    }

}
