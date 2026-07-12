/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.actions.BlockAction;
import org.bukkit.block.Block;

public class BlockActionSetLevers
extends BlockAction {
    private final boolean down;

    public BlockActionSetLevers(TrainCarts plugin, Block block, boolean down) {
        super(plugin, block);
        this.down = down;
    }

    @Override
    public void start() {
        if (this.getBlock() != null) {
            BlockUtil.setLeversAroundBlock((Block)this.getBlock(), (boolean)this.down);
        }
    }
}

