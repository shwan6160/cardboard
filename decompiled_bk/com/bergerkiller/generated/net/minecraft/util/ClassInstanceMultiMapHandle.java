/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Iterator;
import java.util.List;

@Template.Optional
@Template.InstanceType(value="net.minecraft.util.ClassInstanceMultiMap")
public abstract class ClassInstanceMultiMapHandle
extends Template.Handle {
    public static final ClassInstanceMultiMapClass T = Template.Class.create(ClassInstanceMultiMapClass.class, Common.TEMPLATE_RESOLVER);

    public static ClassInstanceMultiMapHandle createHandle(Object handleInstance) {
        return (ClassInstanceMultiMapHandle)T.createHandle(handleInstance);
    }

    public static final ClassInstanceMultiMapHandle createNew(Class<?> oclass) {
        return ClassInstanceMultiMapHandle.T.constr_oclass.newInstance(oclass);
    }

    public abstract boolean add(Object var1);

    public abstract boolean remove(Object var1);

    public abstract Iterator iterator();

    public abstract int size();

    public static final class ClassInstanceMultiMapClass
    extends Template.Class<ClassInstanceMultiMapHandle> {
        public final Template.Constructor.Converted<ClassInstanceMultiMapHandle> constr_oclass = new Template.Constructor.Converted();
        @Template.Optional
        public final Template.Field<List<Object>> listValues_1_8_3 = new Template.Field();
        public final Template.Method<Boolean> add = new Template.Method();
        public final Template.Method<Boolean> remove = new Template.Method();
        public final Template.Method<Iterator> iterator = new Template.Method();
        public final Template.Method<Integer> size = new Template.Method();
    }
}

