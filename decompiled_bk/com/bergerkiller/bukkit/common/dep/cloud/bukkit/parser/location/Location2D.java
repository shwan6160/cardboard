/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Location2D
extends Location {
    protected Location2D(@Nullable World world, double x, double z) {
        super(world, x, 0.0, z);
    }

    public static @NonNull Location2D from(@Nullable World world, double x, double z) {
        return new Location2D(world, x, z);
    }
}

