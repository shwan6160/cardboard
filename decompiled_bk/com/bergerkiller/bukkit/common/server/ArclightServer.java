/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.ForgeSupport;
import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import java.io.File;
import java.util.Collection;
import java.util.Map;

public class ArclightServer
extends SpigotServer {
    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("io.izzel.arclight.server.Launcher");
        }
        catch (Throwable t1) {
            try {
                Class.forName("io.izzel.arclight.common.ArclightMain");
            }
            catch (Throwable t2) {
                return false;
            }
        }
        try {
            Class.forName("io.izzel.arclight.common.mod.util.remapper.resource.RemapSourceHandler");
        }
        catch (Throwable t) {
            return false;
        }
        return true;
    }

    @Override
    public void postInit(CommonServer.PostInitEvent event) {
        Resolver.setClassLoaderRemappingEnabled(true);
        super.postInit(event);
    }

    @Override
    public String getServerName() {
        return "Arclight";
    }

    @Override
    public boolean isForgeServer() {
        return true;
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }
        return !classPath.startsWith("net.minecraft.");
    }

    @Override
    public Collection<String> getLoadableWorldsLegacy() {
        return ForgeSupport.bukkit().getLoadableWorlds();
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return ForgeSupport.bukkit().isLoadableWorld(worldName);
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldRegionFolder(worldName);
    }

    @Override
    public File getWorldFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldFolder(worldName);
    }

    @Override
    public File getWorldLevelFile(String worldName) {
        return ForgeSupport.bukkit().getWorldLevelFile(worldName);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "arclight");
    }
}

