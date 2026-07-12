/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.MinecartTNT")
public abstract class MinecartTNTHandle
extends AbstractMinecartHandle {
    public static final MinecartTNTClass T = Template.Class.create(MinecartTNTClass.class, Common.TEMPLATE_RESOLVER);

    public static MinecartTNTHandle createHandle(Object handleInstance) {
        return (MinecartTNTHandle)T.createHandle(handleInstance);
    }

    public abstract void explode(double var1);

    public abstract void prime();

    public abstract int getFuse();

    public abstract void setFuse(int var1);

    public static final class MinecartTNTClass
    extends Template.Class<MinecartTNTHandle> {
        public final Template.Field.Integer fuse = new Template.Field.Integer();
        public final Template.Method<Void> explode = new Template.Method();
        public final Template.Method<Void> prime = new Template.Method();
    }
}

