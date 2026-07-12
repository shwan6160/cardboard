/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.syncher.EntityDataAccessor")
public abstract class EntityDataAccessorHandle
extends Template.Handle {
    public static final EntityDataAccessorClass T = Template.Class.create(EntityDataAccessorClass.class, Common.TEMPLATE_RESOLVER);

    public static EntityDataAccessorHandle createHandle(Object handleInstance) {
        return (EntityDataAccessorHandle)T.createHandle(handleInstance);
    }

    public abstract int getId();

    public abstract Object getSerializer();

    public static final class EntityDataAccessorClass
    extends Template.Class<EntityDataAccessorHandle> {
        public final Template.Method<Integer> getId = new Template.Method();
        public final Template.Method<Object> getSerializer = new Template.Method();
    }
}

