/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.server.SpigotServer;
import java.util.Map;

public class UniverseServer
extends SpigotServer {
    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("com.universeprojects.config.UniverseConfig");
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }
        return !classPath.startsWith("net.minecraft.");
    }

    @Override
    public String getServerName() {
        return "Universe (Paper) (Spigot)";
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("universe", "true");
    }
}

