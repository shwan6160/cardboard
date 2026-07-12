/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Iterator;

@Template.InstanceType(value="com.bergerkiller.bukkit.common.internal.LongHashSet")
public abstract class LongHashSetHandle
extends Template.Handle {
    public static final LongHashSetClass T = Template.Class.create(LongHashSetClass.class, Common.TEMPLATE_RESOLVER);

    public static LongHashSetHandle createHandle(Object handleInstance) {
        return (LongHashSetHandle)T.createHandle(handleInstance);
    }

    public static LongHashSetHandle createNew(int size) {
        return LongHashSetHandle.T.createNew.invoke(size);
    }

    public abstract Iterator<Long> iterator();

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract void clear();

    public abstract boolean add(long var1);

    public abstract boolean remove(long var1);

    public abstract boolean contains(long var1);

    public abstract long popFirstElement();

    public abstract long[] toArray();

    public abstract long[] popAll();

    public abstract void trim();

    public static LongHashSetHandle createNew() {
        return LongHashSetHandle.createNew(16);
    }

    public static final class LongHashSetClass
    extends Template.Class<LongHashSetHandle> {
        public final Template.StaticMethod.Converted<LongHashSetHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<Iterator<Long>> iterator = new Template.Method.Converted();
        public final Template.Method<Integer> size = new Template.Method();
        public final Template.Method<Boolean> isEmpty = new Template.Method();
        public final Template.Method<Void> clear = new Template.Method();
        public final Template.Method<Boolean> add = new Template.Method();
        public final Template.Method<Boolean> remove = new Template.Method();
        public final Template.Method<Boolean> contains = new Template.Method();
        public final Template.Method<Long> popFirstElement = new Template.Method();
        public final Template.Method<long[]> toArray = new Template.Method();
        public final Template.Method<long[]> popAll = new Template.Method();
        public final Template.Method<Void> trim = new Template.Method();
    }
}

