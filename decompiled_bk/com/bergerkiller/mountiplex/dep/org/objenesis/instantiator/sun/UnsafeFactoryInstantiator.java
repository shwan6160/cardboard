/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.sun;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

@Instantiator(value=Typology.STANDARD)
public class UnsafeFactoryInstantiator<T>
implements ObjectInstantiator<T> {
    private final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private final Class<T> type;

    public UnsafeFactoryInstantiator(Class<T> type) {
        this.type = type;
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.unsafe.allocateInstance(this.type));
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
    }
}

