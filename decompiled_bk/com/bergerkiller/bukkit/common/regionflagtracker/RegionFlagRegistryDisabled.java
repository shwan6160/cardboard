/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryBaseImpl;

class RegionFlagRegistryDisabled
extends RegionFlagRegistryBaseImpl {
    RegionFlagRegistryDisabled() {
    }

    @Override
    protected boolean isStateReady() {
        return true;
    }
}

