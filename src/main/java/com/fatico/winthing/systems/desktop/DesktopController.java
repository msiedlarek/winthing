package com.fatico.winthing.systems.desktop;

import com.fatico.winthing.common.BaseController;
import com.fatico.winthing.messaging.Message;
import com.fatico.winthing.messaging.Registry;

import com.google.inject.Inject;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DesktopController extends BaseController {

    private final DesktopService desktopService;

    @Inject
    public DesktopController(final Registry registry, final DesktopService desktopService) {
        super("desktop");
        this.desktopService = Objects.requireNonNull(desktopService);
        registry.subscribe(prefix + "commands/close_active_window", this::closeActiveWindow);
        registry.subscribe(prefix + "commands/set_display_sleep", this::setDisplaySleep);
    }

    public void closeActiveWindow(final Message message) {
        desktopService.getForegroundWindow().ifPresent(desktopService::closeWindow);
    }

    public void setDisplaySleep(final Message message) {
        final boolean sleep;
        try {
            sleep = message.getPayload().get().getAsBoolean();
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        desktopService.setDisplaySleep(sleep);
    }

}
