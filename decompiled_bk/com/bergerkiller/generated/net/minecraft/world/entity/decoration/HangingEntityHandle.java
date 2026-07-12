/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.generated.net.minecraft.world.entity.decoration;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;

@Template.InstanceType(value="net.minecraft.world.entity.decoration.HangingEntity")
public abstract class HangingEntityHandle
extends EntityHandle {
    public static final HangingEntityClass T = Template.Class.create(HangingEntityClass.class, Common.TEMPLATE_RESOLVER);

    public static HangingEntityHandle createHandle(Object handleInstance) {
        return (HangingEntityHandle)T.createHandle(handleInstance);
    }

    public abstract void setBlockPositionField(IntVector3 var1);

    public abstract IntVector3 getBlockPosition();

    public abstract BlockFace getFacing();

    public static final class HangingEntityClass
    extends Template.Class<HangingEntityHandle> {
        public final Template.Method.Converted<Void> setBlockPositionField = new Template.Method.Converted();
        public final Template.Method.Converted<IntVector3> getBlockPosition = new Template.Method.Converted();
        public final Template.Method.Converted<BlockFace> getFacing = new Template.Method.Converted();
    }
}

