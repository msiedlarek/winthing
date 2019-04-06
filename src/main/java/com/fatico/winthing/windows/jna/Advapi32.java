package com.fatico.winthing.windows.jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface Advapi32 extends com.sun.jna.platform.win32.Advapi32 {

    Advapi32 INSTANCE = (Advapi32) Native.load(
        "advapi32",
        Advapi32.class,
        W32APIOptions.DEFAULT_OPTIONS
    );

    boolean InitiateSystemShutdown(
        String lpMachineName,
        String lpMessage,
        WinDef.DWORD dwTimeout,
        boolean bForceAppsClosed,
        boolean bRebootAfterShutdown
    );

}
