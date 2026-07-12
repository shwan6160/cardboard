/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.utils.ChunkUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  com.bergerkiller.bukkit.common.wrappers.LongHashSet
 *  com.bergerkiller.bukkit.common.wrappers.LongHashSet$LongIterator
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.tc.properties.standard.type.ChunkLoadOptions;
import com.bergerkiller.bukkit.tc.utils.ForwardChunkArea;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.World;

public class ChunkArea {
    public static final Runnable DUMMY_RUNNABLE = new Runnable(){

        @Override
        public void run() {
        }
    };
    public static final int CHUNK_RANGE = 2;
    public static final int CHUNK_EDGE = 5;
    public static final int CHUNK_AREA = 25;
    private World current_world = null;
    private int current_radius = 0;
    private final ForwardChunkArea forward_chunk_area = new ForwardChunkArea();
    private final LongHashSet added_chunk_centers = new LongHashSet();
    private LongHashMap<OwnedChunk> chunks = new LongHashMap();
    private final List<OwnedChunk> all_chunks = new ArrayList<OwnedChunk>();
    private final List<OwnedChunk> removed_chunks = new ArrayList<OwnedChunk>();
    private final List<OwnedChunk> added_chunks = new ArrayList<OwnedChunk>();

    public void reset() {
        this.added_chunk_centers.clear();
        this.chunks.clear();
        for (OwnedChunk chunk : this.all_chunks) {
            chunk.forcedChunk.close();
        }
        this.all_chunks.clear();
        this.removed_chunks.clear();
        this.added_chunks.clear();
        this.forward_chunk_area.reset();
    }

    public void refresh(World world, int radius, LongHashSet coordinates) {
        long ownedCoord;
        OwnedChunk ownedChunk;
        int cz;
        int cx;
        int mz;
        int mx;
        long coord;
        LongHashSet.LongIterator iter;
        boolean radiusDecreased;
        this.removed_chunks.clear();
        this.added_chunks.clear();
        if (this.current_world != world) {
            this.current_world = world;
            this.current_radius = 0;
            this.removed_chunks.addAll(this.chunks.getValues());
            for (OwnedChunk chunk : this.removed_chunks) {
                chunk.forcedChunk.close();
            }
            this.added_chunk_centers.clear();
            this.chunks = new LongHashMap();
            this.all_chunks.clear();
            this.forward_chunk_area.reset();
        }
        for (OwnedChunk owned : this.all_chunks) {
            owned.distance_previous = owned.distance;
        }
        boolean radiusIncreased = radius > this.current_radius;
        boolean bl = radiusDecreased = radius < this.current_radius;
        if (radiusDecreased) {
            iter = this.added_chunk_centers.longIterator();
            while (iter.hasNext()) {
                coord = iter.next();
                mx = MathUtil.longHashMsw((long)coord);
                mz = MathUtil.longHashLsw((long)coord);
                for (cx = -this.current_radius; cx <= this.current_radius; ++cx) {
                    for (cz = -this.current_radius; cz <= this.current_radius; ++cz) {
                        if (Math.abs(cz) <= radius && Math.abs(cx) <= radius || (ownedChunk = (OwnedChunk)this.chunks.get(ownedCoord = MathUtil.longHashToLong((int)(mx + cx), (int)(mz + cz)))) == null) continue;
                        ownedChunk.removeChunk(coord, mx, mz);
                    }
                }
            }
        }
        this.current_radius = radius;
        iter = coordinates.longIterator();
        while (iter.hasNext()) {
            coord = iter.next();
            if (!this.added_chunk_centers.add(coord) && !radiusIncreased) continue;
            mx = MathUtil.longHashMsw((long)coord);
            mz = MathUtil.longHashLsw((long)coord);
            for (cx = -radius; cx <= radius; ++cx) {
                for (cz = -radius; cz <= radius; ++cz) {
                    ownedCoord = MathUtil.longHashToLong((int)(mx + cx), (int)(mz + cz));
                    ownedChunk = (OwnedChunk)this.chunks.get(ownedCoord);
                    if (ownedChunk == null) {
                        ownedChunk = new OwnedChunk(world, mx + cx, mz + cz, ownedCoord);
                        ownedChunk.addChunk(coord, mx, mz);
                        this.all_chunks.add(ownedChunk);
                        this.chunks.put(ownedCoord, (Object)ownedChunk);
                        this.added_chunks.add(ownedChunk);
                        continue;
                    }
                    ownedChunk.addChunk(coord, mx, mz);
                }
            }
        }
        if (radiusDecreased) {
            for (OwnedChunk ownedChunk2 : new ArrayList(this.chunks.values())) {
                if (!ownedChunk2.isEmpty()) continue;
                this.removeOwnedChunk(ownedChunk2);
            }
        }
        LongHashSet.LongIterator added_iter = this.added_chunk_centers.longIterator();
        while (added_iter.hasNext()) {
            long coord2 = added_iter.next();
            if (coordinates.contains(coord2)) continue;
            added_iter.remove();
            int mx2 = MathUtil.longHashMsw((long)coord2);
            int mz2 = MathUtil.longHashLsw((long)coord2);
            for (int cx2 = -radius; cx2 <= radius; ++cx2) {
                for (int cz2 = -radius; cz2 <= radius; ++cz2) {
                    long ownedCoord2 = MathUtil.longHashToLong((int)(mx2 + cx2), (int)(mz2 + cz2));
                    OwnedChunk ownedChunk3 = (OwnedChunk)this.chunks.get(ownedCoord2);
                    if (ownedChunk3 == null) continue;
                    ownedChunk3.removeChunk(coord2, mx2, mz2);
                    if (!ownedChunk3.isEmpty()) continue;
                    this.removeOwnedChunk(ownedChunk3);
                }
            }
        }
    }

