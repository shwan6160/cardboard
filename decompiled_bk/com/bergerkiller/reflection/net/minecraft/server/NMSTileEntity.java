/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.block.Block;

@Deprecated
public class NMSTileEntity {
    public static final ClassTemplate<?> T = ClassTemplate.create(BlockEntityHandle.T.getType);
    public static final TranslatorFieldAccessor<World> world = BlockEntityHandle.T.world_field.toFieldAccessor();
    public static final TranslatorFieldAccessor<IntVector3> position = BlockEntityHandle.T.position_field.toFieldAccessor();
    public static final MethodAccessor<Void> load = ((Template.Method)BlockEntityHandle.T.load.raw).toMethodAccessor();
    public static final MethodAccessor<Void> save = ((Template.Method)BlockEntityHandle.T.save.raw).toMethodAccessor();

    public static boolean hasWorld(Object tileEntity) {
        return BlockEntityHandle.T.getWorld.invoke(tileEntity) != null;
    }

    public static Object getFromWorld(Block block) {
        return NMSTileEntity.getFromWorld(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static Object getFromWorld(World world, Object blockPosition) {
        return ((Template.Method)LevelHandle.T.getTileEntity.raw).invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    public static Object getFromWorld(World world, int x, int y, int z) {
        return NMSTileEntity.getFromWorld(world, BlockPosHandle.createNew(x, y, z).getRaw());
    }

    public static CommonPacket getUpdatePacket(Object tileEntity) {
        if (tileEntity == null) {
            return null;
        }
        return BlockEntityHandle.T.getUpdatePacket.invoke(tileEntity);
    }

    public static Block getBlock(Object tileEntity) {
        BlockEntityHandle handle = BlockEntityHandle.createHandle(tileEntity);
        BlockPosHandle pos = handle.getPosition();
        return handle.getWorld().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }
}

