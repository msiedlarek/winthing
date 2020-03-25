package com.fatico.winthing.windows.jna;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({"checkstyle:methodname", "checkstyle:parametername"})
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {

    Kernel32 INSTANCE = (Kernel32) Native.load(
        "kernel32",
        Kernel32.class,
        W32APIOptions.DEFAULT_OPTIONS
    );

    boolean SetSystemPowerState(
        boolean fSuspend,
        boolean fForce
    );

}
