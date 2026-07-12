/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisBase;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.StdInstantiatorStrategy;

public class ObjenesisStd
extends ObjenesisBase {
    public ObjenesisStd() {
        super(new StdInstantiatorStrategy());
    }

    public ObjenesisStd(boolean useCache) {
        super(new StdInstantiatorStrategy(), useCache);
    }
}

