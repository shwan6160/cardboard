/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FacetBase<V>
implements Facet<V> {
    protected final Class<? extends V> viewerClass;

    protected FacetBase(@Nullable Class<? extends V> viewerClass) {
        this.viewerClass = viewerClass;
    }

    @Override
    public boolean isSupported() {
        return this.viewerClass != null;
    }

    @Override
    public boolean isApplicable(@NotNull V viewer) {
        return this.viewerClass != null && this.viewerClass.isInstance(viewer);
    }
}

