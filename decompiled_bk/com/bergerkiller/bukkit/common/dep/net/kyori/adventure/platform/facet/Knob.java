/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Knob {
    private static final String NAMESPACE = "net.kyo".concat("ri.adventure");
    public static final boolean DEBUG = Knob.isEnabled("debug", false);
    private static final Set<Object> UNSUPPORTED = new CopyOnWriteArraySet<Object>();
    public static volatile Consumer<String> OUT = System.out::println;
    public static volatile BiConsumer<String, Throwable> ERR = (message, err) -> {
        System.err.println((String)message);
        if (err != null) {
            err.printStackTrace(System.err);
        }
    };

    private Knob() {
    }

    public static boolean isEnabled(@NotNull String key, boolean defaultValue) {
        return System.getProperty(NAMESPACE + "." + key, Boolean.toString(defaultValue)).equalsIgnoreCase("true");
    }

    public static void logError(@Nullable Throwable error, @NotNull String format, Object ... arguments) {
        if (DEBUG) {
            ERR.accept(String.format(format, arguments), error);
        }
    }

    public static void logMessage(@NotNull String format, Object ... arguments) {
        if (DEBUG) {
            OUT.accept(String.format(format, arguments));
        }
    }

    public static void logUnsupported(@NotNull Object facet, @NotNull Object value) {
        if (DEBUG && UNSUPPORTED.add(value)) {
            OUT.accept(String.format("Unsupported value '%s' for facet: %s", value, facet));
        }
    }
}

