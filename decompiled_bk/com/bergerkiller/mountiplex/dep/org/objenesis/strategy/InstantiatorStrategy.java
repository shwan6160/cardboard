/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.strategy;

import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;

public interface InstantiatorStrategy {
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> var1);
}

