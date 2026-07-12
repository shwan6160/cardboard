/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

@Template.InstanceType(value="net.minecraft.world.level.block.entity.BlockEntity")
public abstract class BlockEntityHandle
extends Template.Handle {
    public static final BlockEntityClass T = Template.Class.create(BlockEntityClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockEntityHandle createHandle(Object handleInstance) {
        return (BlockEntityHandle)T.createHandle(handleInstance);
    }

    public abstract LevelHandle getWorld();

    public abstract BlockPosHandle getPosition();

    public abstract BlockData getBlockDataIfCached();

    public abstract void load(BlockData var1, CommonTagCompound var2);

    public abstract CommonTagCompound save();

    public abstract BlockData getBlockData();

    public abstract Object getRawBlockData();

    public abstract Material getType();

    public abstract CommonPacket getUpdatePacket();

    public abstract boolean isRemoved();

    public BlockState toBukkit() {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(this.getRaw());
    }

    public static BlockEntityHandle fromBukkit(BlockState blockState) {
        return BlockEntityHandle.createHandle(BlockStateConversion.INSTANCE.blockStateToTileEntity(blockState));
    }

    public abstract World getWorld_field();

    public abstract void setWorld_field(World var1);

    public abstract IntVector3 getPosition_field();

    public abstract void setPosition_field(IntVector3 var1);

    public static final class BlockEntityClass
    extends Template.Class<BlockEntityHandle> {
        public final Template.Field.Converted<World> world_field = new Template.Field.Converted();
        public final Template.Field.Converted<IntVector3> position_field = new Template.Field.Converted();
        public final Template.Method.Converted<LevelHandle> getWorld = new Template.Method.Converted();
        public final Template.Method.Converted<BlockPosHandle> getPosition = new Template.Method.Converted();
        public final Template.Method.Converted<BlockData> getBlockDataIfCached = new Template.Method.Converted();
        public final Template.Method.Converted<Void> load = new Template.Method.Converted();
        public final Template.Method.Converted<CommonTagCompound> save = new Template.Method.Converted();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
        public final Template.Method<Object> getRawBlockData = new Template.Method();
        public final Template.Method.Converted<Material> getType = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method<Integer> getLegacyData = new Template.Method();
        public final Template.Method.Converted<CommonPacket> getUpdatePacket = new Template.Method.Converted();
        public final Template.Method<Boolean> isRemoved = new Template.Method();
    }
}

