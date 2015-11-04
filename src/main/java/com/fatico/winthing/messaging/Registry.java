package com.fatico.winthing.messaging;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

public interface Registry extends RegistryConfigurator {

    Map<String, QualityOfService> getSubscriptions();

    Collection<Consumer<Message>> getConsumers(String topic);

    Queue<Message> getInitialMessages();

    Optional<Message> getWill();

}
