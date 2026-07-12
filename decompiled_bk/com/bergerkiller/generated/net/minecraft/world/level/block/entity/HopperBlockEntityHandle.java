/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

@Template.InstanceType(value="net.minecraft.world.level.block.entity.HopperBlockEntity")
public abstract class HopperBlockEntityHandle
extends BlockEntityHandle {
    public static final HopperBlockEntityClass T = Template.Class.create(HopperBlockEntityClass.class, Common.TEMPLATE_RESOLVER);

    public static HopperBlockEntityHandle createHandle(Object handleInstance) {
        return (HopperBlockEntityHandle)T.createHandle(handleInstance);
    }

    public static boolean suckItems(World world, Object ihopper) {
        return HopperBlockEntityHandle.T.suckItems.invoke(world, ihopper);
    }

    public static final class HopperBlockEntityClass
    extends Template.Class<HopperBlockEntityHandle> {
        public final Template.StaticMethod.Converted<Boolean> suckItems = new Template.StaticMethod.Converted();
    }
}

