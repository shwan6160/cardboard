/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.WorldCreator
 */
package com.bergerkiller.bukkit.common.world;

import com.bergerkiller.bukkit.common.Common;
import java.io.File;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public interface LoadableWorld {
    public String getDisplayName();

    public Collection<String> getNames();

    public Format getFormat();

    public World getWorld();

    public File getRootFolder();

    public File getDimensionFolder();

    public File getLevelFile();

    public File getRegionFolder();

    public WorldCreator getWorldCreator();

    default public boolean isLoaded() {
        return this.getWorld() != null;
    }

    public static LoadableWorld of(World world) {
        return Common.SERVER.getLoadableWorld(world);
    }

    public static LoadableWorld find(String worldName) {
        return Common.SERVER.findLoadableWorld(worldName);
    }

    public static Collection<LoadableWorld> listAll() {
        return Common.SERVER.getLoadableWorlds();
    }

    public static enum Format {
        SPIGOT,
        SPIGOT_CONVERTED,
        PAPER;

    }
}

