/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;
import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

class MapDisplayHeldMapUpdater
extends Task {
    private final CommonMapController controller;
    private final SortedIdentityCache<Player, MapViewEntry> entries = SortedIdentityCache.create(x$0 -> new MapViewEntry((Player)x$0));

    public MapDisplayHeldMapUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        this.entries.sync(PlayerInstancePhase.getAlivePlayers());
        this.entries.forEach(MapViewEntry::update);
    }

    private class MapViewEntry {
        public final Player player;
        public ItemStack lastLeftHand = null;
        public ItemStack lastRightHand = null;

        public MapViewEntry(Player player) {
            this.player = player;
        }

        public void update() {
            ItemStack currLeftHand = PlayerUtil.getItemInHand(this.player, HumanHand.LEFT);
            ItemStack currRightHand = PlayerUtil.getItemInHand(this.player, HumanHand.RIGHT);
            if (CommonMapUUIDStore.isMap(currLeftHand) && !this.mapEquals(currLeftHand, this.lastLeftHand) && !this.mapEquals(currLeftHand, this.lastRightHand)) {
                MapDisplayHeldMapUpdater.this.controller.handleMapShowEvent(new MapShowEvent(this.player, HumanHand.LEFT, currLeftHand));
            }
            if (CommonMapUUIDStore.isMap(currRightHand) && !this.mapEquals(currRightHand, this.lastRightHand) && !this.mapEquals(currRightHand, this.lastLeftHand)) {
                MapDisplayHeldMapUpdater.this.controller.handleMapShowEvent(new MapShowEvent(this.player, HumanHand.RIGHT, currRightHand));
            }
            this.lastLeftHand = currLeftHand;
            this.lastRightHand = currRightHand;
        }

        private final boolean mapEquals(ItemStack item1, ItemStack item2) {
            UUID mapUUID1 = CommonMapUUIDStore.getMapUUID(item1);
            UUID mapUUID2 = CommonMapUUIDStore.getMapUUID(item2);
            return mapUUID1 != null && mapUUID2 != null && mapUUID1.equals(mapUUID2);
        }
    }
}

