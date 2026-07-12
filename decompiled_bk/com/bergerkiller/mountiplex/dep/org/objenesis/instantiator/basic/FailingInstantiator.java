/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;

@Instantiator(value=Typology.NOT_COMPLIANT)
public class FailingInstantiator<T>
implements ObjectInstantiator<T> {
    public FailingInstantiator(Class<T> type) {
    }

    @Override
    public T newInstance() {
        throw new ObjenesisException("Always failing");
    }
}

