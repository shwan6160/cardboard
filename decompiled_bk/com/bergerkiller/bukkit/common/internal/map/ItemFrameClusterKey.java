/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ItemFrameHandle;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

class ItemFrameClusterKey {
    public final World world;
    public final BlockFace facing;
    public final int coordinate;

    public ItemFrameClusterKey(ItemFrameHandle itemFrame) {
        this(itemFrame.getBukkitWorld(), itemFrame.getFacing(), itemFrame.getBlockPosition());
    }

    public ItemFrameClusterKey(World world, BlockFace facing, IntVector3 coordinates) {
        this.world = world;
        this.facing = facing;
        this.coordinate = facing.getModX() * coordinates.x + facing.getModY() * coordinates.y + facing.getModZ() * coordinates.z;
    }

    public int hashCode() {
        return this.coordinate + (this.facing.ordinal() << 6);
    }

    public boolean equals(Object o) {
        ItemFrameClusterKey other = (ItemFrameClusterKey)o;
        return other.coordinate == this.coordinate && (other.world == this.world || other.world.equals((Object)this.world)) && other.facing == this.facing;
    }
}

