/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisHelper;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

public class NullInstantiator<T> {
    private final Class<?> type;
    private ObjectInstantiator<?> instantiator = null;

    private NullInstantiator(Class<? extends T> type) {
        this.type = type;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T create() {
        if (this.instantiator == null) {
            NullInstantiator nullInstantiator = this;
            synchronized (nullInstantiator) {
                if (this.instantiator == null) {
                    if (this.type == null) {
                        throw new IllegalStateException("Class is unavailable");
                    }
                    this.instantiator = ObjenesisHelper.getInstantiatorOf(this.type);
                    if (this.instantiator == null) {
                        throw new IllegalStateException("Class of type " + MPLType.getName(this.type) + " could not be instantiated");
                    }
                }
            }
        }
        try {
            return (T)this.instantiator.newInstance();
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    public static <T> NullInstantiator<T> of(Class<? extends T> type) {
        return new NullInstantiator<T>(type);
    }
}

