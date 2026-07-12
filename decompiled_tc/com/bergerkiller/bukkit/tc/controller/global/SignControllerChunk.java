/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Chunk
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.controller.global.SignController;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class SignControllerChunk {
    public final long chunkKey;
    private SignController.EntryList entries = SignController.EntryList.NONE;
    private SignController.EntryList entriesWithSignActions = null;
    private LoadLevel neighbouringBlocksLoadLevel = LoadLevel.NOT_LOADED;
    private int lastVerifyTick = -1;

    public static long getKeyOf(Chunk chunk) {
        return MathUtil.longHashToLong((int)chunk.getX(), (int)chunk.getZ());
    }

    public SignControllerChunk(long chunkKey) {
        this.chunkKey = chunkKey;
    }

    public void initialize(List<SignController.Entry> entries) {
        this.entries = SignController.EntryList.of(entries);
        this.entriesWithSignActions = null;
        this.neighbouringBlocksLoadLevel = LoadLevel.NOT_LOADED;
    }

    public boolean hasSigns() {
        return this.entries.count() > 0;
    }

    public SignController.Entry[] getEntries() {
        return this.entries.unsortedValues();
    }

    public void addEntry(SignController.Entry entry) {
        SignController.EntryList entries = this.entries;
        SignController.EntryList entriesWithSignActions = this.entriesWithSignActions;
        if (entriesWithSignActions == null) {
            this.entries = entries.add(entry);
        } else if (!entry.hasSignActionEvents()) {
            this.entries = entries.add(entry);
        } else if (entries == entriesWithSignActions) {
            this.entries = this.entriesWithSignActions = entries.add(entry);
        } else {
            this.entries = entries.add(entry);
            this.entriesWithSignActions = entriesWithSignActions.add(entry);
        }
        LoadLevel neighbouringBlocksLoadLevel = this.neighbouringBlocksLoadLevel;
        if (neighbouringBlocksLoadLevel != LoadLevel.NOT_LOADED && (neighbouringBlocksLoadLevel == LoadLevel.ALL_SIGNS || entry.hasSignActionEvents())) {
            entry.registerInNeighbouringBlocks();
        }
    }

    public void removeEntry(SignController.Entry entry) {
        this.entries = this.entries.filter(e -> e != entry);
        if (entry.hasSignActionEvents()) {
            this.entriesWithSignActions = null;
        }
        entry.unregisterInNeighbouringBlocks();
    }

    public void updateEntryHasSignActions(SignController.Entry entry, boolean hasSignActions) {
        LoadLevel neighbouringBlocksLoadLevel;
        SignController.EntryList entriesWithSignActions = this.entriesWithSignActions;
        if (entriesWithSignActions != null) {
            if (!hasSignActions) {
                this.entriesWithSignActions = entriesWithSignActions.filter(e -> e != entry);
            } else if (!entriesWithSignActions.contains(entry)) {
                this.entriesWithSignActions = entriesWithSignActions.add(entry);
            }
        }
        if ((neighbouringBlocksLoadLevel = this.neighbouringBlocksLoadLevel) != LoadLevel.NOT_LOADED && (neighbouringBlocksLoadLevel == LoadLevel.ALL_SIGNS || hasSignActions)) {
            entry.registerInNeighbouringBlocks();
            WorldRailLookup railLookup = RailLookup.forWorldIfInitialized(entry.world.getWorld());
            if (railLookup != null) {
                railLookup.discoverRailPieceFromSign(entry.sign.getBlock()).forceCacheVerification();
            }
        }
    }

    public void verifyEntries() {
        for (SignController.Entry e : this.getEntries()) {
            if (e.verify()) continue;
            e.removeInvalidEntry();
        }
    }

    public boolean checkMayHaveSigns(int x, int y, int z, boolean mustHaveSignActions, int currentTick) {
        LoadLevel requestedLevel;
        int entryCount;
        SignController.EntryList entries;
        if (currentTick != this.lastVerifyTick) {
            this.lastVerifyTick = currentTick;
            this.verifyEntries();
        }
        if (mustHaveSignActions) {
            entries = this.entriesWithSignActions;
            if (entries == null) {
                this.entriesWithSignActions = entries = this.entries.filter(SignController.Entry::hasSignActionEvents);
            }
        } else {
            entries = this.entries;
        }
        if ((entryCount = entries.count()) == 0) {
            return false;
        }
        if (entryCount <= 20) {
            boolean mayHaveSigns = false;
            for (SignController.Entry entry : entries.unsortedValues()) {
                Block b = entry.getBlock();
                if (Math.abs(b.getX() - x) > 2 || Math.abs(b.getY() - y) > 2 || Math.abs(b.getZ() - z) > 2) continue;
                mayHaveSigns = true;
                break;
            }
            if (!mayHaveSigns) {
                return false;
            }
        }
        LoadLevel currentLevel = this.neighbouringBlocksLoadLevel;
        LoadLevel loadLevel = requestedLevel = mustHaveSignActions ? LoadLevel.WITH_SIGN_ACTIONS_ONLY : LoadLevel.ALL_SIGNS;
        if (requestedLevel.level() > currentLevel.level()) {
            for (SignController.Entry e : entries.unsortedValues()) {
                e.registerInNeighbouringBlocks();
            }
            this.neighbouringBlocksLoadLevel = requestedLevel;
        }
        return true;
    }

    private static enum LoadLevel {
        NOT_LOADED(0),
        WITH_SIGN_ACTIONS_ONLY(1),
        ALL_SIGNS(2);

        private final int level;

        private LoadLevel(int level) {
            this.level = level;
        }

        public int level() {
            return this.level;
        }
    }
}

