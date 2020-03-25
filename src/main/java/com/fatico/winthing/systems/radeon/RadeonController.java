package com.fatico.winthing.systems.radeon;

import com.fatico.winthing.common.BaseController;
import com.fatico.winthing.messaging.Message;
import com.fatico.winthing.messaging.Registry;
import com.google.gson.JsonArray;
import com.google.inject.Inject;
import java.util.NoSuchElementException;
import java.util.Objects;

public class RadeonController extends BaseController {

    private final RadeonService radeonService;

    @Inject
    public RadeonController(final Registry registry, final RadeonService radeonService) {
        super("radeon");
        this.radeonService = Objects.requireNonNull(radeonService);
        registry.subscribe(prefix + "commands/set_best_resolution", this::setBestResolution);
        registry.subscribe(prefix + "commands/set_resolution", this::setResolution);
    }

    public void setBestResolution(final Message message) {
        radeonService.setBestResolution(radeonService.getPrimaryAdapterIndex());
    }

    public void setResolution(final Message message) {
        final int width;
        final int height;
        try {
            final JsonArray arguments = message.getPayload().get().getAsJsonArray();
            width = arguments.get(0).getAsInt();
            height = arguments.get(1).getAsInt();
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        radeonService.setResolution(radeonService.getPrimaryAdapterIndex(), width, height);
    }

}
