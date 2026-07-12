/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.dimension;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.dimension.DimensionType")
public abstract class DimensionTypeHandle
extends Template.Handle {
    public static final DimensionTypeClass T = Template.Class.create(DimensionTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static DimensionTypeHandle createHandle(Object handleInstance) {
        return (DimensionTypeHandle)T.createHandle(handleInstance);
    }

    public static Object getDimensionTypeRegistry() {
        return DimensionTypeHandle.T.getDimensionTypeRegistry.invoker.invoke(null);
    }

    public static DimensionTypeHandle fromId(int id) {
        return DimensionTypeHandle.T.fromId.invoke(id);
    }

    public abstract boolean hasSkyLight();

    public abstract int getId();

    public static final class DimensionTypeClass
    extends Template.Class<DimensionTypeHandle> {
        public final Template.StaticMethod<Object> getDimensionTypeRegistry = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<DimensionTypeHandle> fromId = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> hasSkyLight = new Template.Method();
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

