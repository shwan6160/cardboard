/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import org.bukkit.Bukkit;

public class SportBukkitServer
extends CraftBukkitServer {
    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        return Bukkit.getServer().getVersion().contains("SportBukkit");
    }

    @Override
    public String getServerName() {
        return "SportBukkit";
    }
}

