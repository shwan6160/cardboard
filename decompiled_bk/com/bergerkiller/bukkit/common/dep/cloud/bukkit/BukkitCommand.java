/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginIdentifiableCommand
 *  org.bukkit.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitHelper;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.util.StringUtils;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class BukkitCommand<C>
extends org.bukkit.command.Command
implements PluginIdentifiableCommand {
    private final CommandComponent<C> command;
    private final BukkitCommandManager<C> manager;
    private final Command<C> cloudCommand;
    private boolean disabled;

    BukkitCommand(@NonNull String label, @NonNull List<@NonNull String> aliases, @NonNull Command<C> cloudCommand, @NonNull CommandComponent<C> command, @NonNull BukkitCommandManager<C> manager) {
        super(label, BukkitHelper.description(cloudCommand), "", aliases);
        this.command = command;
        this.manager = manager;
        this.cloudCommand = cloudCommand;
        this.disabled = false;
    }

    public @NonNull List<@NonNull String> tabComplete(@NonNull CommandSender sender, @NonNull String alias, @NonNull String @NonNull [] args) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder(this.command.name());
        for (String string : args) {
            builder.append(" ").append(string);
        }
        Suggestions result = this.manager.suggestionFactory().suggestImmediately(this.manager.senderMapper().map(sender), builder.toString());
        return result.list().stream().map(Suggestion::suggestion).map(suggestion -> StringUtils.trimBeforeLastSpace(suggestion, result.commandInput())).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public boolean execute(@NonNull CommandSender commandSender, @NonNull String commandLabel, @NonNull String @NonNull [] strings) {
        StringBuilder builder = new StringBuilder(this.command.name());
        for (String string : strings) {
            builder.append(" ").append(string);
        }
        C sender = this.manager.senderMapper().map(commandSender);
        this.manager.commandExecutor().executeCommand(sender, builder.toString());
        return true;
    }

    public @NonNull String getDescription() {
        return BukkitHelper.description(this.cloudCommand);
    }

    public @NonNull Plugin getPlugin() {
        return this.manager.owningPlugin();
    }

    public @NonNull String getUsage() {
        CommandNode<C> node = this.namedNode();
        if (node == null) {
            this.getPlugin().getLogger().log(Level.WARNING, "Node does not exist in tree for command " + this.getLabel() + ".");
            return "";
        }
        return this.manager.commandSyntaxFormatter().apply(null, Collections.singletonList(Objects.requireNonNull(node.component())), node);
    }

    public boolean testPermissionSilent(@NonNull CommandSender target) {
        CommandNode<C> node = this.namedNode();
        if (this.disabled || node == null) {
            return false;
        }
        Map accessMap = node.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
        C cloudSender = this.manager.senderMapper().map(target);
        for (Map.Entry entry : accessMap.entrySet()) {
            if (!GenericTypeReflector.isSuperType((Type)entry.getKey(), cloudSender.getClass()) || !this.manager.testPermission(cloudSender, (Permission)entry.getValue()).allowed()) continue;
            return true;
        }
        return false;
    }

    @API(status=API.Status.INTERNAL, since="1.7.0")
    void disable() {
        this.disabled = true;
    }

    public boolean isRegistered() {
        return !this.disabled;
    }

    private @Nullable CommandNode<C> namedNode() {
        return this.manager.commandTree().getNamedNode(this.command.name());
    }
}

