/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.map.binding;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.internal.map.ItemFrameCluster;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ItemFrameHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemFrameInfo {
    private final CommonMapController controller;
    public final ItemFrame itemFrame;
    public final ItemFrameHandle itemFrameHandle;
    public final DataWatcher.Item<?> itemFrame_dw_item;
    public final IntVector3 coordinates;
    private final List<Player> viewersAdded;
    public final SortedIdentityCache<Object, Player> viewers;
    public MapUUID lastMapUUID;
    public MapUUID preReloadMapUUID;
    public boolean removed;
    public boolean sentMapInfoToPlayers;
    public boolean requiresFurtherLoading;
    public MapDisplayInfo displayInfo;
    private Object lastFrameRawItem = null;
    private ItemStack lastFrameItem = null;
    private ItemStack lastFrameItemUpdate = null;
    public boolean lastFrameItemUpdateNeeded = true;
    private EntityTrackerEntryStateHandle entityTrackerEntryState = null;
    private Collection<Object> entityTrackerViewers = null;
    public final FastTrackedUpdateSet.Tracker<ItemFrameInfo> needsItemRefresh;
    public final UpdateEntry updateEntry = new UpdateEntry(this);

    public ItemFrameInfo(CommonMapController controller, ItemFrameHandle itemFrame) {
        this.controller = controller;
        this.itemFrame = (ItemFrame)itemFrame.getBukkitEntity();
        this.itemFrameHandle = itemFrame;
        this.itemFrame_dw_item = this.itemFrameHandle.getDataWatcher().getItem(ItemFrameHandle.DATA_ITEM);
        this.coordinates = this.itemFrameHandle.getBlockPosition();
        this.viewersAdded = new ArrayList<Player>();
        this.viewers = SortedIdentityCache.createLinked(raw_viewer -> {
            Player player = EntityTrackerEntryHandle.convertRawViewer(raw_viewer);
            this.viewersAdded.add(player);
            return player;
        });
        this.removed = false;
        this.lastMapUUID = null;
        this.preReloadMapUUID = null;
        this.displayInfo = null;
        this.needsItemRefresh = controller.itemFramesThatNeedItemRefresh.track(this);
        this.sentMapInfoToPlayers = false;
        this.requiresFurtherLoading = false;
    }

    public World getWorld() {
        return this.itemFrame.getWorld();
    }

    public MapLookPosition findLookPosition(Vector startPosition, Vector lookDirection, boolean withinBounds) {
        MapLookPosition result = this.findLookPosition(startPosition, lookDirection);
        return result != null && (!withinBounds || result.isWithinBounds()) ? result : null;
    }

    public MapLookPosition findLookPosition(Vector startPosition, Vector lookDirection) {
        double map_y;
        double map_x;
        double distance;
        if (this.lastMapUUID == null) {
            return null;
        }
        boolean invisible = this.itemFrameHandle.getDataWatcher().getFlag(EntityHandle.DATA_FLAGS, 32);
        double FRAME_OFFSET = invisible ? 0.00625 : 0.0625;
        boolean withinBounds = true;
        IntVector3 frameBlock = this.coordinates;
        BlockFace facing = this.itemFrameHandle.getFacing();
        switch (facing) {
            case NORTH: {
                if (lookDirection.getZ() > 1.0E-10) {
                    distance = ((double)frameBlock.z + 1.0 - FRAME_OFFSET - startPosition.getZ()) / lookDirection.getZ();
                    break;
                }
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z, startPosition.getX(), startPosition.getY(), startPosition.getZ());
                break;
            }
            case SOUTH: {
                if (lookDirection.getZ() < -1.0E-10) {
                    distance = ((double)frameBlock.z + FRAME_OFFSET - startPosition.getZ()) / lookDirection.getZ();
                    break;
                }
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z, startPosition.getX(), startPosition.getY(), startPosition.getZ());
                break;
            }
            case WEST: {
                if (lookDirection.getX() > 1.0E-10) {
                    distance = ((double)frameBlock.x + 1.0 - FRAME_OFFSET - startPosition.getX()) / lookDirection.getX();
                    break;
                }
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z, startPosition.getX(), startPosition.getY(), startPosition.getZ());
                break;
            }
            case EAST: {
                if (lookDirection.getX() < -1.0E-10) {
                    distance = ((double)frameBlock.x + FRAME_OFFSET - startPosition.getX()) / lookDirection.getX();
                    break;
                }
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z, startPosition.getX(), startPosition.getY(), startPosition.getZ());
                break;
            }
            case DOWN: {
                if (lookDirection.getY() > 1.0E-10) {
                    distance = ((double)frameBlock.y + 1.0 - FRAME_OFFSET - startPosition.getY()) / lookDirection.getY();
                    break;
                }
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z, startPosition.getX(), startPosition.getY(), startPosition.getZ());
                break;
            }
            case UP: {
                if (lookDirection.getY() < -1.0E-10) {
                    distance = ((double)frameBlock.y + FRAME_OFFSET - startPosition.getY()) / lookDirection.getY();
                    break;
                }
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z, startPosition.getX(), startPosition.getY(), startPosition.getZ());
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid facing: " + facing);
            }
        }
        double at_x = distance * lookDirection.getX() + startPosition.getX() - (double)frameBlock.x - 0.5;
        double at_y = distance * lookDirection.getY() + startPosition.getY() - (double)frameBlock.y - 0.5;
        double at_z = distance * lookDirection.getZ() + startPosition.getZ() - (double)frameBlock.z - 0.5;
        double edgeDistance = Double.MAX_VALUE;
        if (withinBounds) {
            Vector edge = new Vector(Math.max(0.0, Math.abs(at_x) - 0.5), Math.max(0.0, Math.abs(at_y) - 0.5), Math.max(0.0, Math.abs(at_z) - 0.5));
            edgeDistance = edge.length();
        }
        switch (facing) {
            case NORTH: {
                map_x = 0.5 - at_x;
                map_y = 0.5 - at_y;
                break;
            }
            case SOUTH: {
                map_x = 0.5 + at_x;
                map_y = 0.5 - at_y;
                break;
            }
            case WEST: {
                map_x = 0.5 + at_z;
                map_y = 0.5 - at_y;
                break;
            }
            case EAST: {
                map_x = 0.5 - at_z;
                map_y = 0.5 - at_y;
                break;
            }
            case DOWN: {
                map_x = 0.5 + at_x;
                map_y = 0.5 - at_z;
                break;
            }
            case UP: {
                map_x = 0.5 + at_x;
                map_y = 0.5 + at_z;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid facing: " + facing);
            }
        }
        switch (this.itemFrameHandle.getRotationOrdinal() & 3) {
            case 1: {
                double tmp = map_x;
                map_x = map_y;
                map_y = 1.0 - tmp;
                break;
            }
            case 2: {
                map_x = 1.0 - map_x;
                map_y = 1.0 - map_y;
                break;
            }
            case 3: {
                double tmp = map_x;
                map_x = 1.0 - map_y;
                map_y = tmp;
                break;
            }
        }
        return new MapLookPosition(this, 128.0 * (map_x + (double)this.lastMapUUID.getTileX()), 128.0 * (map_y + (double)this.lastMapUUID.getTileY()), distance, edgeDistance);
    }

    public boolean handleRemoved() {
        if (this.removed) {
            this.remove();
            return true;
        }
        return false;
    }

    public void updateItem() {
        if (this.checkItemChanged()) {
            this.recalculateUUID();
        }
    }

    public void updateViewers(BiConsumer<ItemFrameInfo, Player> newPlayerHandler) {
        if (this.lastMapUUID != null) {
            if (this.entityTrackerEntryState == null) {
                EntityTrackerEntryHandle entry = WorldUtil.getTracker(this.itemFrame.getWorld()).getEntry((Entity)this.itemFrame);
                if (entry == null) {
                    this.removed = true;
                    this.needsItemRefresh.set(false);
                    return;
                }
                this.entityTrackerEntryState = entry.getState();
                this.entityTrackerViewers = entry.getRawViewers();
            }
            if (this.viewers.sync(this.entityTrackerViewers)) {
                try {
                    this.viewersAdded.forEach(p -> newPlayerHandler.accept(this, (Player)p));
                }
                finally {
                    this.viewersAdded.clear();
                }
                if (this.displayInfo != null) {
                    this.displayInfo.hasFrameViewerChanges.set(true);
                }
            }
            if (!this.lastMapUUID.isStaticUUID()) {
                this.entityTrackerEntryState.setTickCounter(1);
            }
        }
    }

    private boolean checkItemChanged() {
        UUID mapUUID;
        boolean raw_item_changed = false;
        Object raw_item = DataWatcher.Item.getRawValue(this.itemFrame_dw_item);
        if (this.lastFrameRawItem != (raw_item = CommonNMS.unwrapDWROptional(raw_item)) || this.lastFrameItemUpdateNeeded) {
            this.lastFrameRawItem = raw_item;
            this.lastFrameItem = WrapperConversion.toItemStack(this.lastFrameRawItem);
            raw_item_changed = true;
        }
        this.lastFrameItemUpdateNeeded = false;
        if (!raw_item_changed || LogicUtil.bothNullOrEqual(this.lastFrameItemUpdate, this.lastFrameItem)) {
            return false;
        }
        this.lastFrameItemUpdate = this.lastFrameItem;
        if (this.lastFrameItemUpdate != null) {
            this.lastFrameItemUpdate = this.lastFrameItemUpdate.clone();
        }
        if ((mapUUID = CommonMapUUIDStore.getMapUUID(this.lastFrameItemUpdate)) == null) {
            this.sentMapInfoToPlayers = false;
            this.requiresFurtherLoading = false;
            if (this.lastMapUUID != null) {
                this.remove();
            }
        } else if (this.lastMapUUID == null || !this.lastMapUUID.getUUID().equals(mapUUID)) {
            return true;
        }
        return false;
    }

    public void onChunkDependencyLoaded() {
        if (this.requiresFurtherLoading) {
            this.recalculateUUID();
        }
    }

    void recalculateUUID() {
        IntVector3 itemFramePosition = this.coordinates;
        ItemFrameCluster cluster = this.controller.findCluster(this.itemFrameHandle, itemFramePosition);
        World world = this.getWorld();
        boolean fullyLoaded = true;
        for (ItemFrameCluster.ChunkDependency dependency : cluster.chunk_dependencies) {
            fullyLoaded &= this.controller.checkClusterChunkDependency(world, dependency);
        }
        if (!fullyLoaded) {
            this.requiresFurtherLoading = true;
            return;
        }
        this.recalculateUUIDInCluster(cluster);
        if (cluster.hasMultipleTiles()) {
            for (ItemFrameInfo itemFrame : this.controller.findClusterItemFrames(cluster)) {
                if (itemFrame == this || (!itemFrame.lastFrameItemUpdateNeeded || !itemFrame.checkItemChanged()) && !itemFrame.requiresFurtherLoading) continue;
                itemFrame.recalculateUUIDInCluster(cluster);
            }
        }
    }

    private void recalculateUUIDInCluster(ItemFrameCluster cluster) {
        MapUUID newMapUUID;
        this.requiresFurtherLoading = false;
        IntVector3 itemFramePosition = this.coordinates;
        UUID mapUUID = this.itemFrameHandle.getItemMapDisplayDynamicOnlyUUID();
        if (mapUUID == null) {
            return;
        }
        if (cluster.hasMultipleTiles()) {
            int tileX = 0;
            int tileY = 0;
            if (cluster.facing.getModY() > 0) {
                switch (cluster.rotation) {
                    case 90: {
                        tileX = itemFramePosition.z - cluster.min_coord.z;
                        tileY = cluster.max_coord.x - itemFramePosition.x;
                        break;
                    }
                    case 180: {
                        tileX = cluster.max_coord.x - itemFramePosition.x;
                        tileY = cluster.max_coord.z - itemFramePosition.z;
                        break;
                    }
                    case 270: {
                        tileX = cluster.max_coord.z - itemFramePosition.z;
                        tileY = itemFramePosition.x - cluster.min_coord.x;
                        break;
                    }
                    default: {
                        tileX = itemFramePosition.x - cluster.min_coord.x;
                        tileY = itemFramePosition.z - cluster.min_coord.z;
                        break;
                    }
                }
            } else if (cluster.facing.getModY() < 0) {
                switch (cluster.rotation) {
                    case 90: {
                        tileX = cluster.max_coord.z - itemFramePosition.z;
                        tileY = cluster.max_coord.x - itemFramePosition.x;
                        break;
                    }
                    case 180: {
                        tileX = cluster.max_coord.x - itemFramePosition.x;
                        tileY = itemFramePosition.z - cluster.min_coord.z;
                        break;
                    }
                    case 270: {
                        tileX = itemFramePosition.z - cluster.min_coord.z;
                        tileY = itemFramePosition.x - cluster.min_coord.x;
                        break;
                    }
                    default: {
                        tileX = itemFramePosition.x - cluster.min_coord.x;
                        tileY = cluster.max_coord.z - itemFramePosition.z;
                        break;
                    }
                }
            } else {
                switch (cluster.facing) {
                    case NORTH: {
                        tileX = cluster.max_coord.x - itemFramePosition.x;
                        break;
                    }
                    case EAST: {
                        tileX = cluster.max_coord.z - itemFramePosition.z;
                        break;
                    }
                    case SOUTH: {
                        tileX = itemFramePosition.x - cluster.min_coord.x;
                        break;
                    }
                    case WEST: {
                        tileX = itemFramePosition.z - cluster.min_coord.z;
                        break;
                    }
                    default: {
                        tileX = 0;
                    }
                }
                tileY = cluster.max_coord.y - itemFramePosition.y;
            }
            newMapUUID = new MapUUID(mapUUID, tileX, tileY);
        } else {
            newMapUUID = new MapUUID(mapUUID, 0, 0);
        }
        if (this.lastMapUUID == null || !this.lastMapUUID.getUUID().equals(mapUUID)) {
            this.remove();
            this.lastMapUUID = newMapUUID;
            this.needsItemRefresh.set(this.sentMapInfoToPlayers && !newMapUUID.equals(this.preReloadMapUUID));
            this.preReloadMapUUID = null;
            this.add();
        } else if (!newMapUUID.equals(this.lastMapUUID)) {
            if (this.displayInfo != null) {
                this.displayInfo.addTileIfMissing(newMapUUID.getTileX(), newMapUUID.getTileY());
                int oldTileX = this.lastMapUUID.getTileX();
                int oldTileY = this.lastMapUUID.getTileY();
                this.lastMapUUID = newMapUUID;
                this.needsItemRefresh.set(this.sentMapInfoToPlayers);
                this.preReloadMapUUID = null;
                this.displayInfo.removeTileIfMissing(oldTileX, oldTileY);
            } else {
                this.lastMapUUID = newMapUUID;
                this.needsItemRefresh.set(this.sentMapInfoToPlayers);
                this.preReloadMapUUID = null;
            }
        }
    }

    public void signalEntityRemoved() {
        this.removed = true;
        this.needsItemRefresh.set(false);
        this.entityTrackerEntryState = null;
    }

    private void remove() {
        if (this.displayInfo != null) {
            this.displayInfo.itemFrames.remove(this);
            this.displayInfo.hasFrameViewerChanges.set(true);
            this.displayInfo.refreshResolution();
            this.displayInfo.removeTileIfMissing(this.lastMapUUID.getTileX(), this.lastMapUUID.getTileY());
            if (!this.displayInfo.itemFrames.isEmpty()) {
                this.displayInfo.hasFrameResolutionChanges.set(true);
            }
            this.displayInfo = null;
        }
        this.viewers.clear();
        this.viewersAdded.clear();
        this.requiresFurtherLoading = false;
        this.lastMapUUID = null;
    }

    private void add() {
        if (this.displayInfo == null && this.lastMapUUID != null) {
            this.displayInfo = this.controller.getInfo(this.lastMapUUID.getUUID());
            this.displayInfo.itemFrames.add(this);
            this.displayInfo.hasFrameResolutionChanges.set(true);
            this.displayInfo.refreshResolution();
            this.displayInfo.addTileIfMissing(this.lastMapUUID.getTileX(), this.lastMapUUID.getTileY());
        }
    }

    public static final class UpdateEntry {
        public final ItemFrameInfo info;
        public boolean added;
        public boolean prioritized;
        public UpdateEntry prev;
        public UpdateEntry next;

        private UpdateEntry(ItemFrameInfo info) {
            this.info = info;
            this.added = false;
            this.prioritized = false;
            this.prev = null;
            this.next = null;
        }
    }
}

