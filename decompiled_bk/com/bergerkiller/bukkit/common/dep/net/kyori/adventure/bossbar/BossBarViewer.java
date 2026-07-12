/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.UnmodifiableView
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface BossBarViewer {
    public @UnmodifiableView @NotNull Iterable<? extends BossBar> activeBossBars();
}

