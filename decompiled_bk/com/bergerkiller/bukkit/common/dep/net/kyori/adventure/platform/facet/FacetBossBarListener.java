/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import java.util.Set;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

class FacetBossBarListener<V>
implements Facet.BossBar<V> {
    private final Facet.BossBar<V> facet;
    private final Function<Component, Component> translator;

    FacetBossBarListener(@NotNull Facet.BossBar<V> facet, @NotNull Function<Component, Component> translator) {
        this.facet = facet;
        this.translator = translator;
    }

    @Override
    public void bossBarInitialized(@NotNull BossBar bar) {
        this.facet.bossBarInitialized(bar);
        this.bossBarNameChanged(bar, bar.name(), bar.name());
    }

    @Override
    public void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
        this.facet.bossBarNameChanged(bar, oldName, this.translator.apply(newName));
    }

    @Override
    public void bossBarProgressChanged(@NotNull BossBar bar, float oldPercent, float newPercent) {
        this.facet.bossBarProgressChanged(bar, oldPercent, newPercent);
    }

    @Override
    public void bossBarColorChanged(@NotNull BossBar bar, @NotNull BossBar.Color oldColor, @NotNull BossBar.Color newColor) {
        this.facet.bossBarColorChanged(bar, oldColor, newColor);
    }

    @Override
    public void bossBarOverlayChanged(@NotNull BossBar bar, @NotNull BossBar.Overlay oldOverlay, @NotNull BossBar.Overlay newOverlay) {
        this.facet.bossBarOverlayChanged(bar, oldOverlay, newOverlay);
    }

    @Override
    public void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
        this.facet.bossBarFlagsChanged(bar, flagsAdded, flagsRemoved);
    }

    @Override
    public void addViewer(@NotNull V viewer) {
        this.facet.addViewer(viewer);
    }

    @Override
    public void removeViewer(@NotNull V viewer) {
        this.facet.removeViewer(viewer);
    }

    @Override
    public boolean isEmpty() {
        return this.facet.isEmpty();
    }

    @Override
    public void close() {
        this.facet.close();
    }
}

