/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisBase;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.SerializingInstantiatorStrategy;

public class ObjenesisSerializer
extends ObjenesisBase {
    public ObjenesisSerializer() {
        super(new SerializingInstantiatorStrategy());
    }

    public ObjenesisSerializer(boolean useCache) {
        super(new SerializingInstantiatorStrategy(), useCache);
    }
}

