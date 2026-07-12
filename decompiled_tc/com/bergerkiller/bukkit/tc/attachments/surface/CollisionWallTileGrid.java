/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet
 *  com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet$Tracker
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurface;
import com.bergerkiller.bukkit.tc.attachments.surface.Shulker;
import com.bergerkiller.bukkit.tc.attachments.surface.ShulkerTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.block.BlockFace;

final class CollisionWallTileGrid {
    private final ShulkerTracker shulkerCache;
    private final WallAxisLogic axisLogic;
    private final LongHashMap<TileColumn> columns = new LongHashMap();
    private final FastTrackedUpdateSet<TileColumn> changedColumns = new FastTrackedUpdateSet();

    public CollisionWallTileGrid(ShulkerTracker shulkerCache, BlockFace face) {
        this.shulkerCache = shulkerCache;
        this.axisLogic = WallAxisLogic.fromFace(face);
    }

    public void update() {
        for (TileColumn column2 : this.columns.values()) {
            column2.cleanupClearedTiles();
        }
        this.changedColumns.forEachAndClear(column -> {
            if (((TileColumn)column).slots.isEmpty()) {
                column.despawnShulkers(this.shulkerCache);
                this.columns.remove(column.key);
            } else {
                column.updateShulkers(this.shulkerCache, this.axisLogic);
            }
        });
    }

    public boolean isEmpty() {
        return this.columns.size() == 0;
    }

    public void addWallTile(CollisionSurface surface, int x, int y, double value) {
        long key = MathUtil.longHashToLong((int)x, (int)y);
        ((TileColumn)this.columns.computeIfAbsent(key, k -> new TileColumn(x, y, key, this.changedColumns))).add(surface, value);
    }

    public void removeWallTile(CollisionSurface surface, int x, int y) {
        long key = MathUtil.longHashToLong((int)x, (int)y);
        TileColumn column = (TileColumn)this.columns.get(key);
        if (column != null) {
            column.remove(surface);
        }
    }

    public void forAllShulkers(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
        this.axisLogic.forAllShulkers(this, minX, minY, minZ, maxX, maxY, maxZ, action);
    }

    public void forAllShulkersWallRelative(int minX, int minY, double minValue, int maxX, int maxY, double maxValue, Consumer<? super Shulker> action) {
        for (int x = minX; x <= maxX; ++x) {
            block1: for (int y = minY; y <= maxY; ++y) {
                long key = MathUtil.longHashToLong((int)x, (int)y);
                TileColumn column = (TileColumn)this.columns.get(key);
                if (column == null) continue;
                for (TileSlot slot : column.slots) {
                    if (!(slot.value >= minValue) || !(slot.value <= maxValue)) continue;
                    Shulker shulker = column.shulker;
                    if (shulker == null) continue block1;
                    action.accept(shulker);
                    continue block1;
                }
            }
        }
    }

