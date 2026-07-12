/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  org.bukkit.command.ConsoleCommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender;

import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.GenericSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ConsoleSource
extends GenericSource {
    ConsoleSource(CommandSourceStack commandSourceStack) {
        super(commandSourceStack);
    }

    public @NonNull ConsoleCommandSender source() {
        return (ConsoleCommandSender)super.source();
    }
}

