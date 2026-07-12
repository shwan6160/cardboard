/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class OfflineSignSide {
    private final OfflineBlock block;
    private final boolean front;

    private OfflineSignSide(OfflineBlock block, boolean front) {
        this.block = block;
        this.front = front;
    }

    public static OfflineSignSide of(OfflineBlock signBlock, boolean front) {
        return new OfflineSignSide(signBlock, front);
    }

    public static OfflineSignSide of(Block signBlock, boolean front) {
        return new OfflineSignSide(OfflineBlock.of((Block)signBlock), front);
    }

    public static OfflineSignSide of(RailLookup.TrackedSign sign) {
        if (sign instanceof RailLookup.TrackedRealSign) {
            return OfflineSignSide.of(sign.signBlock, ((RailLookup.TrackedRealSign)sign).isFrontText());
        }
        throw new IllegalArgumentException("Input TrackedSign is not of a real sign");
    }

    public OfflineWorld getWorld() {
        return this.block.getWorld();
    }

    public OfflineBlock getBlock() {
        return this.block;
    }

    public UUID getWorldUUID() {
        return this.block.getWorldUUID();
    }

    public World getLoadedWorld() {
        return this.block.getLoadedWorld();
    }

    public IntVector3 getPosition() {
        return this.block.getPosition();
    }

    public Block getLoadedBlock() {
        return this.block.getLoadedBlock();
    }

    public boolean isFrontText() {
        return this.front;
    }

    public int hashCode() {
        return this.block.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OfflineSignSide) {
            OfflineSignSide side = (OfflineSignSide)o;
            return this.block.equals((Object)side.block) && this.front == side.front;
        }
        return false;
    }

    public String toString() {
        return "OfflineSignSide{block=" + this.block + ", side=" + (this.front ? "front" : "back") + "}";
    }
}

