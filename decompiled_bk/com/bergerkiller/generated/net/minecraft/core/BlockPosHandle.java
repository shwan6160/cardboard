/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.core.Vec3iHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.Block;

@Template.InstanceType(value="net.minecraft.core.BlockPos")
public abstract class BlockPosHandle
extends Vec3iHandle {
    public static final BlockPosClass T = Template.Class.create(BlockPosClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockPosHandle createHandle(Object handleInstance) {
        return (BlockPosHandle)T.createHandle(handleInstance);
    }

    public static final BlockPosHandle createNew(int x, int y, int z) {
        return BlockPosHandle.T.constr_x_y_z.newInstance(x, y, z);
    }

    public static Object fromIntVector3Raw(IntVector3 vector) {
        return BlockPosHandle.T.fromIntVector3Raw.invoker.invoke(null, vector);
    }

    public static Object fromBukkitBlockRaw(Block block) {
        return BlockPosHandle.T.fromBukkitBlockRaw.invoker.invoke(null, block);
    }

    public static BlockPosHandle fromIntVector3(IntVector3 vector) {
        return BlockPosHandle.createHandle(BlockPosHandle.fromIntVector3Raw(vector));
    }

    public static BlockPosHandle fromBukkitBlock(Block block) {
        return BlockPosHandle.createHandle(BlockPosHandle.fromBukkitBlock(block));
    }

    public static final class BlockPosClass
    extends Template.Class<BlockPosHandle> {
        public final Template.Constructor.Converted<BlockPosHandle> constr_x_y_z = new Template.Constructor.Converted();
        public final Template.StaticMethod<Object> fromIntVector3Raw = new Template.StaticMethod();
        public final Template.StaticMethod<Object> fromBukkitBlockRaw = new Template.StaticMethod();
    }
}

