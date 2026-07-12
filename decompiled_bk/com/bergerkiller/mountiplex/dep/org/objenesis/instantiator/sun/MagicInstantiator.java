/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.sun;

import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic.DelegatingToExoticInstantiator;

@Instantiator(value=Typology.STANDARD)
public class MagicInstantiator<T>
extends DelegatingToExoticInstantiator<T> {
    public MagicInstantiator(Class<T> type) {
        super("com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.exotic.MagicInstantiator", type);
    }
}

