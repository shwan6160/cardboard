/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.controller.Tickable;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface MapDisplayEvents
extends Tickable {
    public void onAttached();

    public void onDetached();

    @Override
    public void onTick();

    public void onKey(MapKeyEvent var1);

    public void onKeyPressed(MapKeyEvent var1);

    public void onKeyReleased(MapKeyEvent var1);

    public void onLeftClick(MapClickEvent var1);

    public void onRightClick(MapClickEvent var1);

    public void onMapItemChanged();

    public void onStatusChanged(MapStatusEvent var1);

    public boolean onItemDrop(Player var1, ItemStack var2);

    public void onBlockInteract(PlayerInteractEvent var1);
}

