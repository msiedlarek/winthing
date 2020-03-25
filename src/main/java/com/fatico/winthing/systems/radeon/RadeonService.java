package com.fatico.winthing.systems.radeon;

import com.fatico.winthing.systems.radeon.jna.AtiAdl;
import com.google.common.collect.ComparisonChain;
import com.google.inject.Inject;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;

@SuppressWarnings({"checkstyle:nofinalizer"})
public class RadeonService {

    private final AtiAdl atiAdl;
    private final Pointer context;

    @Inject
    public RadeonService(final AtiAdl atiAdl) {
        this.atiAdl = Objects.requireNonNull(atiAdl);
        {
            final PointerByReference contextReference = new PointerByReference();
            final int result = atiAdl.ADL2_Main_Control_Create(
                new MallocCallback(),
                1,
                contextReference
            );
            if (result != AtiAdl.ADL_OK) {
                throw new AdlException("ADL2_Main_Control_Create", result);
            }
            this.context = contextReference.getValue();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void finalize() throws Throwable {
        atiAdl.ADL2_Main_Control_Destroy(context);
        super.finalize();
    }

    public int getPrimaryAdapterIndex() {
        final IntByReference adapterIndexReference = new IntByReference();
        final int result = atiAdl.ADL2_Adapter_Primary_Get(
            context,
            adapterIndexReference
        );
        if (result != AtiAdl.ADL_OK) {
            throw new AdlException("ADL2_Display_CustomizedModeListNum_Get", result);
        }
        return adapterIndexReference.getValue();
    }

    public void setBestResolution(final int adapterIndex) {
        final AtiAdl.ADLMode mode = getBestMode(adapterIndex);
        setMode(adapterIndex, mode);
    }

    public void setResolution(final int adapterIndex, final int width, final int height) {
        final AtiAdl.ADLMode mode = getBestMode(adapterIndex);
        mode.iXRes = width;
        mode.iYRes = height;
        setMode(adapterIndex, mode);
    }

    private void setMode(final int adapterIndex, final AtiAdl.ADLMode mode) {
        final int result = atiAdl.ADL2_Display_Modes_Set(
            context,
            adapterIndex,
            -1,
            1,
            (AtiAdl.ADLMode[]) mode.toArray(1)
        );
        if (result != AtiAdl.ADL_OK) {
            throw new AdlException("ADL2_Display_Modes_Set", result);
        }
    }

    private AtiAdl.ADLMode getBestMode(final int adapterIndex) {
        final AtiAdl.ADLMode[] modes;
        {
            final IntByReference numberOfModesReference = new IntByReference();
            final PointerByReference pointer = new PointerByReference();
            final int result = atiAdl.ADL2_Display_PossibleMode_Get(
                context,
                adapterIndex,
                numberOfModesReference,
                pointer
            );
            if (result != AtiAdl.ADL_OK) {
                throw new AdlException("ADL2_Display_Modes_Get", result);
            }
            modes = (AtiAdl.ADLMode[]) new AtiAdl.ADLMode(pointer.getValue()).toArray(
                numberOfModesReference.getValue()
            );
        }
        if (modes.length == 0) {
            throw new NoSuchElementException();
        }
        return Collections.max(Arrays.asList(modes), (left, right) -> ComparisonChain.start()
                .compare(left.iColourDepth, right.iColourDepth)
                .compare(left.iXRes, right.iXRes)
                .compare(left.iYRes, right.iYRes)
                .compare(left.fRefreshRate, right.fRefreshRate)
                .result()
        );
    }

    private static class MallocCallback extends Memory implements AtiAdl.ADL_MAIN_MALLOC_CALLBACK {
        @Override
        public Pointer invoke(int size) {
            return new Pointer(Memory.malloc(size));
        }
    }
}
