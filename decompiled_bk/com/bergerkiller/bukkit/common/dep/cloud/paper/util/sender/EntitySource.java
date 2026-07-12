/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  org.bukkit.entity.Entity
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender;

import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.GenericSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;

public class EntitySource
extends GenericSource {
    EntitySource(CommandSourceStack commandSourceStack) {
        super(commandSourceStack);
    }

    public @NonNull Entity source() {
        return (Entity)super.source();
    }
}

