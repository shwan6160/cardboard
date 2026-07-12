/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.properties.AdventureProperties;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Services0;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class Services {
    private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.TRUE.equals(AdventureProperties.SERVICE_LOAD_FAILURES_ARE_FATAL.value());

    private Services() {
    }

    @NotNull
    public static <P> Optional<P> service(@NotNull Class<P> type) {
        ServiceLoader<P> loader = Services0.loader(type);
        Iterator<P> it = loader.iterator();
        while (it.hasNext()) {
            P instance;
            try {
                instance = it.next();
            }
            catch (Throwable t) {
                if (!SERVICE_LOAD_FAILURES_ARE_FATAL) continue;
                throw new IllegalStateException("Encountered an exception loading service " + type, t);
            }
            if (it.hasNext()) {
                throw new IllegalStateException("Expected to find one service " + type + ", found multiple");
            }
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    @NotNull
    public static <P> Optional<P> serviceWithFallback(@NotNull Class<P> type) {
        ServiceLoader<P> loader = Services0.loader(type);
        Iterator<P> it = loader.iterator();
        Object firstFallback = null;
        while (it.hasNext()) {
            P instance;
            try {
                instance = it.next();
            }
            catch (Throwable t) {
                if (!SERVICE_LOAD_FAILURES_ARE_FATAL) continue;
                throw new IllegalStateException("Encountered an exception loading service " + type, t);
            }
            if (instance instanceof Fallback) {
                if (firstFallback != null) continue;
                firstFallback = instance;
                continue;
            }
            return Optional.of(instance);
        }
        return Optional.ofNullable(firstFallback);
    }

    public static <P> Set<P> services(Class<? extends P> clazz) {
        ServiceLoader<P> loader = Services0.loader(clazz);
        HashSet<P> providers = new HashSet<P>();
        Iterator<P> it = loader.iterator();
        while (it.hasNext()) {
            P instance;
            try {
                instance = it.next();
            }
            catch (ServiceConfigurationError ex) {
                if (!SERVICE_LOAD_FAILURES_ARE_FATAL) continue;
                throw new IllegalStateException("Encountered an exception loading a provider for " + clazz + ": ", ex);
            }
            providers.add(instance);
        }
        return Collections.unmodifiableSet(providers);
    }

    public static interface Fallback {
    }
}

