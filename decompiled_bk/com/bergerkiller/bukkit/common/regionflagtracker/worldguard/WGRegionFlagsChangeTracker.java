/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 */
package com.bergerkiller.bukkit.common.regionflagtracker.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public interface WGRegionFlagsChangeTracker {
    public void cleanup(ProtectedRegion var1);

    public boolean update(ProtectedRegion var1);
}

