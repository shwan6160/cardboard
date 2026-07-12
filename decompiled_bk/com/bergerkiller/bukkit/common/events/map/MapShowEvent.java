/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.events.map;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import java.util.UUID;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MapShowEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack mapItem;
    private final HumanHand hand;
    private final ItemFrame itemFrame;

    public MapShowEvent(Player player, HumanHand hand, ItemStack mapItem) {
        this.player = player;
        this.mapItem = mapItem;
        this.hand = hand;
        this.itemFrame = null;
    }

    public MapShowEvent(Player player, ItemFrame itemFrame) {
        this.player = player;
        this.mapItem = CommonMapController.getItemFrameItem(itemFrame);
        this.itemFrame = itemFrame;
        this.hand = null;
    }

    public boolean isHeldEvent() {
        return this.hand != null;
    }

    public boolean isItemFrameEvent() {
        return this.itemFrame != null;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getMapItem() {
        return this.mapItem;
    }

    public UUID getMapUUID() {
        return CommonMapUUIDStore.getMapUUID(this.mapItem);
    }

    public HumanHand getHand() {
        return this.hand;
    }

    public ItemFrame getItemFrame() {
        return this.itemFrame;
    }

    public MapDisplay getDisplay() {
        MapDisplayInfo info = CommonPlugin.getInstance().getMapController().getInfo(this.mapItem);
        return info == null ? null : info.getViewing(this.player);
    }

    public boolean hasDisplay() {
        return this.getDisplay() != null;
    }

    public void setDisplay(JavaPlugin plugin, MapDisplay display) {
        display.addOwner(this.player);
        CommonMapController.MAP_DISPLAY_INIT_FUNC.initialize(display, plugin, this.mapItem);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

