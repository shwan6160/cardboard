/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import com.bergerkiller.bukkit.common.events.RegionChangeSource;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionBlockChangeChunkCoordinate;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public interface RegionChangeTrackerHandlerOps {
    public JavaPlugin getPlugin();

    public Plugin findPluginEnabledOrProvided(String var1);

    public void notifyChanges(RegionChangeSource var1, World var2, Collection<RegionBlockChangeChunkCoordinate> var3);
}

