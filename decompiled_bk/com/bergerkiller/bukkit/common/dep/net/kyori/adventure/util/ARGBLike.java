/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Range
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.RGBLike;
import org.jetbrains.annotations.Range;

public interface ARGBLike
extends RGBLike {
    public @Range(from=0L, to=255L) int alpha();
}

