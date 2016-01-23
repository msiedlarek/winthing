package com.fatico.winthing.messaging;

import com.fatico.winthing.Settings;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Engine implements MqttCallback, MessagePublisher {

    private static final Charset CHARSET = Charsets.UTF_8;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Gson gson;
    private final Registry registry;
    private final String topicPrefix;
    private final IMqttAsyncClient client;
    private final MqttConnectOptions options = new MqttConnectOptions();
    private final Duration reconnectInterval;

    private final Lock runnningLock = new ReentrantLock();
    private final Condition runningCondition = runnningLock.newCondition();

    @Inject
    public Engine(final Gson gson, final Registry registry, final Config config,
            final MqttClientPersistence persistence) throws MqttException {
        String topicPrefix = config.getString(Settings.TOPIC_PREFIX).replaceFirst("^/+", "");
        if (!topicPrefix.isEmpty() && !topicPrefix.endsWith("/")) {
            topicPrefix += "/";
        }
        this.topicPrefix = topicPrefix;

        this.reconnectInterval = Duration.ofSeconds(config.getLong(Settings.RECONNECT_INTERVAL));

        this.gson = Objects.requireNonNull(gson);
        this.registry = Objects.requireNonNull(registry);

        this.client = new MqttAsyncClient(
            config.getString(Settings.BROKER_URL),
            config.getString(Settings.CLIENT_ID),
            persistence
        );
        this.client.setCallback(this);

        {
            final String username = config.getString(Settings.BROKER_USERNAME);
            if (username != null && !username.isEmpty()) {
                this.options.setUserName(username);
            }
        }
        {
            final String password = config.getString(Settings.BROKER_PASSWORD);
            if (password != null && !password.isEmpty()) {
                this.options.setPassword(password.toCharArray());
            }
        }

        this.options.setCleanSession(true);
    }

    public void run() {
        runnningLock.lock();
        try {
            while (true) {
                boolean connected = false;
                try {
                    connect();
                    connected = true;
                } catch (final MqttException exception) {
                    logger.error("Could not connect: {}", exception.getMessage());
                }
                if (connected) {
                    try {
                        runningCondition.await();
                    } catch (final InterruptedException exception) {
                        try {
                            disconnect();
                        } catch (final MqttException disconnectException) {
                            logger.error("Could not disconnect.", disconnectException);
                        }
                        return;
                    }
                }
                logger.info(
                    "Trying to reconnect in {} seconds...",
                    reconnectInterval.getSeconds()
                );
                try {
                    Thread.sleep(reconnectInterval.toMillis());
                } catch (final InterruptedException exception) {
                    return;
                }
            }
        } finally {
            runnningLock.unlock();
        }
    }

    private void connect() throws MqttException {
        if (registry.getWill().isPresent()) {
            final Message will = registry.getWill().get();
            final MqttMessage mqttMessage = serialize(will);
            this.options.setWill(
                topicPrefix + will.getTopic(),
                mqttMessage.getPayload(),
                mqttMessage.getQos(),
                mqttMessage.isRetained()
            );
        }

        logger.info("Connecting to {} as {}...", client.getServerURI(), client.getClientId());
        client.connect(options).waitForCompletion();
        logger.info("Connected.");

        logger.info("Subscribing to topics...");
        for (final Map.Entry<String, QualityOfService> entry
                : registry.getSubscriptions().entrySet()) {
            logger.info("  - {}", topicPrefix + entry.getKey());
            client.subscribe(topicPrefix + entry.getKey(), entry.getValue().ordinal());
        }
        logger.info("Subscribed.");

        logger.info("Sending initial messages...");
        registry.getInitialMessages().stream().forEach(this::publish);

        logger.info("Engine started.");
    }

    private void disconnect() throws MqttException {
        client.disconnect();
    }

    @Override
    public void publish(final Message message) {
        final MqttMessage mqttMessage = serialize(message);
        try {
            client.publish(
                topicPrefix + message.getTopic(),
                mqttMessage
            );
        } catch (final MqttException exception) {
            logger.error("Error while publishing message.", exception);
        }
    }

    @Override
    public void connectionLost(final Throwable throwable) {
        logger.error("Connection lost.");
        runnningLock.lock();
        try {
            runningCondition.signal();
        } finally {
            runnningLock.unlock();
        }
    }

    @Override
    public void messageArrived(String topic, final MqttMessage mqttMessage) throws Exception {
        if (!topic.startsWith(topicPrefix)) {
            return;
        }
        topic = topic.substring(topicPrefix.length());

        final Collection<Consumer<Message>> consumers = registry.getConsumers(topic);
        if (consumers.isEmpty()) {
            return;
        }

        final byte[] payloadBytes = mqttMessage.getPayload();
        final JsonElement payload;
        if (payloadBytes.length == 0) {
            payload = null;
        } else {
            payload = gson.fromJson(new String(payloadBytes, CHARSET), JsonElement.class);
        }

        final Message message = new Message(
            topic,
            payload,
            QualityOfService.values()[mqttMessage.getQos()],
            mqttMessage.isRetained()
        );

        logger.debug(
            "Received: {}({})",
            message.getTopic(),
            message.getPayload().isPresent() ? message.getPayload().get().toString() : ""
        );

        for (final Consumer<Message> consumer : consumers) {
            try {
                consumer.accept(message);
            } catch (final Exception exception) {
                logger.error(
                    "Error while processing {}({}): {}",
                    message.getTopic(),
                    message.getPayload().isPresent() ? message.getPayload().get().toString() : "",
                    exception.getMessage()
                );
            }
        }
    }

    @Override
    public void deliveryComplete(final IMqttDeliveryToken token) {
        // Do nothing.
    }

    private MqttMessage serialize(final Message message) {
        final byte[] payload;
        if (message.getPayload().isPresent()) {
            payload = gson.toJson(message.getPayload().get()).getBytes(CHARSET);
        } else {
            payload = new byte[0];
        }
        final MqttMessage mqttMessage = new MqttMessage(payload);
        mqttMessage.setQos(message.getQualityOfService().ordinal());
        mqttMessage.setRetained(message.isRetained());
        return mqttMessage;
    }

}
