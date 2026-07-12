/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender;

import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.EntitySource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PlayerSource
extends EntitySource {
    PlayerSource(CommandSourceStack commandSourceStack) {
        super(commandSourceStack);
    }

    public @NonNull Player source() {
        return (Player)super.source();
    }
}

