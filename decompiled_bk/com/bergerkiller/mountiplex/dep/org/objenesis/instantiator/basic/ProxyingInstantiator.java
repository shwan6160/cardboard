/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic;

import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic.DelegatingToExoticInstantiator;

@Instantiator(value=Typology.STANDARD)
public class ProxyingInstantiator<T>
extends DelegatingToExoticInstantiator<T> {
    public ProxyingInstantiator(Class<T> type) {
        super("com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.exotic.ProxyingInstantiator", type);
    }
}

