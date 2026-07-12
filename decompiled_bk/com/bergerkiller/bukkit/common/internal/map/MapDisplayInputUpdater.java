/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class MapDisplayInputUpdater
extends Task {
    private final CommonMapController controller;

    public MapDisplayInputUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        Iterator<Map.Entry<Player, MapPlayerInput>> iter = this.controller.playerInputs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Player, MapPlayerInput> entry = iter.next();
            if (PlayerInstancePhase.of(entry.getKey()).isConnected()) {
                entry.getValue().onTick();
                continue;
            }
            entry.getValue().onDisconnected();
            iter.remove();
        }
    }
}

