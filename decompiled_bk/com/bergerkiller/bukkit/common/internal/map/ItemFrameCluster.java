/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.block.BlockFace;

public final class ItemFrameCluster {
    public final OfflineWorld world;
    public final BlockFace facing;
    public final Set<IntVector3> coordinates;
    public final int rotation;
    public final IntVector3 min_coord;
    public final IntVector3 max_coord;
    public final ChunkDependency[] chunk_dependencies;
    private static final ChunkDependencyBuilder BUILDER = new ChunkDependencyBuilder();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemFrameCluster(OfflineWorld world, BlockFace facing, Set<IntVector3> coordinates, int rotation) {
        this.world = world;
        this.facing = facing;
        this.coordinates = coordinates;
        this.rotation = rotation;
        if (this.hasMultipleTiles()) {
            int max_z;
            int max_y;
            int max_x;
            Iterator<IntVector3> iter = coordinates.iterator();
            IntVector3 coord = iter.next();
            int min_x = max_x = coord.x;
            int min_y = max_y = coord.y;
            int min_z = max_z = coord.z;
            while (iter.hasNext()) {
                coord = iter.next();
                if (coord.x < min_x) {
                    min_x = coord.x;
                }
                if (coord.y < min_y) {
                    min_y = coord.y;
                }
                if (coord.z < min_z) {
                    min_z = coord.z;
                }
                if (coord.x > max_x) {
                    max_x = coord.x;
                }
                if (coord.y > max_y) {
                    max_y = coord.y;
                }
                if (coord.z <= max_z) continue;
                max_z = coord.z;
            }
            this.min_coord = new IntVector3(min_x, min_y, min_z);
            this.max_coord = new IntVector3(max_x, max_y, max_z);
        } else {
            this.min_coord = this.max_coord = coordinates.iterator().next();
        }
        ChunkDependencyBuilder chunkDependencyBuilder = BUILDER;
        synchronized (chunkDependencyBuilder) {
            try {
                this.chunk_dependencies = BUILDER.process(facing, coordinates);
            }
            finally {
                BUILDER.reset();
            }
        }
    }

    public boolean hasMultipleTiles() {
        return this.coordinates.size() > 1;
    }

    private static final class ChunkDependencyBuilder {
        private final LongHashSet covered = new LongHashSet();
        private final List<ChunkDependency> dependencies = new ArrayList<ChunkDependency>();

        private ChunkDependencyBuilder() {
        }

        public ChunkDependency[] process(BlockFace facing, Collection<IntVector3> coordinates) {
            for (IntVector3 coordinate : coordinates) {
                this.covered.add(coordinate.getChunkX(), coordinate.getChunkZ());
            }
            if (!FaceUtil.isAlongX(facing)) {
                for (IntVector3 coordinate : coordinates) {
                    if ((coordinate.x & 0xF) == 0) {
                        this.probe(coordinate.getChunkX(), coordinate.getChunkZ(), -1, 0);
                        continue;
                    }
                    if ((coordinate.x & 0xF) != 15) continue;
                    this.probe(coordinate.getChunkX(), coordinate.getChunkZ(), 1, 0);
                }
            }
            if (!FaceUtil.isAlongZ(facing)) {
                for (IntVector3 coordinate : coordinates) {
                    if ((coordinate.z & 0xF) == 0) {
                        this.probe(coordinate.getChunkX(), coordinate.getChunkZ(), 0, -1);
                        continue;
                    }
                    if ((coordinate.z & 0xF) != 15) continue;
                    this.probe(coordinate.getChunkX(), coordinate.getChunkZ(), 0, 1);
                }
            }
            return this.dependencies.toArray(new ChunkDependency[this.dependencies.size()]);
        }

        public void probe(int cx, int cz, int dx, int dz) {
            int n_cx = cx + dx;
            int n_cz = cz + dz;
            if (this.covered.add(n_cx, n_cz)) {
                this.dependencies.add(new ChunkDependency(cx, cz, n_cx, n_cz));
            }
        }

        public void reset() {
            this.covered.clear();
            this.dependencies.clear();
        }
    }

    public static final class ChunkDependency {
        public static final ChunkDependency NONE = new ChunkDependency();
        public final IntVector2 self;
        public final IntVector2 neighbour;

        private ChunkDependency() {
            this.self = null;
            this.neighbour = null;
        }

        public ChunkDependency(int cx, int cz, int n_cx, int n_cz) {
            this.self = new IntVector2(cx, cz);
            this.neighbour = new IntVector2(n_cx, n_cz);
        }
    }
}

