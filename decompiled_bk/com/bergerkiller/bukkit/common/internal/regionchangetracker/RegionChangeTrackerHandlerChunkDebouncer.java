/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import com.bergerkiller.bukkit.common.events.RegionChangeSource;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionBlockChangeChunkCoordinate;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerOps;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

class RegionChangeTrackerHandlerChunkDebouncer {
    private final RegionChangeSource source;
    private final World world;
    private final RegionChangeTrackerHandlerOps ops;
    private final AtomicInteger ticksOfNoChanges = new AtomicInteger();
    private int commitEarlyTaskId;
    private int commitLateTaskId;
    private Set<RegionBlockChangeChunkCoordinate> values = new HashSet<RegionBlockChangeChunkCoordinate>();
    private RegionBlockChangeChunkCoordinate lookupCoord = new RegionBlockChangeChunkCoordinate(0, 0);

    public RegionChangeTrackerHandlerChunkDebouncer(RegionChangeSource source, World world, RegionChangeTrackerHandlerOps ops) {
        this.source = source;
        this.world = world;
        this.ops = ops;
    }

    public boolean addBlock(int blockX, int blockZ) {
        return this.addChunk(blockX >> 4, blockZ >> 4);
    }

    public boolean addChunk(int chunkX, int chunkZ) {
        Set<RegionBlockChangeChunkCoordinate> values = this.values;
        if (values.isEmpty()) {
            this.ticksOfNoChanges.set(0);
            this.commitEarlyTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.ops.getPlugin(), new Runnable(){

                @Override
                public void run() {
                    if (RegionChangeTrackerHandlerChunkDebouncer.this.ticksOfNoChanges.incrementAndGet() >= 5) {
                        RegionChangeTrackerHandlerChunkDebouncer.this.commit();
                    }
                }
            }, 1L, 1L);
            this.commitLateTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.ops.getPlugin(), new Runnable(){

                @Override
                public void run() {
                    RegionChangeTrackerHandlerChunkDebouncer.this.commitLateTaskId = -1;
                    RegionChangeTrackerHandlerChunkDebouncer.this.commit();
                }
            }, 100L);
            values.add(new RegionBlockChangeChunkCoordinate(chunkX, chunkZ));
            return true;
        }
        this.lookupCoord.x = chunkX;
        this.lookupCoord.z = chunkZ;
        if (values.add(this.lookupCoord)) {
            this.lookupCoord = new RegionBlockChangeChunkCoordinate(0, 0);
            this.ticksOfNoChanges.set(0);
            return true;
        }
        return false;
    }

    public synchronized void commit() {
        if (this.commitEarlyTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.commitEarlyTaskId);
            this.commitEarlyTaskId = -1;
        }
        if (this.commitLateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.commitLateTaskId);
            this.commitLateTaskId = -1;
        }
        if (!this.values.isEmpty()) {
            Set<RegionBlockChangeChunkCoordinate> tmp = this.values;
            this.values = new HashSet<RegionBlockChangeChunkCoordinate>();
            this.ops.notifyChanges(this.source, this.world, tmp);
        }
    }
}

