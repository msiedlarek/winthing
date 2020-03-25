package com.fatico.winthing.systems.keyboard;

import com.fatico.winthing.windows.input.KeyboardKey;
import com.fatico.winthing.windows.jna.User32;
import com.google.inject.Inject;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class KeyboardService {

    private final User32 user32;

    @Inject
    public KeyboardService(final User32 user32) {
        this.user32 = Objects.requireNonNull(user32);
    }

    public void pressKeys(final List<KeyboardKey> keys) {
        if (keys.isEmpty()) {
            return;
        }

        final WinUser.INPUT input = new WinUser.INPUT();
        final WinUser.INPUT[] inputs = (WinUser.INPUT[]) input.toArray(keys.size() * 2);

        final ListIterator<KeyboardKey> iterator = keys.listIterator();
        int index = 0;
        while (iterator.hasNext()) {
            setKeyDown(inputs[index], iterator.next());
            index++;
        }
        while (iterator.hasPrevious()) {
            setKeyUp(inputs[index], iterator.previous());
            index++;
        }

        user32.SendInput(new WinDef.DWORD(inputs.length), inputs, inputs[0].size());
    }

    private void setKeyDown(final WinUser.INPUT input, final KeyboardKey key) {
        input.type.setValue(WinUser.INPUT.INPUT_KEYBOARD);
        input.input.setType(WinUser.KEYBDINPUT.class);
        input.input.ki.wVk = key.getVirtualKeyCode();
    }

    private void setKeyUp(final WinUser.INPUT input, final KeyboardKey key) {
        input.type.setValue(WinUser.INPUT.INPUT_KEYBOARD);
        input.input.setType(WinUser.KEYBDINPUT.class);
        input.input.ki.dwFlags.setValue(WinUser.KEYBDINPUT.KEYEVENTF_KEYUP);
        input.input.ki.wVk = key.getVirtualKeyCode();
    }

}
