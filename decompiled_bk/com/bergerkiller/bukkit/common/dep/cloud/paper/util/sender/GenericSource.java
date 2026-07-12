/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender;

import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.Source;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

class GenericSource
implements Source {
    private final CommandSourceStack commandSourceStack;

    GenericSource(@NonNull CommandSourceStack commandSourceStack) {
        this.commandSourceStack = commandSourceStack;
    }

    @Override
    public final @NonNull CommandSourceStack stack() {
        return this.commandSourceStack;
    }

    @Override
    public @NonNull CommandSender source() {
        return this.commandSourceStack.getSender();
    }
}

