/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.dep.me.lucko.commodore;

import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.BrigadierUnsupportedException;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.Commodore;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.PaperCommodore;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.ReflectionCommodore;
import java.util.Objects;
import java.util.function.Function;
import org.bukkit.plugin.Plugin;

public final class CommodoreProvider {
    private static final Function<Plugin, Commodore> PROVIDER = CommodoreProvider.checkSupported();

    private CommodoreProvider() {
        throw new AssertionError();
    }

    private static Function<Plugin, Commodore> checkSupported() {
        try {
            Class.forName("com.mojang.brigadier.CommandDispatcher");
        }
        catch (Throwable e) {
            CommodoreProvider.printDebugInfo(e);
            return null;
        }
        try {
            PaperCommodore.ensureSetup();
            return PaperCommodore::new;
        }
        catch (Throwable e) {
            CommodoreProvider.printDebugInfo(e);
            try {
                ReflectionCommodore.ensureSetup();
                return ReflectionCommodore::new;
            }
            catch (Throwable e2) {
                CommodoreProvider.printDebugInfo(e2);
                return null;
            }
        }
    }

    private static void printDebugInfo(Throwable e) {
        if (System.getProperty("commodore.debug") != null) {
            System.err.println("Exception while initialising commodore:");
            e.printStackTrace(System.err);
        }
    }

    public static boolean isSupported() {
        return PROVIDER != null;
    }

    public static Commodore getCommodore(Plugin plugin) throws BrigadierUnsupportedException {
        Objects.requireNonNull(plugin, "plugin");
        if (PROVIDER == null) {
            throw new BrigadierUnsupportedException("Brigadier is not supported by the server. Set -Dcommodore.debug=true for debug info.");
        }
        return PROVIDER.apply(plugin);
    }
}