    static interface WallAxisLogic {
        public static WallAxisLogic fromFace(BlockFace face) {
            final BlockFace pushAxis = face.getOppositeFace();
            switch (face) {
                case NORTH: {
                    return new WallAxisLogic(){

                        @Override
                        public BlockFace getPushAxis() {
                            return pushAxis;
                        }

                        @Override
                        public void sortNearest(List<TileSlot> slots) {
                            slots.sort(Collections.reverseOrder());
                        }

                        @Override
                        public void applyShulkerTile(Shulker shulker, int x, int y) {
                            shulker.x = (double)x + 0.5;
                            shulker.y = (double)y + 0.5;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void applyShulkerValue(Shulker shulker, double value) {
                            shulker.z = value;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void forAllShulkers(CollisionWallTileGrid grid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
                            grid.forAllShulkersWallRelative(minX, minY, minZ, maxX, maxY, (double)maxZ + 1.0, action);
                        }
                    };
                }
                case SOUTH: {
                    return new WallAxisLogic(){

                        @Override
                        public BlockFace getPushAxis() {
                            return pushAxis;
                        }

                        @Override
                        public void sortNearest(List<TileSlot> slots) {
                            Collections.sort(slots);
                        }

                        @Override
                        public void applyShulkerTile(Shulker shulker, int x, int y) {
                            shulker.x = (double)x + 0.5;
                            shulker.y = (double)y + 0.5;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void applyShulkerValue(Shulker shulker, double value) {
                            shulker.z = value;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void forAllShulkers(CollisionWallTileGrid grid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
                            grid.forAllShulkersWallRelative(minX, minY, minZ, maxX, maxY, (double)maxZ + 1.0, action);
                        }
                    };
                }
                case WEST: {
                    return new WallAxisLogic(){

                        @Override
                        public BlockFace getPushAxis() {
                            return pushAxis;
                        }

                        @Override
                        public void sortNearest(List<TileSlot> slots) {
                            slots.sort(Collections.reverseOrder());
                        }

                        @Override
                        public void applyShulkerTile(Shulker shulker, int x, int y) {
                            shulker.z = (double)x + 0.5;
                            shulker.y = (double)y + 0.5;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void applyShulkerValue(Shulker shulker, double value) {
                            shulker.x = value;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void forAllShulkers(CollisionWallTileGrid grid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
                            grid.forAllShulkersWallRelative(minZ, minY, minX, maxZ, maxY, (double)maxX + 1.0, action);
                        }
                    };
                }
                case EAST: {
                    return new WallAxisLogic(){

                        @Override
                        public BlockFace getPushAxis() {
                            return pushAxis;
                        }

                        @Override
                        public void sortNearest(List<TileSlot> slots) {
                            Collections.sort(slots);
                        }

                        @Override
                        public void applyShulkerTile(Shulker shulker, int x, int y) {
                            shulker.z = (double)x + 0.5;
                            shulker.y = (double)y + 0.5;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void applyShulkerValue(Shulker shulker, double value) {
                            shulker.x = value;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void forAllShulkers(CollisionWallTileGrid grid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
                            grid.forAllShulkersWallRelative(minZ, minY, minX, maxZ, maxY, (double)maxX + 1.0, action);
                        }
                    };
                }
                case DOWN: {
                    return new WallAxisLogic(){

                        @Override
                        public BlockFace getPushAxis() {
                            return pushAxis;
                        }

                        @Override
                        public void sortNearest(List<TileSlot> slots) {
                            slots.sort(Collections.reverseOrder());
                        }

                        @Override
                        public void applyShulkerTile(Shulker shulker, int x, int y) {
                            shulker.x = (double)x + 0.5;
                            shulker.z = (double)y + 0.5;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void applyShulkerValue(Shulker shulker, double value) {
                            shulker.y = value;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void forAllShulkers(CollisionWallTileGrid grid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
                            grid.forAllShulkersWallRelative(minX, minZ, minY, maxX, maxZ, (double)maxY + 1.0, action);
                        }
                    };
                }
                case UP: {
                    return new WallAxisLogic(){

                        @Override
                        public BlockFace getPushAxis() {
                            return pushAxis;
                        }

                        @Override
                        public void sortNearest(List<TileSlot> slots) {
                            Collections.sort(slots);
                        }

                        @Override
                        public void applyShulkerTile(Shulker shulker, int x, int y) {
                            shulker.x = (double)x + 0.5;
                            shulker.z = (double)y + 0.5;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void applyShulkerValue(Shulker shulker, double value) {
                            shulker.y = value;
                            shulker.invalidateBoundingBox();
                        }

                        @Override
                        public void forAllShulkers(CollisionWallTileGrid grid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
                            grid.forAllShulkersWallRelative(minX, minZ, minY, maxX, maxZ, (double)maxY + 1.0, action);
                        }
                    };
                }
            }
            throw new IllegalArgumentException("Invalid face for wall: " + face);
        }

        public BlockFace getPushAxis();

        public void sortNearest(List<TileSlot> var1);

        public void applyShulkerTile(Shulker var1, int var2, int var3);

        public void applyShulkerValue(Shulker var1, double var2);

        public void forAllShulkers(CollisionWallTileGrid var1, int var2, int var3, int var4, int var5, int var6, int var7, Consumer<? super Shulker> var8);
    }

    private static final class TileColumn {
        public final int tile_x;
        public final int tile_y;
        public final long key;
        public final FastTrackedUpdateSet.Tracker<TileColumn> tracker;
        private List<TileSlot> slots = Collections.emptyList();
        private boolean slotsMutable = false;
        private Shulker shulker = null;

        public TileColumn(int x, int y, long key, FastTrackedUpdateSet<TileColumn> changedColumns) {
            this.tile_x = x;
            this.tile_y = y;
            this.key = key;
            this.tracker = changedColumns.track((Object)this);
        }

        public void notifyChanged() {
            this.tracker.set(true);
        }

        public void add(CollisionSurface surface, double value) {
            for (TileSlot slot : this.slots) {
                if (slot.surface != surface) continue;
                slot.token = surface.getUpdateCounter();
                if (slot.value != value) {
                    slot.value = value;
                    this.notifyChanged();
                }
                return;
            }
            if (this.slotsMutable) {
                this.slots.add(new TileSlot(surface, value));
            } else if (this.slots.isEmpty()) {
                this.slots = Collections.singletonList(new TileSlot(surface, value));
            } else {
                this.slots = new ArrayList<TileSlot>(this.slots);
                this.slots.add(new TileSlot(surface, value));
                this.slotsMutable = true;
            }
            this.notifyChanged();
        }

        private void removeIf(Predicate<TileSlot> condition) {
            if (this.slotsMutable) {
                if (this.slots.removeIf(condition)) {
                    this.notifyChanged();
                }
            } else if (this.slots.size() == 1 && condition.test(this.slots.get(0))) {
                this.slots = Collections.emptyList();
                this.notifyChanged();
            }
        }

        public void remove(CollisionSurface surface) {
            this.removeIf(slot -> slot.surface == surface);
        }

        public void cleanupClearedTiles() {
            this.removeIf(slot -> slot.token != slot.surface.getUpdateCounter());
        }

        public void despawnShulkers(ShulkerTracker shulkerCache) {
            Shulker shulker = this.shulker;
            if (shulker != null) {
                shulkerCache.destroy(shulker);
                this.shulker = null;
            }
        }

        public void updateShulkers(ShulkerTracker shulkerCache, WallAxisLogic axisLogic) {
            Shulker shulker = this.shulker;
            if (shulker == null) {
                this.shulker = shulker = shulkerCache.spawn(axisLogic.getPushAxis());
                axisLogic.applyShulkerTile(shulker, this.tile_x, this.tile_y);
            }
            if (this.slots.size() > 1) {
                axisLogic.sortNearest(this.slots);
            }
            axisLogic.applyShulkerValue(shulker, this.slots.get((int)0).value);
            shulker.scheduleMovement();
        }
    }

    public static final class TileSlot
    implements Comparable<TileSlot> {
        public final CollisionSurface surface;
        public int token;
        public double value;

        public TileSlot(CollisionSurface surface, double value) {
            this.surface = surface;
            this.token = surface.getUpdateCounter();
            this.value = value;
        }

        @Override
        public int compareTo(TileSlot other) {
            return Double.compare(this.value, other.value);
        }
    }
}

