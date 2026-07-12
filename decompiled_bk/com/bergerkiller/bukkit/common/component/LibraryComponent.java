/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.component;

import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import java.util.function.Predicate;

public interface LibraryComponent {
    public void enable() throws Throwable;

    public void disable() throws Throwable;

    public static <E, L extends LibraryComponent> Conditional<E, L> when(String identifier, Predicate<E> isSupported, CheckedSupplier<L> componentSupplier) {
        return LibraryComponent.when(identifier, isSupported, (E e) -> (LibraryComponent)componentSupplier.get());
    }

    public static <E, L extends LibraryComponent> Conditional<E, L> when(final String identifier, final Predicate<E> isSupported, final CheckedFunction<E, L> componentFunction) {
        return new Conditional<E, L>(){

            @Override
            public String getIdentifier() {
                return identifier;
            }

            @Override
            public boolean isSupported(E environment) {
                return isSupported.test(environment);
            }

            @Override
            public L create(E environment) throws Throwable {
                return (LibraryComponent)componentFunction.apply(environment);
            }
        };
    }

    public static <E, L extends LibraryComponent> Conditional<E, L> forVersions(String identifier, String minimumMinecraftVersion, String maximumMinecraftVersion, CheckedSupplier<L> componentSupplier) {
        return LibraryComponent.forVersions(identifier, minimumMinecraftVersion, maximumMinecraftVersion, (E e) -> (LibraryComponent)componentSupplier.get());
    }

    public static <E, L extends LibraryComponent> Conditional<E, L> forVersions(String identifier, final String minimumMinecraftVersion, final String maximumMinecraftVersion, final CheckedFunction<E, L> componentFunction) {
        boolean hasMaximum;
        boolean hasMinimum = minimumMinecraftVersion != null && !minimumMinecraftVersion.isEmpty();
        boolean bl = hasMaximum = maximumMinecraftVersion != null && !maximumMinecraftVersion.isEmpty();
        final String identifierWithVersion = hasMinimum && hasMaximum ? identifier + "[" + minimumMinecraftVersion + " - " + maximumMinecraftVersion + "]" : (hasMinimum ? identifier + "[" + minimumMinecraftVersion + " AND LATER]" : (hasMaximum ? identifier + "[" + maximumMinecraftVersion + " AND BEFORE]" : identifier + "[ALWAYS]"));
        return new Conditional<E, L>(){

            @Override
            public String getIdentifier() {
                return identifierWithVersion;
            }

            @Override
            public boolean isSupported(E environment) {
                if (minimumMinecraftVersion != null && !minimumMinecraftVersion.isEmpty() && !CommonBootstrap.evaluateMCVersion(">=", minimumMinecraftVersion)) {
                    return false;
                }
                return maximumMinecraftVersion == null || maximumMinecraftVersion.isEmpty() || CommonBootstrap.evaluateMCVersion("<=", maximumMinecraftVersion);
            }

            @Override
            public L create(E environment) throws Throwable {
                return (LibraryComponent)componentFunction.apply(environment);
            }
        };
    }

    public static interface Conditional<E, L extends LibraryComponent> {
        public String getIdentifier();

        public boolean isSupported(E var1);

        public L create(E var1) throws Throwable;
    }
}

