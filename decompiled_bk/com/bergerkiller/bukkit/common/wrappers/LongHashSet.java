/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_Iterator_1_14;
import com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_Iterator_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.com.bergerkiller.bukkit.common.internal.LongHashSetHandle;
import java.util.Iterator;

public class LongHashSet
extends BasicWrapper<LongHashSetHandle>
implements Iterable<Long>,
Cloneable {
    public LongHashSet() {
        this.setHandle(LongHashSetHandle.createNew());
    }

    public LongHashSet(int size) {
        this.setHandle(LongHashSetHandle.createNew(size));
    }

    public LongHashSet(Object handle) {
        this.setHandle(LongHashSetHandle.createHandle(handle));
    }

    @Override
    public Iterator<Long> iterator() {
        return ((LongHashSetHandle)this.handle).iterator();
    }

    public boolean add(int msw, int lsw) {
        return ((LongHashSetHandle)this.handle).add(MathUtil.toLong(msw, lsw));
    }

    public boolean add(long value) {
        return ((LongHashSetHandle)this.handle).add(value);
    }

    public boolean contains(int msw, int lsw) {
        return ((LongHashSetHandle)this.handle).contains(MathUtil.toLong(msw, lsw));
    }

    public boolean contains(long value) {
        return ((LongHashSetHandle)this.handle).contains(value);
    }

    public boolean remove(int msw, int lsw) {
        return ((LongHashSetHandle)this.handle).remove(MathUtil.toLong(msw, lsw));
    }

    public boolean remove(long value) {
        return ((LongHashSetHandle)this.handle).remove(value);
    }

    public void clear() {
        ((LongHashSetHandle)this.handle).clear();
    }

    public long[] toArray() {
        return ((LongHashSetHandle)this.handle).toArray();
    }

    public long popFirst() {
        return ((LongHashSetHandle)this.handle).popFirstElement();
    }

    public long[] popAll() {
        return ((LongHashSetHandle)this.handle).popAll();
    }

    public void trim() {
        ((LongHashSetHandle)this.handle).trim();
    }

    public boolean isEmpty() {
        return ((LongHashSetHandle)this.handle).isEmpty();
    }

    public int size() {
        return Math.max(((LongHashSetHandle)this.handle).size(), 0);
    }

    public LongIterator longIterator() {
        if (CommonCapabilities.UTIL_COLLECTIONS_REMOVED) {
            return new LongHashSet_Iterator_1_14(this.getRawHandle());
        }
        return new LongHashSet_Iterator_1_8_to_1_13_2(this.getRawHandle());
    }

    public LongHashSet clone() {
        return new LongHashSet(LogicUtil.clone(this.getRawHandle()));
    }

    public static abstract class LongIterator {
        public abstract boolean hasNext();

        public abstract long next();

        public abstract void remove();
    }
}

