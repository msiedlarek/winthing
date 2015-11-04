package com.fatico.winthing.messaging;

import java.util.function.Consumer;

public interface RegistryConfigurator {

    void subscribe(final String topic, final Consumer<Message> consumer);

    void subscribe(final String topic, final Consumer<Message> consumer,
        final QualityOfService qos);

    void queueInitialMessage(final Message message);

    void setWill(final Message message);

}
