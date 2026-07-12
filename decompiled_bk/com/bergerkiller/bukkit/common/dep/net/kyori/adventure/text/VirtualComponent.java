/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.VirtualComponentRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface VirtualComponent
extends TextComponent {
    @NotNull
    public Class<?> contextType();

    @NotNull
    public VirtualComponentRenderer<?> renderer();
}

