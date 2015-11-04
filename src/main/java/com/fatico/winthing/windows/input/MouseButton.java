package com.fatico.winthing.windows.input;

import com.sun.jna.platform.win32.WinDef;

public enum MouseButton {

    LEFT(1),
    RIGHT(2),
    MIDDLE(4),
    X1(5),
    X2(6);

    private final WinDef.WORD virtualKeyCode;

    MouseButton(final int virtualKeyCode) {
        assert 0 < virtualKeyCode;
        assert virtualKeyCode < 0xFF;
        this.virtualKeyCode = new WinDef.WORD(virtualKeyCode);
    }

    public WinDef.WORD getVirtualKeyCode() {
        return virtualKeyCode;
    }

}
