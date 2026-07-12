/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.expr;

import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.CtConstructor;
import com.bergerkiller.mountiplex.dep.javassist.CtMethod;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.CodeIterator;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.MethodInfo;
import com.bergerkiller.mountiplex.dep.javassist.expr.MethodCall;

public class ConstructorCall
extends MethodCall {
    protected ConstructorCall(int pos, CodeIterator i, CtClass decl, MethodInfo m) {
        super(pos, i, decl, m);
    }

    @Override
    public String getMethodName() {
        return this.isSuper() ? "super" : "this";
    }

    @Override
    public CtMethod getMethod() throws NotFoundException {
        throw new NotFoundException("this is a constructor call.  Call getConstructor().");
    }

    public CtConstructor getConstructor() throws NotFoundException {
        return this.getCtClass().getConstructor(this.getSignature());
    }

    @Override
    public boolean isSuper() {
        return super.isSuper();
    }
}

