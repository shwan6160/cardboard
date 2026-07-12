/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldguard.protection.flags.Flag
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 */
package com.bergerkiller.bukkit.common.regionflagtracker.worldguard;

import com.bergerkiller.bukkit.common.regionflagtracker.worldguard.WGRegionFlagsChangeTracker;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WGRegionFlagsChangeTrackerFallback
implements WGRegionFlagsChangeTracker {
    private Map<Flag<?>, ?> lastFlags;

    public WGRegionFlagsChangeTrackerFallback(ProtectedRegion region) {
        this.lastFlags = new HashMap(region.getFlags());
    }

    @Override
    public void cleanup(ProtectedRegion region) {
        this.lastFlags = Collections.emptyMap();
    }

    @Override
    public boolean update(ProtectedRegion region) {
        if (region.getFlags().equals(this.lastFlags)) {
            return false;
        }
        this.lastFlags = new HashMap(region.getFlags());
        return true;
    }
}

