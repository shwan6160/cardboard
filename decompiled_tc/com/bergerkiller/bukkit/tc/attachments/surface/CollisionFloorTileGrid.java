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
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionFloorTileShape;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurface;
import com.bergerkiller.bukkit.tc.attachments.surface.Shulker;
import com.bergerkiller.bukkit.tc.attachments.surface.ShulkerTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.block.BlockFace;

class CollisionFloorTileGrid {
    private final ShulkerTracker shulkerCache;
    private final LongHashMap<TileColumn> columns = new LongHashMap();
    private final FastTrackedUpdateSet<TileColumn> changedColumns = new FastTrackedUpdateSet();

    public CollisionFloorTileGrid(ShulkerTracker shulkerCache) {
        this.shulkerCache = shulkerCache;
    }

    public void update() {
        for (TileColumn column2 : this.columns.values()) {
            column2.cleanupClearedTiles();
        }
        this.changedColumns.forEachAndClear(column -> {
            if (column.shapes == TileColumnShapes.EMPTY) {
                column.despawnShulkers(this.shulkerCache);
                this.columns.remove(column.key);
            } else {
                column.updateShulkers(this.shulkerCache);
            }
        });
    }

    public void addFloorTile(CollisionSurface surface, int x, int z, CollisionFloorTileShape shape) {
        long key = MathUtil.longHashToLong((int)x, (int)z);
        ((TileColumn)this.columns.computeIfAbsent(key, k -> new TileColumn(x, z, key, this.changedColumns))).add(surface, shape);
    }

    public void removeFloorTile(CollisionSurface surface, int x, int z) {
        long key = MathUtil.longHashToLong((int)x, (int)z);
        TileColumn column = (TileColumn)this.columns.get(key);
        if (column != null) {
            column.remove(surface);
        }
    }

