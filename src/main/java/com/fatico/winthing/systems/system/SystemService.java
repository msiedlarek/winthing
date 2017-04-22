package com.fatico.winthing.systems.system;

import com.fatico.winthing.windows.SystemException;
import com.fatico.winthing.windows.jna.Advapi32;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Tlhelp32;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SystemService {

    private static final List<String> REQUIRED_PRIVILEGES = ImmutableList.of(
        WinNT.SE_SHUTDOWN_NAME
    );

    private final Kernel32 kernel32;
    private final Advapi32 advapi32;
    private final Shell32 shell32;

    @Inject
    public SystemService(final Kernel32 kernel32, final Advapi32 advapi32,
            final Shell32 shell32) throws SystemException {
        this.kernel32 = Objects.requireNonNull(kernel32);
        this.advapi32 = Objects.requireNonNull(advapi32);
        this.shell32 = Objects.requireNonNull(shell32);
        escalatePrivileges(REQUIRED_PRIVILEGES);
    }

    public void shutdown() throws SystemException {
        final boolean success = advapi32.InitiateSystemShutdown(
            null,
            null,
            new WinDef.DWORD(0),
            true,
            false
        );
        if (!success) {
            throw new SystemException(Kernel32Util.formatMessage(kernel32.GetLastError()));
        }
    }

    public void reboot() throws SystemException {
        final boolean success = advapi32.InitiateSystemShutdown(
            null,
            null,
            new WinDef.DWORD(0),
            true,
            true
        );
        if (!success) {
            throw new SystemException(Kernel32Util.formatMessage(kernel32.GetLastError()));
        }
    }

    public void run(final String command, final String parameters, final String workingDirectory)
            throws SystemException {
        final WinDef.INT_PTR result = shell32.ShellExecute(
            null,
            "open",
            Objects.requireNonNull(command),
            Objects.requireNonNull(parameters),
            workingDirectory,
            WinUser.SW_SHOWNORMAL
        );
        if (result.intValue() <= 32) {
            throw new SystemException("Could not run command: " + command + " " + parameters);
        }
    }

    public void open(final String uri) throws SystemException {
        final WinDef.INT_PTR result = shell32.ShellExecute(
            null,
            "open",
            Objects.requireNonNull(uri),
            null,
            null,
            WinUser.SW_SHOWNORMAL
        );
        if (result.intValue() <= 32) {
            throw new SystemException("Cannot open URI: " + uri);
        }
    }

    public Map<Integer, String> findProcesses(final String nameFragment) {
        Objects.requireNonNull(nameFragment);

        final String lowercaseNameFragment = nameFragment.toLowerCase();
        final Map<Integer, String> processIds = new HashMap<>();

        final WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(
            Tlhelp32.TH32CS_SNAPPROCESS,
            null
        );
        try {
            final Tlhelp32.PROCESSENTRY32.ByReference entryReference =
                new Tlhelp32.PROCESSENTRY32.ByReference();
            if (kernel32.Process32First(snapshot, entryReference)) {
                while (kernel32.Process32Next(snapshot, entryReference)) {
                    final String processName = new String(entryReference.szExeFile).trim();
                    if (processName.toLowerCase().contains(lowercaseNameFragment)) {
                        processIds.put(entryReference.th32ProcessID.intValue(), processName);
                    }
                }
            }
        } finally {
            kernel32.CloseHandle(snapshot);
        }

        return processIds;
    }

    private void escalatePrivileges(final List<String> requiredPrivilegeNames)
            throws SystemException {
        final WinNT.HANDLE accessToken;
        {
            final WinNT.HANDLEByReference tokenReference = new WinNT.HANDLEByReference();
            final boolean success = advapi32.OpenProcessToken(
                kernel32.GetCurrentProcess(),
                WinNT.TOKEN_ADJUST_PRIVILEGES | WinNT.TOKEN_QUERY,
                tokenReference
            );
            if (!success) {
                throw new SystemException("Cannot open access token");
            }
            accessToken = tokenReference.getValue();
        }

        final WinNT.TOKEN_PRIVILEGES privileges = new WinNT.TOKEN_PRIVILEGES(
            requiredPrivilegeNames.size()
        );
        {
            privileges.PrivilegeCount.setValue(requiredPrivilegeNames.size());
            int index = 0;
            for (final String privilegeName : requiredPrivilegeNames) {
                final WinNT.LUID luid = new WinNT.LUID();
                {
                    final boolean success = advapi32.LookupPrivilegeValue(
                        null,
                        privilegeName,
                        luid
                    );
                    if (!success) {
                        throw new SystemException("Cannot find privilege " + privilegeName);
                    }
                }
                privileges.Privileges[index] = new WinNT.LUID_AND_ATTRIBUTES();
                privileges.Privileges[index].Luid = luid;
                privileges.Privileges[index].Attributes.setValue(WinNT.SE_PRIVILEGE_ENABLED);
                index++;
            }
        }

        {
            final boolean success = advapi32.AdjustTokenPrivileges(
                accessToken,
                false,
                privileges,
                privileges.size(),
                null,
                null
            );
            if (!success) {
                throw new SystemException(
                    "Cannot obtain required privileges: "
                        + Kernel32Util.formatMessage(kernel32.GetLastError())
                );
            }
        }
    }

}
