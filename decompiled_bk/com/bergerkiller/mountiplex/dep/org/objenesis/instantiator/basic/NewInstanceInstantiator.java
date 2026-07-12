/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic;

import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.util.ClassUtils;

@Instantiator(value=Typology.NOT_COMPLIANT)
public class NewInstanceInstantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;

    public NewInstanceInstantiator(Class<T> type) {
        this.type = type;
    }

    @Override
    public T newInstance() {
        return ClassUtils.newInstance(this.type);
    }
}

