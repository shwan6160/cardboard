/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  org.bukkit.World
 *  ru.beykerykt.lightapi.LightAPI
 *  ru.beykerykt.lightapi.LightType
 *  ru.beykerykt.lightapi.chunks.ChunkInfo
 */
package com.bergerkiller.bukkit.tc.attachments.control.light;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.World;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

class LightAPIControllerForkImpl
extends LightAPIController {
    private final World world;
    private final LightType lightType;
    private final Map<IntVector3, LevelList> levels;
    private final Map<IntVector3, LevelList> dirty;

    public static LightAPIController forSkyLight(World world) {
        return new LightAPIControllerForkImpl(world, LightType.SKY);
    }

    public static LightAPIController forBlockLight(World world) {
        return new LightAPIControllerForkImpl(world, LightType.BLOCK);
    }

    private LightAPIControllerForkImpl(World world, LightType lightType) {
        this.world = world;
        this.lightType = lightType;
        this.levels = new HashMap<IntVector3, LevelList>();
        this.dirty = new HashMap<IntVector3, LevelList>();
    }

    @Override
    public void add(IntVector3 position, int level) {
        LevelList list;
        if (level >= 1 && level <= 15 && (list = this.levels.computeIfAbsent(position, p -> new LevelList())).add(level)) {
            this.dirty.put(position, list);
            this.schedule();
        }
    }

    @Override
    public void remove(IntVector3 position, int level) {
        LevelList list = this.levels.get(position);
        if (list != null && list.remove(level)) {
            this.dirty.put(position, list);
            this.schedule();
        }
    }

    @Override
    public void move(IntVector3 old_position, IntVector3 new_position, int level) {
        this.remove(old_position, level);
        this.add(new_position, level);
    }

    @Override
    public void update(IntVector3 position, int old_level, int new_level) {
        LevelList list = this.levels.get(position);
        if (list != null && list.remove(old_level) | list.add(new_level)) {
            this.dirty.put(position, list);
            this.schedule();
        }
    }

    @Override
    public boolean onSync() {
        if (this.dirty.isEmpty()) {
            return false;
        }
        boolean async = true;
        HashSet chunks = new HashSet();
        for (Map.Entry<IntVector3, LevelList> dirty_entry : this.dirty.entrySet()) {
            if (!dirty_entry.getValue().needsRemoving()) continue;
            IntVector3 pos = dirty_entry.getKey();
            LightAPI.deleteLight((World)this.world, (int)pos.x, (int)pos.y, (int)pos.z, (LightType)this.lightType, (boolean)true);
            chunks.addAll(LightAPI.collectChunks((World)this.world, (int)pos.x, (int)pos.y, (int)pos.z, (LightType)this.lightType, (int)15));
        }
        for (Map.Entry<IntVector3, LevelList> dirty_entry : this.dirty.entrySet()) {
            LevelList list = dirty_entry.getValue();
            if (list.isEmpty()) {
                this.levels.remove(dirty_entry.getKey());
                continue;
            }
            IntVector3 pos = dirty_entry.getKey();
            int level = list.sync();
            LightAPI.createLight((World)this.world, (int)pos.x, (int)pos.y, (int)pos.z, (LightType)this.lightType, (int)level, (boolean)true);
            chunks.addAll(LightAPI.collectChunks((World)this.world, (int)pos.x, (int)pos.y, (int)pos.z, (LightType)this.lightType, (int)level));
        }
        this.dirty.clear();
        for (ChunkInfo chunk : chunks) {
            LightAPI.updateChunk((ChunkInfo)chunk, (LightType)this.lightType);
        }
        return true;
    }

    private static final class LevelList {
        private static final int[] NO_LEVELS = new int[0];
        private static final int[][] SINGLE_LEVEL = new int[16][1];
        private int sync = 0;
        private int[] levels = NO_LEVELS;

        private LevelList() {
        }

        public boolean needsRemoving() {
            return this.levels == NO_LEVELS ? this.sync > 0 : this.sync > this.levels[0];
        }

        public boolean isEmpty() {
            return this.levels == NO_LEVELS;
        }

        public int sync() {
            this.sync = this.levels[0];
            return this.sync;
        }

        public boolean add(int level) {
            if (this.levels == NO_LEVELS) {
                this.levels = SINGLE_LEVEL[level];
                return true;
            }
            if (level > this.levels[0]) {
                int[] new_levels = new int[this.levels.length + 1];
                new_levels[0] = level;
                System.arraycopy(this.levels, 0, new_levels, 1, this.levels.length);
                this.levels = new_levels;
                return true;
            }
            int[] new_levels = new int[this.levels.length + 1];
            for (int i = 0; i < this.levels.length; ++i) {
                int other_level = this.levels[i];
                if (level > other_level) {
                    new_levels[i] = level;
                    System.arraycopy(this.levels, i, new_levels, i + 1, this.levels.length - i);
                    this.levels = new_levels;
                    return false;
                }
                new_levels[i] = other_level;
            }
            new_levels[this.levels.length] = level;
            this.levels = new_levels;
            return false;
        }

        public boolean remove(int level) {
            int len = this.levels.length;
            if (len == 1) {
                if (this.levels[0] == level) {
                    this.levels = NO_LEVELS;
                    return true;
                }
                return false;
            }
            if (len == 2) {
                if (this.levels[1] == level) {
                    this.levels = SINGLE_LEVEL[this.levels[0]];
                    return false;
                }
                if (this.levels[0] == level) {
                    this.levels = SINGLE_LEVEL[this.levels[1]];
                    return true;
                }
                return false;
            }
            if (this.levels[0] == level) {
                int[] new_levels = new int[len - 1];
                System.arraycopy(this.levels, 1, new_levels, 0, len - 1);
                this.levels = new_levels;
                return new_levels[0] != level;
            }
            int i = 1;
            while (i < len) {
                if (this.levels[i] == level) {
                    int[] new_levels = new int[len - 1];
                    System.arraycopy(this.levels, 0, new_levels, 0, i);
                    System.arraycopy(this.levels, i + 1, new_levels, i, len - i - 1);
                    this.levels = new_levels;
                    return false;
                }
                ++len;
            }
            return false;
        }

        static {
            for (int level = 0; level <= 15; ++level) {
                LevelList.SINGLE_LEVEL[level][0] = level;
            }
        }
    }
}

