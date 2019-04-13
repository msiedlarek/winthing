package com.fatico.winthing.windows.jna;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({"checkstyle:methodname", "checkstyle:parametername"})
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface User32 extends com.sun.jna.platform.win32.User32 {

    User32 INSTANCE = (User32) Native.load(
        "user32",
        User32.class,
        W32APIOptions.DEFAULT_OPTIONS
    );

    LRESULT SendMessage(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);

}
