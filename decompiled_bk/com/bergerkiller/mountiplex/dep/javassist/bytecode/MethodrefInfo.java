/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.bytecode;

import com.bergerkiller.mountiplex.dep.javassist.bytecode.ConstPool;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.MemberrefInfo;
import java.io.DataInputStream;
import java.io.IOException;

class MethodrefInfo
extends MemberrefInfo {
    static final int tag = 10;

    public MethodrefInfo(int cindex, int ntindex, int thisIndex) {
        super(cindex, ntindex, thisIndex);
    }

    public MethodrefInfo(DataInputStream in, int thisIndex) throws IOException {
        super(in, thisIndex);
    }

    @Override
    public int getTag() {
        return 10;
    }

    @Override
    public String getTagName() {
        return "Method";
    }

    @Override
    protected int copy2(ConstPool dest, int cindex, int ntindex) {
        return dest.addMethodrefInfo(cindex, ntindex);
    }
}

