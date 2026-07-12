/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import java.util.HashSet;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockSet
extends HashSet<BlockLocation> {
    private static final long serialVersionUID = 1L;

    public boolean contains(Block block) {
        return super.contains(new BlockLocation(block));
    }

    public boolean contains(World world, int x, int y, int z) {
        return this.contains(world.getName(), x, y, z);
    }

    public boolean contains(World world, IntVector3 coord) {
        return this.contains(world.getName(), coord.x, coord.y, coord.z);
    }

    public boolean contains(String world, IntVector3 coord) {
        return this.contains(world, coord.x, coord.y, coord.z);
    }

    public boolean contains(String world, int x, int y, int z) {
        return super.contains(new BlockLocation(world, x, y, z));
    }

    public boolean remove(Block block) {
        return super.remove(new BlockLocation(block));
    }

    public boolean remove(World world, int x, int y, int z) {
        return this.remove(world.getName(), x, y, z);
    }

    public boolean remove(World world, IntVector3 coord) {
        return this.remove(world.getName(), coord.x, coord.y, coord.z);
    }

    public boolean remove(String world, IntVector3 coord) {
        return this.remove(world, coord.x, coord.y, coord.z);
    }

    public boolean remove(String world, int x, int y, int z) {
        return super.remove(new BlockLocation(world, x, y, z));
    }

    @Override
    public boolean add(Block block) {
        return super.add(new BlockLocation(block));
    }

    public boolean add(World world, int x, int y, int z) {
        return this.add(world.getName(), x, y, z);
    }

    public boolean add(World world, IntVector3 coord) {
        return this.add(world.getName(), coord.x, coord.y, coord.z);
    }

    public boolean add(String world, IntVector3 coord) {
        return this.add(world, coord.x, coord.y, coord.z);
    }

    public boolean add(String world, int x, int y, int z) {
        return super.add(new BlockLocation(world, x, y, z));
    }
}

