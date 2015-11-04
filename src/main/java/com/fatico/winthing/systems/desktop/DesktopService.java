package com.fatico.winthing.systems.desktop;

import com.fatico.winthing.windows.jna.User32;

import com.google.inject.Inject;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;

import java.util.Objects;
import java.util.Optional;

public class DesktopService {

    private static final long SC_MONITORPOWER = 0xF170;

    private final User32 user32;

    @Inject
    public DesktopService(final User32 user32) {
        this.user32 = Objects.requireNonNull(user32);
    }

    public Optional<WinDef.HWND> getForegroundWindow() {
        return Optional.ofNullable(user32.GetForegroundWindow());
    }

    public void setForegroundWindow(final WinDef.HWND window) {
        user32.SetForegroundWindow(window);
    }

    public void closeWindow(final WinDef.HWND window) {
        user32.SendMessage(window, WinUser.WM_CLOSE, null, null);
    }

    public Optional<WinDef.HWND> getMainWindow(final int process) {
        class Callback implements WinUser.WNDENUMPROC {
            public WinDef.HWND foundWindow = null;

            @Override
            public boolean callback(final WinDef.HWND hwnd, final Pointer pointer) {
                final IntByReference processIdReference = new IntByReference();
                user32.GetWindowThreadProcessId(hwnd, processIdReference);
                if (processIdReference.getValue() == process) {
                    foundWindow = hwnd;
                    return false;
                }
                return true;
            }
        }

        final Callback callback = new Callback();
        user32.EnumWindows(callback, null);
        return Optional.ofNullable(callback.foundWindow);
    }

    public void setDisplaySleep(final boolean sleep) {
        user32.DefWindowProc(
            getForegroundWindow().get(),
            WinUser.WM_SYSCOMMAND,
            new WinDef.WPARAM(SC_MONITORPOWER),
            new WinDef.LPARAM(sleep ? 2 : -1)
        );
    }

}
