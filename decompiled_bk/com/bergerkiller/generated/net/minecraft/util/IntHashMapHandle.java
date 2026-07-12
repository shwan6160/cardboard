/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.InstanceType(value="net.minecraft.util.IntHashMap")
public abstract class IntHashMapHandle
extends Template.Handle {
    public static final IntHashMapClass T = Template.Class.create(IntHashMapClass.class, Common.TEMPLATE_RESOLVER);

    public static IntHashMapHandle createHandle(Object handleInstance) {
        return (IntHashMapHandle)T.createHandle(handleInstance);
    }

    public static IntHashMapHandle createNew() {
        return IntHashMapHandle.T.createNew.invoke();
    }

    public abstract Object get(int var1);

    public abstract Object remove(int var1);

    public abstract void put(int var1, Object var2);

    public abstract boolean containsKey(int var1);

    public abstract void clear();

    public abstract int size();

    public abstract Object getEntry(int var1);

    public abstract List<IntHashMap.Entry> getEntries();

    public abstract List<Object> getValues();

    public abstract IntHashMapHandle cloneMap();

    public static final class IntHashMapClass
    extends Template.Class<IntHashMapHandle> {
        public final Template.StaticMethod.Converted<IntHashMapHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Object> get = new Template.Method();
        public final Template.Method<Object> remove = new Template.Method();
        public final Template.Method.Converted<Void> put = new Template.Method.Converted();
        public final Template.Method<Boolean> containsKey = new Template.Method();
        public final Template.Method<Void> clear = new Template.Method();
        public final Template.Method<Integer> size = new Template.Method();
        public final Template.Method<Object> getEntry = new Template.Method();
        public final Template.Method<List<IntHashMap.Entry>> getEntries = new Template.Method();
        public final Template.Method<List<Object>> getValues = new Template.Method();
        public final Template.Method.Converted<IntHashMapHandle> cloneMap = new Template.Method.Converted();
    }

    @Template.InstanceType(value="net.minecraft.util.IntHashMap.IntHashMapEntry")
    public static abstract class IntHashMapEntryHandle
    extends Template.Handle {
        public static final IntHashMapEntryClass T = Template.Class.create(IntHashMapEntryClass.class, Common.TEMPLATE_RESOLVER);

        public static IntHashMapEntryHandle createHandle(Object handleInstance) {
            return (IntHashMapEntryHandle)T.createHandle(handleInstance);
        }

        public abstract int getKey();

        public abstract Object getValue();

        public abstract void setValue(Object var1);

        public static final class IntHashMapEntryClass
        extends Template.Class<IntHashMapEntryHandle> {
            public final Template.Method<Integer> getKey = new Template.Method();
            public final Template.Method<Object> getValue = new Template.Method();
            public final Template.Method<Void> setValue = new Template.Method();
        }
    }
}

