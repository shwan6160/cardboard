/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.permission;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.permission.PermissionCheckers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.TriState;
import java.util.Objects;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public interface PermissionChecker
extends Predicate<String> {
    public static final Pointer<PermissionChecker> POINTER = Pointer.pointer(PermissionChecker.class, Key.key("adventure", "permission"));

    @NotNull
    public static PermissionChecker always(@NotNull TriState state) {
        Objects.requireNonNull(state);
        if (state == TriState.TRUE) {
            return PermissionCheckers.TRUE;
        }
        if (state == TriState.FALSE) {
            return PermissionCheckers.FALSE;
        }
        return PermissionCheckers.NOT_SET;
    }

    @NotNull
    public TriState value(@NotNull String var1);

    @Override
    default public boolean test(@NotNull String permission) {
        return this.value(permission) == TriState.TRUE;
    }
}

