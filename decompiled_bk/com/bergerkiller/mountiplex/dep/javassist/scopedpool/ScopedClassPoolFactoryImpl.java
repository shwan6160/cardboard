/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.scopedpool;

import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.scopedpool.ScopedClassPool;
import com.bergerkiller.mountiplex.dep.javassist.scopedpool.ScopedClassPoolFactory;
import com.bergerkiller.mountiplex.dep.javassist.scopedpool.ScopedClassPoolRepository;

public class ScopedClassPoolFactoryImpl
implements ScopedClassPoolFactory {
    @Override
    public ScopedClassPool create(ClassLoader cl, ClassPool src, ScopedClassPoolRepository repository) {
        return new ScopedClassPool(cl, src, repository, false);
    }

    @Override
    public ScopedClassPool create(ClassPool src, ScopedClassPoolRepository repository) {
        return new ScopedClassPool(null, src, repository, true);
    }
}

