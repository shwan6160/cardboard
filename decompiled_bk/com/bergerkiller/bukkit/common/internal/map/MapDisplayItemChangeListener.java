/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

class MapDisplayItemChangeListener
implements Listener {
    private final CommonMapController controller;

    public MapDisplayItemChangeListener(CommonMapController controller) {
        this.controller = controller;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            this.controller.updateItemFrame(event.getRightClicked().getEntityId());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            this.controller.updateItemFrame(event.getEntity().getEntityId());
        }
    }
}

