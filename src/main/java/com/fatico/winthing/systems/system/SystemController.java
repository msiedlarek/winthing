package com.fatico.winthing.systems.system;

import com.fatico.winthing.common.BaseController;
import com.fatico.winthing.messaging.Message;
import com.fatico.winthing.messaging.QualityOfService;
import com.fatico.winthing.messaging.Registry;
import com.fatico.winthing.windows.SystemException;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;

import java.util.NoSuchElementException;
import java.util.Objects;

public class SystemController extends BaseController {

    private final SystemService systemService;

    @Inject
    public SystemController(final Registry registry, final SystemService systemService)
            throws SystemException {
        super("system");
        this.systemService = Objects.requireNonNull(systemService);
        registry.queueInitialMessage(
            makeMessage(
                prefix + "online",
                new JsonPrimitive(true),
                QualityOfService.AT_LEAST_ONCE,
                true
            )
        );
        registry.setWill(
            makeMessage(
                prefix + "online",
                new JsonPrimitive(false),
                QualityOfService.AT_LEAST_ONCE,
                true
            )
        );
        registry.subscribe(prefix + "commands/shutdown", this::shutdown);
        registry.subscribe(prefix + "commands/reboot", this::reboot);
        registry.subscribe(prefix + "commands/run", this::run);
        registry.subscribe(prefix + "commands/open", this::open);
    }

    public void shutdown(final Message message) {
        systemService.shutdown();
    }

    void reboot(final Message message) {
        systemService.reboot();
    }

    public void run(final Message message) {
        final String command;
        final String parameters;
        final String workingDirectory;
        try {
            final JsonArray arguments = message.getPayload().get().getAsJsonArray();
            command = arguments.get(0).getAsString();
            parameters = (arguments.size() > 1 ? arguments.get(1).getAsString() : "");
            workingDirectory = (arguments.size() > 2 ? arguments.get(2).getAsString() : null);
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        systemService.run(command, parameters, workingDirectory);
    }

    public void open(final Message message) {
        final String uri;
        try {
            uri = message.getPayload().get().getAsString();
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        systemService.open(uri);
    }

}
