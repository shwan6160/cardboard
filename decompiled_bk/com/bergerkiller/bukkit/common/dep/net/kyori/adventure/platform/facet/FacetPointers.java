/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FacetPointers {
    private static final String NAMESPACE = "adventure_platform";
    public static final Pointer<String> SERVER = Pointer.pointer(String.class, Key.key("adventure_platform", "server"));
    public static final Pointer<Key> WORLD = Pointer.pointer(Key.class, Key.key("adventure_platform", "world"));
    public static final Pointer<Type> TYPE = Pointer.pointer(Type.class, Key.key("adventure_platform", "type"));

    private FacetPointers() {
    }

    public static enum Type {
        PLAYER,
        CONSOLE,
        OTHER;

    }
}

