/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.syncher.EntityDataSerializers")
public abstract class EntityDataSerializersHandle
extends Template.Handle {
    public static final EntityDataSerializersClass T = Template.Class.create(EntityDataSerializersClass.class, Common.TEMPLATE_RESOLVER);

    public static EntityDataSerializersHandle createHandle(Object handleInstance) {
        return (EntityDataSerializersHandle)T.createHandle(handleInstance);
    }

    public static int getSerializerId(Object paramDataWatcherSerializer) {
        return EntityDataSerializersHandle.T.getSerializerId.invoke(paramDataWatcherSerializer);
    }

    public static final class EntityDataSerializersClass
    extends Template.Class<EntityDataSerializersHandle> {
        public final Template.StaticMethod.Converted<Integer> getSerializerId = new Template.StaticMethod.Converted();
    }
}