    private void removeOwnedChunk(OwnedChunk ownedChunk) {
        ownedChunk.forcedChunk.close();
        this.removed_chunks.add(ownedChunk);
        this.chunks.remove(ownedChunk.chunkKey);
        this.all_chunks.remove(ownedChunk);
    }

    public final void getForcedChunks(List<ForcedChunk> forcedChunks) {
        for (OwnedChunk chunk : this.all_chunks) {
            if (chunk.forcedChunk.isNone()) continue;
            forcedChunks.add(chunk.forcedChunk.clone());
        }
    }

    public final LongHashSet getAllCenters() {
        return this.added_chunk_centers;
    }

    public final Collection<OwnedChunk> getAll() {
        return this.all_chunks;
    }

    public final List<OwnedChunk> getRemoved() {
        return this.removed_chunks;
    }

    public final List<OwnedChunk> getAdded() {
        return this.added_chunks;
    }

    public boolean containsChunk(long chunkLongCoord) {
        return this.chunks.contains(chunkLongCoord);
    }

    public boolean containsChunk(int chunkX, int chunkZ) {
        return this.containsChunk(MathUtil.longHashToLong((int)chunkX, (int)chunkZ));
    }

    public ForwardChunkArea getForwardChunkArea() {
        return this.forward_chunk_area;
    }

    public static final class OwnedChunk {
        private final int cx;
        private final int cz;
        private final long chunkKey;
        private final World world;
        private final LongHashSet chunks = new LongHashSet();
        private int distance;
        private int distance_previous;
        private final ForcedChunk forcedChunk = ForcedChunk.none();

        public OwnedChunk(World world, int cx, int cz, long chunkKey) {
            this.world = world;
            this.cx = cx;
            this.cz = cz;
            this.chunkKey = chunkKey;
            this.distance = Integer.MAX_VALUE;
            this.distance_previous = Integer.MAX_VALUE;
        }

        public boolean isLoaded() {
            return this.world.isChunkLoaded(this.cx, this.cz);
        }

        public void keepLoaded(ChunkLoadOptions.Mode mode) {
            if (mode != ChunkLoadOptions.Mode.DISABLED) {
                this.forcedChunk.move(ChunkUtil.forceChunkLoaded((World)this.world, (int)this.cx, (int)this.cz, (int)mode.getPerChunkRadius()));
            } else {
                this.forcedChunk.close();
            }
        }

        public void loadChunk() {
            this.world.getChunkAt(this.cx, this.cz);
        }

        public World getWorld() {
            return this.world;
        }

        public int getX() {
            return this.cx;
        }

        public int getZ() {
            return this.cz;
        }

        public int getDistance() {
            return this.distance;
        }

        public int getPreviousDistance() {
            return this.distance_previous;
        }

        public boolean isAdded() {
            return this.distance < Integer.MAX_VALUE && this.distance_previous == Integer.MAX_VALUE;
        }

        public boolean isRemoved() {
            return this.distance == Integer.MAX_VALUE && this.distance_previous < Integer.MAX_VALUE;
        }

        private void addChunk(long key, int cx, int cz) {
            if (this.chunks.add(key)) {
                this.distance = Math.min(this.distance, this.calcDistance(cx, cz));
            }
        }

        private void removeChunk(long key, int cx, int cz) {
            int oldDistance;
            if (this.chunks.remove(key) && (oldDistance = this.calcDistance(cx, cz)) <= this.distance) {
                this.distance = Integer.MAX_VALUE;
                LongHashSet.LongIterator iter = this.chunks.longIterator();
                while (iter.hasNext()) {
                    long storedChunk = iter.next();
                    int distance = this.calcDistance(MathUtil.longHashMsw((long)storedChunk), MathUtil.longHashLsw((long)storedChunk));
                    if (distance >= this.distance) continue;
                    this.distance = distance;
                }
            }
        }

        public boolean isEmpty() {
            return this.chunks.isEmpty();
        }

        private final int calcDistance(int cx, int cz) {
            return Math.max(Math.abs(cx - this.cx), Math.abs(cz - this.cz));
        }
    }
}

