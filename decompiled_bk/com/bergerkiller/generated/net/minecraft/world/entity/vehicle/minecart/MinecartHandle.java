/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.Minecart")
public abstract class MinecartHandle
extends AbstractMinecartHandle {
    public static final MinecartClass T = Template.Class.create(MinecartClass.class, Common.TEMPLATE_RESOLVER);

    public static MinecartHandle createHandle(Object handleInstance) {
        return (MinecartHandle)T.createHandle(handleInstance);
    }

    public static final class MinecartClass
    extends Template.Class<MinecartHandle> {
    }
}

