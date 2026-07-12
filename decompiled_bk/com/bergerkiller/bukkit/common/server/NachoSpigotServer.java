/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.util.Map;

public class NachoSpigotServer
extends SpigotServer {
    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            MPLType.getClassByName("dev.cobblesword.nachospigot.Nacho");
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
        return true;
    }

    @Override
    public String getServerName() {
        return "NachoSpigot";
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("nachospigot", "true");
    }
}

