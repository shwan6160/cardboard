/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common;

import org.bukkit.plugin.Plugin;

@Deprecated
public class Timings
implements AutoCloseable {
    private static final Timings NOOP = new Timings(null);

    public Timings(Plugin plugin) {
    }

    public Timings(Plugin plugin, String name) {
    }

    public final Timings create(String name) {
        return new Timings(null, name);
    }

    public final Timings start() {
        return this;
    }

    public final void stop() {
    }

    public <T extends Runnable> T run(T runnable) {
        try {
            this.start();
            runnable.run();
        }
        finally {
            this.stop();
        }
        return runnable;
    }

    @Override
    public void close() {
        this.stop();
    }

    @Deprecated
    public static Timings start(Class<?> profiledClass, String name) {
        return NOOP;
    }

    public static Timings create(Plugin plugin, String name) {
        try (Timings tmp = new Timings(plugin);){
            Timings timings = tmp.create(name);
            return timings;
        }
    }

    public static Timings noop() {
        return NOOP;
    }
}

