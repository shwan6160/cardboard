/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.LongFunction;

@Template.InstanceType(value="org.bukkit.craftbukkit.util.LongObjectHashMap")
public abstract class LongObjectHashMapHandle
extends Template.Handle {
    public static final LongObjectHashMapClass T = Template.Class.create(LongObjectHashMapClass.class, Common.TEMPLATE_RESOLVER);

    public static LongObjectHashMapHandle createHandle(Object handleInstance) {
        return (LongObjectHashMapHandle)T.createHandle(handleInstance);
    }

    public static LongObjectHashMapHandle createNew() {
        return LongObjectHashMapHandle.T.createNew.invoke();
    }

    public abstract void clear();

    public abstract int size();

    public abstract boolean containsKey(long var1);

    public abstract Object get(long var1);

    public abstract Object remove(long var1);

    public abstract Object put(long var1, Object var3);

    public abstract Collection<Object> values();

    public abstract Set<Long> keySet();

    public abstract Object merge(long var1, Object var3, BiFunction<?, ?, ?> var4);

    public abstract Object computeIfAbsent(long var1, LongFunction<?> var3);

    public abstract Object getOrDefault(long var1, Object var3);

    public abstract LongObjectHashMapHandle cloneMap();

    public static final class LongObjectHashMapClass
    extends Template.Class<LongObjectHashMapHandle> {
        public final Template.StaticMethod.Converted<LongObjectHashMapHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Void> clear = new Template.Method();
        public final Template.Method<Integer> size = new Template.Method();
        public final Template.Method<Boolean> containsKey = new Template.Method();
        public final Template.Method<Object> get = new Template.Method();
        public final Template.Method<Object> remove = new Template.Method();
        public final Template.Method<Object> put = new Template.Method();
        public final Template.Method.Converted<Collection<Object>> values = new Template.Method.Converted();
        public final Template.Method.Converted<Set<Long>> keySet = new Template.Method.Converted();
        public final Template.Method<Object> merge = new Template.Method();
        public final Template.Method<Object> computeIfAbsent = new Template.Method();
        public final Template.Method<Object> getOrDefault = new Template.Method();
        public final Template.Method.Converted<LongObjectHashMapHandle> cloneMap = new Template.Method.Converted();
    }
}

