/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeRegular;
import org.bukkit.block.Block;

public class RailTypeDetector
extends RailTypeRegular {
    @Override
    public boolean isRail(BlockData blockData) {
        return blockData.isType(RailTypeRegular.RailMaterials.DETECTOR);
    }

    @Override
    public boolean hasBlockActivation(Block railBlock) {
        return true;
    }
}

