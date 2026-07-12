/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.core.MappedRegistryHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LookupEntityClassMap<K, V>
implements Map<K, V> {
    private final Map<K, V> _base;

    public LookupEntityClassMap(Map<?, ?> base) {
        this._base = base;
    }

    @Override
    public final int size() {
        return this._base.size();
    }

    @Override
    public final boolean isEmpty() {
        return this._base.isEmpty();
    }

    @Override
    public final boolean containsKey(Object key) {
        return this._base.containsKey(LookupEntityClassMap.translate(key));
    }

    @Override
    public final boolean containsValue(Object value) {
        return this._base.containsValue(value);
    }

    @Override
    public final V get(Object key) {
        return this._base.get(LookupEntityClassMap.translate(key));
    }

    @Override
    public final V put(K key, V value) {
        return this._base.put(key, value);
    }

    @Override
    public final V remove(Object key) {
        return this._base.remove(key);
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> m) {
        this._base.putAll(m);
    }

    @Override
    public final void clear() {
        this._base.clear();
    }

    @Override
    public final Set<K> keySet() {
        return this._base.keySet();
    }

    @Override
    public final Collection<V> values() {
        return this._base.values();
    }

    @Override
    public final Set<Map.Entry<K, V>> entrySet() {
        return this._base.entrySet();
    }

    private static Object translate(Object key) {
        if (key instanceof Class) {
            return ClassInterceptor.findBaseType((Class)key);
        }
        return key;
    }

    public static void hook() {
        if (Common.evaluateMCVersion(">=", "1.13")) {
            return;
        }
        if (EntityTypeHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            Map base = (Map)((Template.StaticField)EntityTypeHandle.T.opt_typeNameMap_1_10_2.raw).get();
            LookupEntityClassMap repl = new LookupEntityClassMap(base);
            ((Template.StaticField)EntityTypeHandle.T.opt_typeNameMap_1_10_2.raw).set(repl);
            return;
        }
        MappedRegistryHandle reg = EntityTypeHandle.T.opt_getRegistry.invoke();
        Map<Object, Object> base = MappedRegistryHandle.T.opt_inverseLookupField.get(reg.getRaw());
        LookupEntityClassMap repl = new LookupEntityClassMap(base);
        MappedRegistryHandle.T.opt_inverseLookupField.set(reg.getRaw(), repl);
    }

    public static void unhook() {
        if (Common.evaluateMCVersion(">=", "1.13")) {
            return;
        }
        if (EntityTypeHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            Object orig = ((Template.StaticField)EntityTypeHandle.T.opt_typeNameMap_1_10_2.raw).get();
            if (orig instanceof LookupEntityClassMap) {
                LookupEntityClassMap repl = (LookupEntityClassMap)orig;
                ((Template.StaticField)EntityTypeHandle.T.opt_typeNameMap_1_10_2.raw).set(repl._base);
            }
            return;
        }
        MappedRegistryHandle reg = EntityTypeHandle.T.opt_getRegistry.invoke();
        Map<Object, Object> orig = MappedRegistryHandle.T.opt_inverseLookupField.get(reg.getRaw());
        if (orig instanceof LookupEntityClassMap) {
            LookupEntityClassMap repl = (LookupEntityClassMap)orig;
            MappedRegistryHandle.T.opt_inverseLookupField.set(reg.getRaw(), repl._base);
        }
    }
}

