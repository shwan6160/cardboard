/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.NotNull;

public interface Namespaced {
    @KeyPattern.Namespace
    @NotNull
    public String namespace();
}

