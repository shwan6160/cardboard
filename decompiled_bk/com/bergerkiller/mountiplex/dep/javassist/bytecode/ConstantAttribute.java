/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.bytecode;

import com.bergerkiller.mountiplex.dep.javassist.bytecode.AttributeInfo;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.ByteArray;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.ConstPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class ConstantAttribute
extends AttributeInfo {
    public static final String tag = "ConstantValue";

    ConstantAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    public ConstantAttribute(ConstPool cp, int index) {
        super(cp, tag);
        byte[] bvalue = new byte[]{(byte)(index >>> 8), (byte)index};
        this.set(bvalue);
    }

    public int getConstantValue() {
        return ByteArray.readU16bit(this.get(), 0);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        int index = this.getConstPool().copy(this.getConstantValue(), newCp, classnames);
        return new ConstantAttribute(newCp, index);
    }
}

