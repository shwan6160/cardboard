/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.util.IntHashMapHandle;
import java.util.List;

public class IntHashMap<T>
extends BasicWrapper<IntHashMapHandle>
implements Cloneable {
    public IntHashMap() {
        this.setHandle(IntHashMapHandle.createNew());
    }

    public IntHashMap(Object handle) {
        this.setHandle(IntHashMapHandle.createHandle(handle));
    }

    private IntHashMap(IntHashMapHandle wrappedHandle) {
        this.setHandle(wrappedHandle);
    }

    public T get(int key) {
        return (T)((IntHashMapHandle)this.handle).get(key);
    }

    public boolean contains(int key) {
        return ((IntHashMapHandle)this.handle).containsKey(key);
    }

    public T remove(int key) {
        return (T)((IntHashMapHandle)this.handle).remove(key);
    }

    public void put(int key, Object value) {
        ((IntHashMapHandle)this.handle).put(key, value);
    }

    public void clear() {
        ((IntHashMapHandle)this.handle).clear();
    }

    public Entry<T> getEntry(int key) {
        Object entryHandle = ((IntHashMapHandle)this.handle).getEntry(key);
        if (entryHandle == null) {
            return null;
        }
        return new Entry(entryHandle);
    }

    public List<Entry<T>> entries() {
        return (List)LogicUtil.unsafeCast(((IntHashMapHandle)this.handle).getEntries());
    }

    public List<T> values() {
        return ((IntHashMapHandle)this.handle).getValues();
    }

    public int size() {
        return ((IntHashMapHandle)this.handle).size();
    }

    public IntHashMap<T> clone() {
        return new IntHashMap<T>(((IntHashMapHandle)this.handle).cloneMap());
    }

    public static final class Entry<T>
    extends BasicWrapper<IntHashMapHandle.IntHashMapEntryHandle> {
        public Entry(Object handle) {
            this.setHandle(IntHashMapHandle.IntHashMapEntryHandle.createHandle(handle));
        }

        public int getKey() {
            return ((IntHashMapHandle.IntHashMapEntryHandle)this.handle).getKey();
        }

        public T getValue() {
            return (T)((IntHashMapHandle.IntHashMapEntryHandle)this.handle).getValue();
        }

        public void setValue(T value) {
            ((IntHashMapHandle.IntHashMapEntryHandle)this.handle).setValue(value);
        }
    }
}

