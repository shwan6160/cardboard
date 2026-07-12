/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Logging {
    public static final ModuleLogger LOGGER;
    public static final ModuleLogger LOGGER_DEBUG;
    public static final ModuleLogger LOGGER_CONFIG;
    public static final ModuleLogger LOGGER_CONVERSION;
    public static final ModuleLogger LOGGER_REFLECTION;
    public static final ModuleLogger LOGGER_NETWORK;
    public static final ModuleLogger LOGGER_TIMINGS;
    public static final ModuleLogger LOGGER_PERMISSIONS;
    public static final ModuleLogger LOGGER_REGISTRY;
    public static final ModuleLogger LOGGER_MAPDISPLAY;

    static {
        CommonPlugin plugin = null;
        if (CommonPlugin.hasInstance()) {
            plugin = CommonPlugin.getInstance();
        } else if (Bukkit.getServer() != null && Bukkit.getServer().getPluginManager() != null) {
            plugin = Bukkit.getServer().getPluginManager().getPlugin("BKCommonLib");
        }
        LOGGER = plugin != null ? new ModuleLogger((Plugin)plugin, new String[0]) : new ModuleLogger("BKCommonLib");
        LOGGER_DEBUG = LOGGER.getModule("Debug");
        LOGGER_CONFIG = LOGGER.getModule("Configuration");
        LOGGER_CONVERSION = LOGGER.getModule("Conversion");
        LOGGER_REFLECTION = LOGGER.getModule("Reflection");
        LOGGER_NETWORK = LOGGER.getModule("Network");
        LOGGER_TIMINGS = LOGGER.getModule("Timings");
        LOGGER_PERMISSIONS = LOGGER.getModule("Permissions");
        LOGGER_REGISTRY = LOGGER.getModule("Registry");
        LOGGER_MAPDISPLAY = LOGGER.getModule("Maps");
    }
}

