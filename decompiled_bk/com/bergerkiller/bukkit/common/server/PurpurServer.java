/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.server.SpigotServer;
import java.util.Map;

public class PurpurServer
extends SpigotServer {
    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
            return true;
        }
        catch (Throwable throwable) {
            try {
                Class.forName("net.pl3x.purpur.PurpurConfig");
                return true;
            }
            catch (Throwable throwable2) {
                return false;
            }
        }
    }

    @Override
    public String getServerName() {
        return "Purpur (Paper) (Spigot)";
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("purpur", "true");
    }
}

