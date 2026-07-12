/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexSignMetadata;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCuboid;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlot;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class MutexZone {
    public final OfflineBlock signBlock;
    public final boolean signFront;
    public final String statement;
    public final MutexZoneSlot slot;
    public final MutexZoneSlotType type;
    private Boolean leversDown = null;

    protected MutexZone(OfflineBlock signBlock, boolean signFront, MutexZoneSlotType type, String name, String statement) {
        this.signBlock = signBlock;
        this.signFront = signFront;
        this.statement = statement;
        this.slot = MutexZoneCache.findSlot(name, this);
        this.type = type;
    }

    protected abstract void addToWorld(MutexZoneCacheWorld var1);

    public abstract boolean containsBlock(IntVector3 var1);

    public abstract boolean isNearby(IntVector3 var1, int var2);

    public abstract void forAllContainedChunks(ChunkCoordConsumer var1);

    public abstract long showDebugColorSeed();

    public abstract void showDebug(Player var1, Color var2);

    protected abstract void setLeversDown(boolean var1);

    public double getSpacing(MinecartGroup group) {
        return 0.0;
    }

    public abstract double hitTest(double var1, double var3, double var5, double var7, double var9, double var11);

    public Block getSignBlock() {
        return this.signBlock.getLoadedBlock();
    }

    public boolean isSignFrontText() {
        return this.signFront;
    }

    public static IntVector3 getPosition(SignActionEvent info) {
        Location middlePos = info.getCenterLocation();
        if (middlePos != null) {
            return new IntVector3(middlePos);
        }
        return new IntVector3(info.getBlock());
    }

    protected void setLevers(boolean down) {
        Boolean bState = down;
        if (this.leversDown == bState) {
            return;
        }
        this.leversDown = bState;
        this.setLeversDown(down);
    }

    public void onUsed(MinecartGroup group) {
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{sign=" + this.signBlock + ",type=" + this.type.name() + "}";
    }

    public static MutexZone createCuboid(OfflineWorld world, IntVector3 signPosition, boolean isFrontText, MutexSignMetadata metadata) {
        return new MutexZoneCuboid(world.getBlockAt(signPosition), isFrontText, metadata.start, metadata.end, metadata.type, metadata.name, metadata.statement);
    }

    @FunctionalInterface
    public static interface ChunkCoordConsumer {
        public void accept(int var1, int var2);
    }
}

