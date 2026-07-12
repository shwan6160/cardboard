/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.convert;

import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.BadBytecode;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.CodeAttribute;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.CodeIterator;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.ConstPool;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.MethodInfo;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.Opcode;

public abstract class Transformer
implements Opcode {
    private Transformer next;

    public Transformer(Transformer t) {
        this.next = t;
    }

    public Transformer getNext() {
        return this.next;
    }

    public void initialize(ConstPool cp, CodeAttribute attr) {
    }

    public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException {
        this.initialize(cp, minfo.getCodeAttribute());
    }

    public void clean() {
    }

    public abstract int transform(CtClass var1, int var2, CodeIterator var3, ConstPool var4) throws CannotCompileException, BadBytecode;

    public int extraLocals() {
        return 0;
    }

    public int extraStack() {
        return 0;
    }
}

