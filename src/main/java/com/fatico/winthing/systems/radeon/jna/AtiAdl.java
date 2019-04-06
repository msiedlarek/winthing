package com.fatico.winthing.systems.radeon.jna;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings("UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD")
public interface AtiAdl extends Library {

    AtiAdl INSTANCE = (AtiAdl) Native.load(
        (Platform.is64Bit() ? "atiadlxx" : "atiadlxy"),
        AtiAdl.class
    );

    int ADL_OK = 0;

    interface ADL_MAIN_MALLOC_CALLBACK extends Callback {
        Pointer invoke(int size);
    }

    class ADLDisplayID extends Structure {
        public int iDisplayLogicalIndex;
        public int iDisplayPhysicalIndex;
        public int iDisplayLogicalAdapterIndex;
        public int iDisplayPhysicalAdapterIndex;

        @Override
        protected List<String> getFieldOrder() {
            return ImmutableList.of(
                "iDisplayLogicalIndex",
                "iDisplayPhysicalIndex",
                "iDisplayLogicalAdapterIndex",
                "iDisplayPhysicalAdapterIndex"
            );
        }
    }

    class ADLMode extends Structure {
        public int iAdapterIndex;
        public ADLDisplayID displayID;
        public int iXPos;
        public int iYPos;
        public int iXRes;
        public int iYRes;
        public int iColourDepth;
        public float fRefreshRate;
        public int iOrientation;
        public int iModeFlag;
        public int iModeMask;
        public int iModeValue;

        public ADLMode(final Pointer pointer) {
            super(pointer);
        }

        @Override
        protected List<String> getFieldOrder() {
            return ImmutableList.of(
                "iAdapterIndex",
                "displayID",
                "iXPos",
                "iYPos",
                "iXRes",
                "iYRes",
                "iColourDepth",
                "fRefreshRate",
                "iOrientation",
                "iModeFlag",
                "iModeMask",
                "iModeValue"
            );
        }
    }

    int ADL2_Main_Control_Create(
        ADL_MAIN_MALLOC_CALLBACK callback,
        int iEnumConnectedAdapters,
        PointerByReference context
    );

    int ADL2_Main_Control_Destroy(Pointer context);

    int ADL2_Adapter_Primary_Get(
        Pointer context,
        IntByReference lpPrimaryAdapterIndex
    );

    int ADL2_Display_Modes_Set(
        Pointer context,
        int iAdapterIndex,
        int iDisplayIndex,
        int iNumModes,
        ADLMode[] lpModes
    );

    int ADL2_Display_PossibleMode_Get(
        Pointer context,
        int iAdapterIndex,
        IntByReference lpNumModes,
        PointerByReference lppModes
    );

}
