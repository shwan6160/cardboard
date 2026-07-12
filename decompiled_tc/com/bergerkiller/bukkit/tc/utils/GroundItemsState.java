/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.BlockStateBase
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.InventoryHolder
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.BlockStateBase;
import com.bergerkiller.bukkit.tc.utils.GroundItemsInventory;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

public class GroundItemsState
extends BlockStateBase
implements InventoryHolder {
    private GroundItemsInventory inventory;

    public GroundItemsState(Block block, int radius) {
        super(block);
        this.inventory = new GroundItemsInventory(block, (double)radius + 0.5);
    }

    public GroundItemsInventory getInventory() {
        return this.inventory;
    }
}

