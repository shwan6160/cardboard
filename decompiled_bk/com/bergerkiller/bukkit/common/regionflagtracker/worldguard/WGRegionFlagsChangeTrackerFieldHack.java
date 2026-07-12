/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 */
package com.bergerkiller.bukkit.common.regionflagtracker.worldguard;

import com.bergerkiller.bukkit.common.regionflagtracker.worldguard.WGRegionFlagsChangeTracker;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class WGRegionFlagsChangeTrackerFieldHack
implements WGRegionFlagsChangeTracker {
    private static final Field FLAGS_FIELD = WGRegionFlagsChangeTrackerFieldHack.getFlagsFieldVerify();
    private ChangeTrackedMap expectedFlags;
    private Map<?, ?> lastFlags;

    public WGRegionFlagsChangeTrackerFieldHack(ProtectedRegion region) throws OptimizationNotSupportedException {
        if (FLAGS_FIELD == null) {
            throw new OptimizationNotSupportedException("ProtectedRegion.flags field is not available");
        }
        this.expectedFlags = WGRegionFlagsChangeTrackerFieldHack.hook(region);
        this.lastFlags = new HashMap<Object, Object>(this.expectedFlags);
    }

    @Override
    public void cleanup(ProtectedRegion region) {
        try {
            if (FLAGS_FIELD == null) {
                throw new IllegalStateException("Flags field not supported");
            }
            Object currFieldValue = FLAGS_FIELD.get(region);
            if (currFieldValue instanceof ChangeTrackedMap) {
                FLAGS_FIELD.set(region, ((ChangeTrackedMap)currFieldValue).base);
            }
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to restore ProtectedRegion.flags field", t);
        }
    }

    @Override
    public boolean update(ProtectedRegion region) {
        if (region.getFlags() != this.expectedFlags) {
            try {
                this.expectedFlags = WGRegionFlagsChangeTrackerFieldHack.hook(region);
            }
            catch (Throwable t) {
                throw new IllegalStateException("Failed to update ProtectedRegion.flags field after modification", t);
            }
        } else if (!this.expectedFlags.changed.getAndSet(false)) {
            return false;
        }
        if (region.getFlags().equals(this.lastFlags)) {
            return false;
        }
        this.lastFlags = new HashMap(region.getFlags());
        return true;
    }

    private static ChangeTrackedMap hook(ProtectedRegion region) throws OptimizationNotSupportedException {
        try {
            ConcurrentMap base = (ConcurrentMap)FLAGS_FIELD.get(region);
            if (base instanceof ChangeTrackedMap) {
                return (ChangeTrackedMap)base;
            }
            ChangeTrackedMap tracked = new ChangeTrackedMap(base);
            FLAGS_FIELD.set(region, tracked);
            if (region.getFlags() != tracked) {
                FLAGS_FIELD.set(region, base);
                throw new IllegalStateException("ProtectedRegion.getFlags() does not return the flags field");
            }
            return tracked;
        }
        catch (Throwable t) {
            throw new OptimizationNotSupportedException("Failed to hook ProtectedRegion.flags field", t);
        }
    }

    private static Field getFlagsFieldVerify() {
        try {
            Field f = ProtectedRegion.class.getDeclaredField("flags");
            if (Modifier.isStatic(f.getModifiers())) {
                return null;
            }
            if (!f.getType().isAssignableFrom(ConcurrentMap.class)) {
                return null;
            }
            f.setAccessible(true);
            return f;
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static class OptimizationNotSupportedException
    extends Exception {
        public OptimizationNotSupportedException(String message) {
            super(message);
        }

        public OptimizationNotSupportedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class ChangeTrackedMap
    extends AbstractMap<Object, Object>
    implements ConcurrentMap<Object, Object> {
        public final ConcurrentMap<Object, Object> base;
        public final AtomicBoolean changed = new AtomicBoolean();

        public ChangeTrackedMap(ConcurrentMap<Object, Object> base) {
            this.base = base;
        }

        @Override
        public int size() {
            return this.base.size();
        }

        @Override
        public boolean isEmpty() {
            return this.base.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.base.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.base.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return this.base.get(key);
        }

        @Override
        public Object getOrDefault(Object key, Object defaultValue) {
            return this.base.getOrDefault(key, defaultValue);
        }

        @Override
        public Object put(Object key, Object value) {
            Object oldValue = this.base.put(key, value);
            this.changed.set(true);
            return oldValue;
        }

        @Override
        public void putAll(Map<?, ?> m) {
            this.base.putAll(m);
            this.changed.set(true);
        }

        @Override
        public Object remove(Object key) {
            Object removed = this.base.remove(key);
            if (removed != null) {
                this.changed.set(true);
            }
            return removed;
        }

        @Override
        public Object putIfAbsent(Object key, Object value) {
            Object result = this.base.putIfAbsent(key, value);
            this.changed.set(true);
            return result;
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (this.base.remove(key, value)) {
                this.changed.set(true);
                return true;
            }
            return false;
        }

        @Override
        public boolean replace(Object key, Object oldValue, Object newValue) {
            boolean result = this.base.replace(key, oldValue, newValue);
            if (result) {
                this.changed.set(true);
            }
            return result;
        }

        @Override
        public Object replace(Object key, Object value) {
            Object result = this.base.replace(key, value);
            this.changed.set(true);
            return result;
        }

        @Override
        public void clear() {
            this.base.clear();
            this.changed.set(true);
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            return this.base.entrySet();
        }

        @Override
        public Set<Object> keySet() {
            return this.base.keySet();
        }

        @Override
        public boolean equals(Object o) {
            return this.base.equals(o);
        }

        @Override
        public int hashCode() {
            return this.base.hashCode();
        }
    }
}

