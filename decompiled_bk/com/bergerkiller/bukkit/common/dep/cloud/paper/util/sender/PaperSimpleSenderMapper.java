/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender;

import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.ConsoleSource;
import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.EntitySource;
import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.GenericSource;
import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.PlayerSource;
import com.bergerkiller.bukkit.common.dep.cloud.paper.util.sender.Source;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PaperSimpleSenderMapper
implements SenderMapper<CommandSourceStack, Source> {
    public static @NonNull PaperSimpleSenderMapper simpleSenderMapper() {
        return new PaperSimpleSenderMapper();
    }

    PaperSimpleSenderMapper() {
    }

    @Override
    public @NonNull Source map(@NonNull CommandSourceStack base) {
        CommandSender commandSender = base.getSender();
        if (commandSender instanceof ConsoleCommandSender) {
            return new ConsoleSource(base);
        }
        if (commandSender instanceof Player) {
            return new PlayerSource(base);
        }
        if (commandSender instanceof Entity) {
            return new EntitySource(base);
        }
        return new GenericSource(base);
    }

    @Override
    public @NonNull CommandSourceStack reverse(@NonNull Source mapped) {
        return mapped.stack();
    }
}

