/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.MinecartHopper")
public abstract class MinecartHopperHandle
extends AbstractMinecartHandle {
    public static final MinecartHopperClass T = Template.Class.create(MinecartHopperClass.class, Common.TEMPLATE_RESOLVER);

    public static MinecartHopperHandle createHandle(Object handleInstance) {
        return (MinecartHopperHandle)T.createHandle(handleInstance);
    }

    public abstract boolean suckItems();

    public abstract boolean isSuckingEnabled();

    public abstract void setSuckingEnabled(boolean var1);

    public static final class MinecartHopperClass
    extends Template.Class<MinecartHopperHandle> {
        public final Template.Method<Boolean> suckItems = new Template.Method();
        public final Template.Method<Boolean> isSuckingEnabled = new Template.Method();
        public final Template.Method<Void> setSuckingEnabled = new Template.Method();
    }
}

