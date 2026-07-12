/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.scopedpool;

import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.scopedpool.ScopedClassPool;
import com.bergerkiller.mountiplex.dep.javassist.scopedpool.ScopedClassPoolRepository;

public interface ScopedClassPoolFactory {
    public ScopedClassPool create(ClassLoader var1, ClassPool var2, ScopedClassPoolRepository var3);

    public ScopedClassPool create(ClassPool var1, ScopedClassPoolRepository var2);
}

