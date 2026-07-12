/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import org.bukkit.entity.Player;

class LookAtSearchResult {
    public final MapDisplay display;
    public final MapLookPosition lookPosition;

    public LookAtSearchResult(MapDisplay display, MapLookPosition lookPosition) {
        this.display = display;
        this.lookPosition = lookPosition;
    }

    public MapClickEvent click(Player player, MapAction action) {
        MapClickEvent event = new MapClickEvent(player, this.lookPosition, this.display, action);
        CommonUtil.callEvent(event);
        if (!event.isCancelled()) {
            if (action == MapAction.LEFT_CLICK) {
                event.getDisplay().onLeftClick(event);
                event.getDisplay().getRootWidget().onLeftClick(event);
            } else {
                event.getDisplay().onRightClick(event);
                event.getDisplay().getRootWidget().onRightClick(event);
            }
        }
        return event;
    }

    public String toString() {
        return "{display=" + this.display + ", lookPosition=" + this.lookPosition + "}";
    }
}

