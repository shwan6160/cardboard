/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.BlockMap
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.collections.BlockMap;
import org.bukkit.block.Block;

public class BlockTimeoutMap
extends BlockMap<Long> {
    private static final long serialVersionUID = 1L;

    public void mark(Block block) {
        super.put(block, (Object)System.currentTimeMillis());
    }

    public boolean isMarked(Block block, long timeout) {
        Long value = (Long)super.get(block);
        return value != null && value + timeout > System.currentTimeMillis();
    }
}

