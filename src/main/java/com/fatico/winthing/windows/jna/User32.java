package com.fatico.winthing.windows.jna;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({"checkstyle:methodname", "checkstyle:parametername"})
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface User32 extends com.sun.jna.platform.win32.User32 {

    User32 INSTANCE = (User32) Native.loadLibrary(
        "user32",
        User32.class,
        W32APIOptions.DEFAULT_OPTIONS
    );

    int EWX_SHUTDOWN = 0x00000001;
    int EWX_REBOOT = 0x00000002;

    int EWX_FORCEIFHUNG = 0x00000010;

    int SHTDN_REASON_MAJOR_OTHER = 0x00000000;
    int SHTDN_REASON_MINOR_OTHER = 0x00000000;
    int SHTDN_REASON_FLAG_PLANNED = 0x80000000;

    LRESULT SendMessage(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);

    boolean ExitWindowsEx(int uFlags, DWORD dwReason);

}
