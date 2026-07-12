/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist;

import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;

public interface Translator {
    public void start(ClassPool var1) throws NotFoundException, CannotCompileException;

    public void onLoad(ClassPool var1, String var2) throws NotFoundException, CannotCompileException;
}

