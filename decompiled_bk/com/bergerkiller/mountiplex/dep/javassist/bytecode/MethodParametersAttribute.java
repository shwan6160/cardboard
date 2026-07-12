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

public class MethodParametersAttribute
extends AttributeInfo {
    public static final String tag = "MethodParameters";

    MethodParametersAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    public MethodParametersAttribute(ConstPool cp, String[] names, int[] flags) {
        super(cp, tag);
        byte[] data = new byte[names.length * 4 + 1];
        data[0] = (byte)names.length;
        for (int i = 0; i < names.length; ++i) {
            String name = names[i];
            ByteArray.write16bit(name == null ? 0 : cp.addUtf8Info(name), data, i * 4 + 1);
            ByteArray.write16bit(flags[i], data, i * 4 + 3);
        }
        this.set(data);
    }

    public int size() {
        return this.info[0] & 0xFF;
    }

    public int name(int i) {
        return ByteArray.readU16bit(this.info, i * 4 + 1);
    }

    public String parameterName(int i) {
        int index = this.name(i);
        return index == 0 ? null : this.getConstPool().getUtf8Info(index);
    }

    public int accessFlags(int i) {
        return ByteArray.readU16bit(this.info, i * 4 + 3);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        int s = this.size();
        ConstPool cp = this.getConstPool();
        String[] names = new String[s];
        int[] flags = new int[s];
        for (int i = 0; i < s; ++i) {
            int index = this.name(i);
            names[i] = index == 0 ? null : cp.getUtf8Info(index);
            flags[i] = this.accessFlags(i);
        }
        return new MethodParametersAttribute(newCp, names, flags);
    }
}

