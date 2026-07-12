/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.SetMultimap
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ItemFrameHandle;
import com.google.common.collect.SetMultimap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

class MapDisplayItemMapIdUpdater
extends Task {
    private final CommonMapController controller;
    private static final int GENERATION_COUNTER_CLEANUP_INTERVAL = 1000;

    public MapDisplayItemMapIdUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        CommonMapController commonMapController = this.controller;
        synchronized (commonMapController) {
            this.updateMapIds();
        }
    }

    public void updateMapIds() {
        SetMultimap<UUID, MapUUID> dirtyMaps;
        ItemStack item;
        PlayerInventory inv;
        if (this.controller.idGenerationCounter > 1000) {
            this.controller.idGenerationCounter = 0;
            HashSet<MapUUID> validUUIDs = new HashSet<MapUUID>();
            for (Set set : this.controller.itemFrameEntities.values()) {
                for (ItemFrameHandle itemFrame : set) {
                    MapUUID mapUUID = this.controller.getItemFrameMapUUID(itemFrame);
                    if (mapUUID == null) continue;
                    validUUIDs.add(mapUUID);
                }
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); ++i) {
                    item = inv.getItem(i);
                    UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
                    if (mapUUID == null) continue;
                    validUUIDs.add(new MapUUID(mapUUID));
                }
            }
            this.controller.cleanupUnusedUUIDs(validUUIDs);
        }
        if (!(dirtyMaps = this.controller.swapDirtyMapUUIDs()).isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); ++i) {
                    item = inv.getItem(i);
                    UUID uuid = CommonMapUUIDStore.getMapUUID(item);
                    if (!dirtyMaps.containsKey((Object)uuid)) continue;
                    inv.setItem(i, item.clone());
                }
            }
            dirtyMaps.keySet().stream().map(this.controller.maps::get).filter(Objects::nonNull).forEach(info -> {
                Set mapUUIDs = dirtyMaps.get((Object)info.getUniqueId());
                for (ItemFrameInfo itemFrameInfo : info.getItemFrames()) {
                    if (!mapUUIDs.contains(itemFrameInfo.lastMapUUID)) continue;
                    itemFrameInfo.itemFrameHandle.refreshItem();
                }
                for (MapSession session : info.getSessions()) {
                    for (MapDisplayTile tile : session.tiles) {
                        if (!mapUUIDs.contains(tile.getMapTileUUID())) continue;
                        session.onlineOwners.forEach(o -> o.sendDirtyTile(tile));
                    }
                }
            });
            dirtyMaps.clear();
        }
    }
}

