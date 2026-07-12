/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.PluginHolder;
import com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescription;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public final class BukkitHelper {
    private BukkitHelper() {
    }

    public static @NonNull String description(@NonNull Command<?> command) {
        Optional<String> bukkitDescription = command.commandMeta().optional(BukkitCommandMeta.BUKKIT_DESCRIPTION);
        if (bukkitDescription.isPresent()) {
            return bukkitDescription.get();
        }
        CommandDescription description = command.commandDescription();
        if (!description.isEmpty()) {
            return description.description().textDescription();
        }
        return command.rootComponent().description().textDescription();
    }

    public static @NonNull String namespacedLabel(@NonNull PluginHolder manager, @NonNull String label) {
        return BukkitHelper.namespacedLabel(manager.owningPlugin().getName(), label);
    }

    public static @NonNull String namespacedLabel(@NonNull String pluginName, @NonNull String label) {
        return (pluginName + ':' + label).toLowerCase(Locale.ROOT);
    }

    public static @NonNull String stripNamespace(@NonNull PluginHolder manager, @NonNull String command) {
        return BukkitHelper.stripNamespace(manager.owningPlugin().getName(), command);
    }

    public static @NonNull String stripNamespace(@NonNull String pluginName, @NonNull String command) {
        CharSequence[] split = command.split(" ");
        if (!split[0].contains(":")) {
            return command;
        }
        String token = split[0];
        String[] splitToken = token.split(":");
        if (BukkitHelper.namespacedLabel(pluginName, splitToken[1]).equals(token)) {
            split[0] = splitToken[1];
            return String.join((CharSequence)" ", split);
        }
        return command;
    }

    public static @NonNull Executor mainThreadExecutor(@NonNull PluginHolder pluginHolder) {
        Plugin plugin = pluginHolder.owningPlugin();
        Server server = plugin.getServer();
        return task -> {
            if (server.isPrimaryThread()) {
                task.run();
                return;
            }
            server.getScheduler().runTask(plugin, task);
        };
    }

    public static void ensurePluginEnabledOrEnabling(@NonNull Plugin plugin) {
        Plugin fromManager = Bukkit.getServer().getPluginManager().getPlugin(plugin.getName());
        if (!plugin.equals((Object)fromManager) || !plugin.isEnabled()) {
            throw new IllegalStateException("The plugin '" + plugin + "' is not (yet?) valid per the PluginManager. Try calling this method from onEnable rather than in the plugin constructor or onLoad.");
        }
    }
}

