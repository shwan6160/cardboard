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
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockMap<V>
extends HashMap<BlockLocation, V> {
    private static final long serialVersionUID = 1L;

    public boolean containsKey(World world, IntVector3 coord) {
        return this.containsKey(world.getName(), coord);
    }

    public boolean containsKey(String world, IntVector3 coord) {
        return this.containsKey(world, coord.x, coord.y, coord.z);
    }

    public boolean containsKey(String world, int x, int y, int z) {
        return super.containsKey(new BlockLocation(world, x, y, z));
    }

    public boolean containsKey(Block block) {
        return super.containsKey(new BlockLocation(block));
    }

    public V get(World world, IntVector3 coord) {
        return this.get(world.getName(), coord);
    }

    public V get(String world, IntVector3 coord) {
        return this.get(world, coord.x, coord.y, coord.z);
    }

    public V get(World world, int x, int y, int z) {
        return this.get(world.getName(), x, y, z);
    }

    public V get(String world, int x, int y, int z) {
        return super.get(new BlockLocation(world, x, y, z));
    }

    public V get(Block block) {
        return super.get(new BlockLocation(block));
    }

    public V put(World world, IntVector3 coord, V value) {
        return this.put(world.getName(), coord, value);
    }

    public V put(String world, IntVector3 coord, V value) {
        return this.put(world, coord.x, coord.y, coord.z, value);
    }

    @Override
    public V put(Block block, V value) {
        return super.put(new BlockLocation(block), value);
    }

    public V put(World world, int x, int y, int z, V value) {
        return this.put(world.getName(), x, y, z, value);
    }

    public V put(String world, int x, int y, int z, V value) {
        return super.put(new BlockLocation(world, x, y, z), value);
    }

    public V remove(World world, IntVector3 coord) {
        return this.remove(world.getName(), coord);
    }

    public V remove(String world, IntVector3 coord) {
        return this.remove(world, coord.x, coord.y, coord.z);
    }

    public V remove(World world, int x, int y, int z) {
        return this.remove(world.getName(), x, y, z);
    }

    public V remove(String world, int x, int y, int z) {
        return super.remove(new BlockLocation(world, x, y, z));
    }

    public V remove(Block block) {
        return super.remove(new BlockLocation(block));
    }
}

