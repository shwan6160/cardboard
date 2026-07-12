/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.actions.Action;
import org.bukkit.block.Block;

public class BlockAction
extends Action {
    private final TrainCarts traincarts;
    private final Block block;

    public BlockAction(TrainCarts traincarts, Block block) {
        this.traincarts = traincarts;
        this.block = block;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    public Block getBlock() {
        return this.block;
    }
}

