/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class NoCommandInLeafException
extends IllegalStateException {
    private final CommandComponent<?> commandComponent;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public NoCommandInLeafException(@NonNull CommandComponent<?> commandComponent) {
        super(String.format("Leaf node '%s' does not have associated owning command", commandComponent.name()));
        this.commandComponent = commandComponent;
    }

    public @NonNull CommandComponent<?> commandComponent() {
        return this.commandComponent;
    }
}