    public void forAllShulkers(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<? super Shulker> action) {
        double shulkerMinY = minY;
        double shulkerMaxY = (double)maxY + 1.0;
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                long key = MathUtil.longHashToLong((int)x, (int)z);
                TileColumn column = (TileColumn)this.columns.get(key);
                if (column == null) continue;
                for (Shulker shulker : column.shulkers) {
                    if (!(shulker.y >= shulkerMinY) || !(shulker.y <= shulkerMaxY)) continue;
                    action.accept(shulker);
                }
            }
        }
    }

    private static final class TileColumn {
        public final int tile_x;
        public final int tile_z;
        public final long key;
        public final FastTrackedUpdateSet.Tracker<TileColumn> tracker;
        public TileColumnShapes shapes = TileColumnShapes.EMPTY;
        private List<Shulker> shulkers = Collections.emptyList();

        public TileColumn(int x, int z, long key, FastTrackedUpdateSet<TileColumn> changedColumns) {
            this.tile_x = x;
            this.tile_z = z;
            this.key = key;
            this.tracker = changedColumns.track((Object)this);
        }

        public void notifyChanged() {
            this.tracker.set(true);
        }

        public void add(CollisionSurface surface, CollisionFloorTileShape shape) {
            this.shapes = this.shapes.add(this, surface, shape);
        }

        public void remove(CollisionSurface surface) {
            this.shapes = this.shapes.remove(this, surface);
        }

        public void cleanupClearedTiles() {
            this.shapes = this.shapes.cleanupClearedTiles(this);
        }

        public void despawnShulkers(ShulkerTracker shulkerCache) {
            if (!this.shulkers.isEmpty()) {
                for (Shulker shulker : this.shulkers) {
                    shulkerCache.destroy(shulker);
                }
                this.shulkers = Collections.emptyList();
            }
        }

        public void updateShulkers(ShulkerTracker shulkerCache) {
            CollisionFloorTileShape shape = this.shapes.getShape();
            int requestedCount = shape.shulkerCount();
            int currentCount = this.shulkers.size();
            if (this.shulkers.isEmpty()) {
                this.shulkers = new ArrayList<Shulker>(4);
            }
            while (requestedCount > currentCount) {
                Shulker shulker = shulkerCache.spawn(BlockFace.UP);
                shulker.x = Double.NaN;
                shulker.y = -1.7976931348623157E308;
                shulker.z = Double.NaN;
                shulker.invalidateBoundingBox();
                this.shulkers.add(shulker);
                ++currentCount;
            }
            shape.forEachShulker(this.tile_x, this.tile_z, (x, y, z) -> {
                Shulker bestShulker = null;
                for (Shulker shulker : this.shulkers) {
                    if (shulker.picked) continue;
                    if (shulker.x == x && shulker.z == z) {
                        shulker.y = y;
                        shulker.picked = true;
                        return;
                    }
                    if (bestShulker != null && (!(shulker.y <= y) || !(shulker.y > bestShulker.y))) continue;
                    bestShulker = shulker;
                }
                if (bestShulker == null) {
                    throw new IllegalStateException("Not enough shulkers");
                }
                bestShulker.picked = true;
                bestShulker.x = x;
                bestShulker.y = y;
                bestShulker.z = z;
                bestShulker.invalidateBoundingBox();
            });
            Iterator<Shulker> iter = this.shulkers.iterator();
            while (iter.hasNext()) {
                Shulker shulker = iter.next();
                if (shulker.picked) {
                    shulker.picked = false;
                    shulker.scheduleMovement();
                    continue;
                }
                shulkerCache.destroy(shulker);
                iter.remove();
            }
        }
    }

    private static interface TileColumnShapes {
        public static final TileColumnShapes EMPTY = new TileColumnShapes(){

            @Override
            public CollisionFloorTileShape getShape() {
                throw new IllegalStateException("There are no tiles at this column");
            }

            @Override
            public TileColumnShapes cleanupClearedTiles(TileColumn column) {
                return this;
            }

            @Override
            public TileColumnShapes remove(TileColumn column, CollisionSurface surface) {
                return this;
            }

            @Override
            public TileColumnShapes add(TileColumn column, CollisionSurface surface, CollisionFloorTileShape shape) {
                column.notifyChanged();
                return new SingleTileColumn(surface, shape);
            }
        };

        public CollisionFloorTileShape getShape();

        public TileColumnShapes cleanupClearedTiles(TileColumn var1);

        public TileColumnShapes remove(TileColumn var1, CollisionSurface var2);

        public TileColumnShapes add(TileColumn var1, CollisionSurface var2, CollisionFloorTileShape var3);
    }

    private static class ShapeSlot
    implements Comparable<ShapeSlot> {
        public final CollisionSurface surface;
        public CollisionFloorTileShape shape;
        public int token;

        public ShapeSlot(ShapeSlot copy) {
            this.surface = copy.surface;
            this.shape = copy.shape;
            this.token = copy.token;
        }

        public ShapeSlot(CollisionSurface surface, CollisionFloorTileShape shape) {
            this.surface = surface;
            this.shape = shape;
            this.token = surface.getUpdateCounter();
        }

        protected boolean isClearedFromSurface() {
            return this.token != this.surface.getUpdateCounter();
        }

        protected void updateShape(CollisionFloorTileShape shape) {
            this.shape = shape;
            this.token = this.surface.getUpdateCounter();
        }

        @Override
        public int compareTo(ShapeSlot shapeSlot) {
            return this.shape.compareTo(shapeSlot.shape);
        }
    }

    private static final class MultiTileColumn
    implements TileColumnShapes {
        private final List<ShapeSlot> slots = new ArrayList<ShapeSlot>(5);
        private CollisionFloorTileShape combinedShape = null;

        public MultiTileColumn(SingleTileColumn existing) {
            this.slots.add(new ShapeSlot(existing));
        }

        @Override
        public CollisionFloorTileShape getShape() {
            CollisionFloorTileShape combinedShape = this.combinedShape;
            if (combinedShape == null) {
                CollisionFloorTileShape newShape;
                Collections.sort(this.slots);
                Iterator<ShapeSlot> iter = this.slots.iterator();
                combinedShape = iter.next().shape;
                while (iter.hasNext() && (newShape = iter.next().shape).getMaxY() > combinedShape.getMinY()) {
                    combinedShape = combinedShape.mergeWith(newShape);
                }
                this.combinedShape = combinedShape;
            }
            return combinedShape;
        }

        @Override
        public TileColumnShapes cleanupClearedTiles(TileColumn column) {
            return this.remove(column, ShapeSlot::isClearedFromSurface);
        }

        @Override
        public TileColumnShapes remove(TileColumn column, CollisionSurface surface) {
            return this.remove(column, (ShapeSlot s) -> s.surface == surface);
        }

        private TileColumnShapes remove(TileColumn column, Predicate<ShapeSlot> predicate) {
            if (!this.slots.removeIf(predicate)) {
                return this;
            }
            this.combinedShape = null;
            column.notifyChanged();
            int remainingSize = this.slots.size();
            if (remainingSize == 1) {
                return new SingleTileColumn(this.slots.get(0));
            }
            if (remainingSize == 0) {
                return TileColumnShapes.EMPTY;
            }
            return this;
        }

        @Override
        public TileColumnShapes add(TileColumn column, CollisionSurface surface, CollisionFloorTileShape shape) {
            for (ShapeSlot slot : this.slots) {
                if (slot.surface != surface) continue;
                boolean changed = !slot.shape.equals(shape);
                slot.updateShape(shape);
                if (changed) {
                    this.combinedShape = null;
                    column.notifyChanged();
                }
                return this;
            }
            this.combinedShape = null;
            this.slots.add(new ShapeSlot(surface, shape));
            column.notifyChanged();
            return this;
        }
    }

    private static final class SingleTileColumn
    extends ShapeSlot
    implements TileColumnShapes {
        public SingleTileColumn(ShapeSlot slot) {
            super(slot);
        }

        public SingleTileColumn(CollisionSurface surface, CollisionFloorTileShape shape) {
            super(surface, shape);
        }

        @Override
        public CollisionFloorTileShape getShape() {
            return this.shape;
        }

        @Override
        public TileColumnShapes cleanupClearedTiles(TileColumn column) {
            if (this.isClearedFromSurface()) {
                column.notifyChanged();
                return TileColumnShapes.EMPTY;
            }
            return this;
        }

        @Override
        public TileColumnShapes remove(TileColumn column, CollisionSurface surface) {
            if (surface == this.surface) {
                column.notifyChanged();
                return TileColumnShapes.EMPTY;
            }
            return this;
        }

        @Override
        public TileColumnShapes add(TileColumn column, CollisionSurface surface, CollisionFloorTileShape shape) {
            if (surface == this.surface) {
                boolean changed = !this.shape.equals(shape);
                this.updateShape(shape);
                if (changed) {
                    column.notifyChanged();
                }
                return this;
            }
            return new MultiTileColumn(this).add(column, surface, shape);
        }
    }
}

