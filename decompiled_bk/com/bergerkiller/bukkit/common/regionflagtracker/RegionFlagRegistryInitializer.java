/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryBaseImpl;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryDisabled;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryWorldGuard;
import org.bukkit.plugin.Plugin;

class RegionFlagRegistryInitializer {
    RegionFlagRegistryInitializer() {
    }

    public static RegionFlagRegistryBaseImpl initialize() {
        Plugin worldguardPlugin = RegionFlagRegistryBaseImpl.findPlugin("WorldGuard", p -> true);
        if (worldguardPlugin != null) {
            boolean available = false;
            try {
                Class.forName("com.sk89q.worldguard.WorldGuard");
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.session.handler.FlagValueChangeHandler");
                available = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (available) {
                return new RegionFlagRegistryWorldGuard();
            }
        }
        return new RegionFlagRegistryDisabled();
    }
}

