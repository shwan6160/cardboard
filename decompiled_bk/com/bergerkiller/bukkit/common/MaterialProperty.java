/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public abstract class MaterialProperty<T> {
    public T get(BlockData blockData) {
        return this.get(blockData.getType());
    }

    public T get(ItemStack item) {
        return item == null ? this.get(Material.AIR) : this.get(item.getType());
    }

    public T get(Block block) {
        return block == null ? this.get(Material.AIR) : this.get(WorldUtil.getBlockData(block));
    }

    public T get(Chunk chunk, int x, int y, int z) {
        return this.get(ChunkUtil.getBlockData(chunk, x, y, z));
    }

    public T get(World world, int x, int y, int z) {
        return this.get(WorldUtil.getBlockData(world, x, y, z));
    }

    public abstract T get(Material var1);
}

