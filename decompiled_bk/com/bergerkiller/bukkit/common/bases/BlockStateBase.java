/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.proxies.BlockStateProxy;
import org.bukkit.block.Block;

public class BlockStateBase
extends BlockStateProxy {
    public BlockStateBase(Block block) {
        super(CommonMethods.CraftBlockState_new(block));
    }
}

