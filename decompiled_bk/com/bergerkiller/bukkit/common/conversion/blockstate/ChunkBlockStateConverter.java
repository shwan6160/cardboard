/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.bukkit.common.conversion.blockstate;

import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

public class ChunkBlockStateConverter
extends DuplexConverter<Object, BlockState> {
    private final Chunk chunk;

    public ChunkBlockStateConverter(Chunk chunk) {
        super(BlockEntityHandle.T.getType(), BlockState.class);
        this.chunk = chunk;
    }

    @Override
    public BlockState convertInput(Object value) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(this.chunk, value);
    }

    @Override
    public Object convertOutput(BlockState value) {
        return HandleConversion.toTileEntityHandle(value);
    }
}

