package com.fatico.winthing.systems.keyboard;

import com.fatico.winthing.common.BaseController;
import com.fatico.winthing.messaging.Message;
import com.fatico.winthing.messaging.Registry;
import com.fatico.winthing.windows.input.KeyboardKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class KeyboardController extends BaseController {

    private final KeyboardService keyboardService;

    @Inject
    public KeyboardController(final Registry registry, final KeyboardService keyboardService) {
        super("keyboard");
        this.keyboardService = Objects.requireNonNull(keyboardService);
        registry.subscribe(prefix + "commands/press_keys", this::pressKeys);
    }

    public void pressKeys(final Message message) {
        final List<KeyboardKey> keys;
        try {
            final JsonArray arguments = message.getPayload().get().getAsJsonArray();
            keys = new ArrayList<>(arguments.size());
            for (final JsonElement element : arguments) {
                try {
                    keys.add(KeyboardKey.getByCodename(element.getAsString()));
                } catch (final NoSuchElementException exception) {
                    throw new IllegalArgumentException("Unknown key: " + element.getAsString());
                }
            }
        } catch (final NoSuchElementException | IllegalStateException exception) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        keyboardService.pressKeys(keys);
    }

}
