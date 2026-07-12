/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 */
package com.bergerkiller.bukkit.tc.attachments.ui.block;

import com.bergerkiller.bukkit.common.wrappers.BlockData;

public interface BlockDataSelector {
    public void onSelectedBlockDataChanged(BlockData var1);

    public BlockData getSelectedBlockData();

    public BlockDataSelector setSelectedBlockData(BlockData var1);
}

