/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.level.chunk.DataLayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSNibbleArray {
    public static final ClassTemplate<?> T = ClassTemplate.create(DataLayerHandle.T.getType());
    public static final FieldAccessor<byte[]> array = DataLayerHandle.T.dataField.toFieldAccessor();

    public static int copyTo(Object nibbleArray, byte[] destArray, int offset) {
        byte[] data = array.get(nibbleArray);
        System.arraycopy(data, 0, destArray, offset, data.length);
        return data.length + offset;
    }

    public static byte[] getArrayCopy(Object nibbleArray) {
        byte[] data = array.get(nibbleArray);
        byte[] rval = new byte[data.length];
        NMSNibbleArray.copyTo(nibbleArray, rval, 0);
        return rval;
    }

    public static byte[] getValueArray(Object nibbleArray) {
        return array.get(nibbleArray);
    }
}

