/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic;

import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;

@Instantiator(value=Typology.NOT_COMPLIANT)
public class NullInstantiator<T>
implements ObjectInstantiator<T> {
    public NullInstantiator(Class<T> type) {
    }

    @Override
    public T newInstance() {
        return null;
    }
}

