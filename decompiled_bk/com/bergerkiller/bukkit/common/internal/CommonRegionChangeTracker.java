/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.events.MultiBlockChangeEvent;
import com.bergerkiller.bukkit.common.events.RegionChangeSource;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionBlockChangeChunkCoordinate;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTracker;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

class CommonRegionChangeTracker
extends RegionChangeTracker
implements LibraryComponent {
    public CommonRegionChangeTracker(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void notifyChanges(RegionChangeSource source, World world, Collection<RegionBlockChangeChunkCoordinate> chunks) {
        if (CommonUtil.hasHandlers(MultiBlockChangeEvent.getHandlerList())) {
            HashSet<IntVector2> conv = new HashSet<IntVector2>(chunks.size());
            for (RegionBlockChangeChunkCoordinate chunk : chunks) {
                conv.add(new IntVector2(chunk.x, chunk.z));
            }
            CommonUtil.callEvent(new MultiBlockChangeEvent(source, world, conv));
        }
    }
}

