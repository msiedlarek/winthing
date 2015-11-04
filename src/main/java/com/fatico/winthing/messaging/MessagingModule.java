package com.fatico.winthing.messaging;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MessagingModule extends PrivateModule {

    @Override
    protected void configure() {
        bind(MqttClientPersistence.class).to(MemoryPersistence.class).in(Singleton.class);

        bind(Registry.class).to(SimpleRegistry.class).in(Singleton.class);

        bind(Engine.class).in(Singleton.class);
        bind(MessagePublisher.class).to(Engine.class);

        expose(Registry.class);
        expose(MessagePublisher.class);
        expose(Engine.class);
    }

}
