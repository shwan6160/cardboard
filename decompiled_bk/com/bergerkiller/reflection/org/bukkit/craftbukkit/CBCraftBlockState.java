/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

@Deprecated
public class CBCraftBlockState {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.block.CraftBlockState");

    public static Object toTileEntity(BlockState state) {
        return BlockStateConversion.INSTANCE.blockStateToTileEntity(state);
    }

    public static BlockState toBlockState(Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    public static BlockState toBlockState(Object tileEntity) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(tileEntity);
    }
}

