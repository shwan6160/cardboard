/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToggledState
extends AtomicBoolean {
    private static final long serialVersionUID = 1L;

    public ToggledState(boolean initial) {
        super(initial);
    }

    public ToggledState() {
    }

    public boolean set() {
        return this.compareAndSet(false, true);
    }

    public boolean clear() {
        return this.compareAndSet(true, false);
    }
}

