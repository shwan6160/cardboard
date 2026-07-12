/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.TickTracker
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.utils.ChunkUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  com.bergerkiller.mountiplex.reflection.SafeMethod
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.TickTracker;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ForwardChunkArea {
    private final TickTracker beginTickTracker;
    private World world = null;
    private final LongHashMap<Entry> entries = new LongHashMap();
    private final List<Entry> entriesList = new ArrayList<Entry>();
    private Entry lastEntry = null;
    private boolean state = false;
    private static final ForceLoadedFunc FORCE_LOADED_FUNC = SafeMethod.contains(ForcedChunk.class, (String)"load", (Class[])new Class[]{World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE}) ? (w, cx, cz) -> ForcedChunk.load((World)w, (int)cx, (int)cz, (int)1) : ChunkUtil::forceChunkLoaded;

    public ForwardChunkArea() {
        this.beginTickTracker = new TickTracker();
        this.beginTickTracker.setRunnable(() -> {
            boolean expectedState = this.state;
            if (this.lastEntry != null && this.lastEntry.state != expectedState) {
                this.lastEntry = null;
            }
            Iterator<Entry> iter = this.entriesList.iterator();
            while (iter.hasNext()) {
                Entry e = iter.next();
                if (e.state == expectedState) continue;
                iter.remove();
                this.entries.remove(e.key);
                e.chunk.close();
            }
            this.state = !expectedState;
        });
    }

    public void begin(World world) {
        this.beginTickTracker.update();
        if (this.world != world) {
            this.reset();
            this.world = world;
        }
    }

    public void reset() {
        if (!this.entriesList.isEmpty()) {
            for (Entry e : this.entriesList) {
                e.chunk.close();
            }
            this.entries.clear();
            this.entriesList.clear();
            this.lastEntry = null;
        }
    }

    public void addBlock(Block block) {
        this.add(block.getX() >> 4, block.getZ() >> 4);
    }

    public void add(int cx, int cz) {
        long key = MathUtil.longHashToLong((int)cx, (int)cz);
        Entry e = this.lastEntry;
        if (e == null || e.key != key) {
            this.lastEntry = e = (Entry)this.entries.computeIfAbsent(key, k -> {
                Entry newEntry = new Entry(FORCE_LOADED_FUNC.forceLoaded(this.world, cx, cz), k, false);
                this.entriesList.add(newEntry);
                return newEntry;
            });
        }
        e.state = this.state;
    }

    private static final class Entry {
        public final ForcedChunk chunk;
        public final long key;
        public boolean state;

        public Entry(ForcedChunk chunk, long key, boolean state) {
            this.chunk = chunk;
            this.key = key;
            this.state = state;
        }
    }

    @FunctionalInterface
    private static interface ForceLoadedFunc {
        public ForcedChunk forceLoaded(World var1, int var2, int var3);
    }
}

