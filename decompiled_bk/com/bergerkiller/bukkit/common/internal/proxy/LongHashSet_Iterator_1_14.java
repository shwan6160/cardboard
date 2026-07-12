/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import java.util.Iterator;

public class LongHashSet_Iterator_1_14
extends LongHashSet.LongIterator {
    private final Iterator<Long> iterator;

    public LongHashSet_Iterator_1_14(Object handle) {
        this.iterator = ((Iterable)handle).iterator();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public long next() {
        return this.iterator.next();
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }
}

