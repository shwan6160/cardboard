/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.MinecartSpawner")
public abstract class MinecartSpawnerHandle
extends AbstractMinecartHandle {
    public static final MinecartSpawnerClass T = Template.Class.create(MinecartSpawnerClass.class, Common.TEMPLATE_RESOLVER);

    public static MinecartSpawnerHandle createHandle(Object handleInstance) {
        return (MinecartSpawnerHandle)T.createHandle(handleInstance);
    }

    public abstract MobSpawner getMobSpawner();

    public abstract void setMobSpawner(MobSpawner var1);

    public static final class MinecartSpawnerClass
    extends Template.Class<MinecartSpawnerHandle> {
        public final Template.Field.Converted<MobSpawner> mobSpawner = new Template.Field.Converted();
    }
}

