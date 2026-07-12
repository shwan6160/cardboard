/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import org.bukkit.Chunk;

public class RegionBlockChangeChunkCoordinate {
    public int x;
    public int z;

    public RegionBlockChangeChunkCoordinate(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    public RegionBlockChangeChunkCoordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int hashCode() {
        long i = (long)this.x & 0xFFFFFFFFL | ((long)this.z & 0xFFFFFFFFL) << 32;
        int j = (int)i;
        int k = (int)(i >> 32);
        return j ^ k;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RegionBlockChangeChunkCoordinate) {
            RegionBlockChangeChunkCoordinate iv3 = (RegionBlockChangeChunkCoordinate)object;
            return iv3.x == this.x && iv3.z == this.z;
        }
        return false;
    }

    public String toString() {
        return "{" + this.x + ", " + this.z + "}";
    }
}

