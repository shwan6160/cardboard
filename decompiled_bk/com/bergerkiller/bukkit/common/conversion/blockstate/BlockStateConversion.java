/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.bukkit.common.conversion.blockstate;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion_1_12_2;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion_1_13;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public abstract class BlockStateConversion {
    public static final BlockStateConversion INSTANCE;

    public abstract Object blockStateToTileEntity(BlockState var1);

    public abstract BlockState blockToBlockState(Block var1);

    public abstract BlockState tileEntityToBlockState(Chunk var1, Object var2);

    public abstract Object getTileEntityFromWorld(Block var1);

    @Deprecated
    public BlockState tileEntityToBlockState(Object nmsTileEntity) {
        return this.tileEntityToBlockState(null, nmsTileEntity);
    }

    static {
        CommonBootstrap.initServer();
        BlockStateConversion inst = null;
        try {
            inst = Common.evaluateMCVersion(">=", "1.13") ? new BlockStateConversion_1_13() : new BlockStateConversion_1_12_2();
        }
        catch (Throwable t) {
            Logging.LOGGER_CONVERSION.log(Level.SEVERE, "Unhandled error initializing block state conversion logic", t);
        }
        INSTANCE = inst;
    }
}

