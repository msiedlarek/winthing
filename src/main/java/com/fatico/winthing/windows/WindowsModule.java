package com.fatico.winthing.windows;

import com.fatico.winthing.windows.jna.Advapi32;
import com.fatico.winthing.windows.jna.User32;

import com.google.inject.PrivateModule;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Shell32;

public class WindowsModule extends PrivateModule {

    @Override
    protected void configure() {
        bind(User32.class).toInstance(User32.INSTANCE);
        expose(User32.class);

        bind(Kernel32.class).toInstance(Kernel32.INSTANCE);
        expose(Kernel32.class);

        bind(Advapi32.class).toInstance(Advapi32.INSTANCE);
        expose(Advapi32.class);

        bind(Shell32.class).toInstance(Shell32.INSTANCE);
        expose(Shell32.class);
    }

}
