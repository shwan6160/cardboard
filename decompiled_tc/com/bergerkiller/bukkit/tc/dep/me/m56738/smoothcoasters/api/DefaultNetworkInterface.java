/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api;

import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.NetworkInterface;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DefaultNetworkInterface
implements NetworkInterface {
    private final Plugin plugin;

    public DefaultNetworkInterface(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void sendMessage(Player player, String channel, byte[] data) {
        player.sendPluginMessage(this.plugin, channel, data);
    }
}

