/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandSendEvent
 *  org.bukkit.event.server.ServerLoadEvent
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.dep.me.lucko.commodore;

import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.AbstractCommodore;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.Commodore;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.ReflectionUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

final class ReflectionCommodore
extends AbstractCommodore
implements Commodore {
    private static final Field CONSOLE_FIELD;
    private static final Method GET_COMMAND_DISPATCHER_METHOD;
    private static final Method GET_BRIGADIER_DISPATCHER_METHOD;
    private static final Constructor<?> COMMAND_WRAPPER_CONSTRUCTOR;
    private final Plugin plugin;
    private final List<LiteralCommandNode<?>> registeredNodes = new ArrayList();

    ReflectionCommodore(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new ServerReloadListener(this), this.plugin);
    }

    private CommandDispatcher<?> getDispatcher() {
        try {
            Object mcServerObject = CONSOLE_FIELD.get(Bukkit.getServer());
            Object commandDispatcherObject = GET_COMMAND_DISPATCHER_METHOD.invoke(mcServerObject, new Object[0]);
            return (CommandDispatcher)GET_BRIGADIER_DISPATCHER_METHOD.invoke(commandDispatcherObject, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(LiteralCommandNode<?> node) {
        Objects.requireNonNull(node, "node");
        CommandDispatcher<?> dispatcher = this.getDispatcher();
        RootCommandNode<?> root = dispatcher.getRoot();
        ReflectionCommodore.removeChild(root, node.getName());
        root.addChild(node);
        this.registeredNodes.add(node);
    }

    @Override
    public void register(Command command, LiteralCommandNode<?> node, Predicate<? super Player> permissionTest) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(permissionTest, "permissionTest");
        try {
            SuggestionProvider wrapper = (SuggestionProvider)COMMAND_WRAPPER_CONSTRUCTOR.newInstance(this.plugin.getServer(), command);
            ReflectionCommodore.setRequiredHackyFieldsRecursively(node, wrapper);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        Collection<String> aliases = ReflectionCommodore.getAliases(command);
        if (!aliases.contains(node.getLiteral())) {
            node = ReflectionCommodore.renameLiteralNode(node, command.getName());
        }
        for (String alias : aliases) {
            if (node.getLiteral().equals(alias)) {
                this.register(node);
                continue;
            }
            this.register((LiteralCommandNode<?>)((LiteralArgumentBuilder)LiteralArgumentBuilder.literal(alias).redirect(node)).build());
        }
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new CommandDataSendListener(command, permissionTest), this.plugin);
    }

    static void ensureSetup() {
    }

    static {
        try {
            Class<?> commandDispatcher;
            Class<?> minecraftServer;
            if (ReflectionUtil.minecraftVersion() >= 19) {
                throw new UnsupportedOperationException("ReflectionCommodore is not supported on MC 1.19 or above. Switch to Paper :)");
            }
            if (ReflectionUtil.minecraftVersion() > 16) {
                minecraftServer = ReflectionUtil.mcClass("server.MinecraftServer");
                commandDispatcher = ReflectionUtil.mcClass("commands.CommandDispatcher");
            } else {
                minecraftServer = ReflectionUtil.nmsClass("MinecraftServer");
                commandDispatcher = ReflectionUtil.nmsClass("CommandDispatcher");
            }
            Class<?> craftServer = ReflectionUtil.obcClass("CraftServer");
            CONSOLE_FIELD = craftServer.getDeclaredField("console");
            CONSOLE_FIELD.setAccessible(true);
            GET_COMMAND_DISPATCHER_METHOD = Arrays.stream(minecraftServer.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0).filter(method -> commandDispatcher.isAssignableFrom(method.getReturnType())).findFirst().orElseThrow(NoSuchMethodException::new);
            GET_COMMAND_DISPATCHER_METHOD.setAccessible(true);
            GET_BRIGADIER_DISPATCHER_METHOD = Arrays.stream(commandDispatcher.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0).filter(method -> CommandDispatcher.class.isAssignableFrom(method.getReturnType())).findFirst().orElseThrow(NoSuchMethodException::new);
            GET_BRIGADIER_DISPATCHER_METHOD.setAccessible(true);
            Class<?> commandWrapperClass = ReflectionUtil.obcClass("command.BukkitCommandWrapper");
            COMMAND_WRAPPER_CONSTRUCTOR = commandWrapperClass.getConstructor(craftServer, Command.class);
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final class ServerReloadListener
    implements Listener {
        private final ReflectionCommodore commodore;

        private ServerReloadListener(ReflectionCommodore commodore) {
            this.commodore = commodore;
        }

        @EventHandler
        public void onLoad(ServerLoadEvent e) {
            CommandDispatcher dispatcher = this.commodore.getDispatcher();
            RootCommandNode root = dispatcher.getRoot();
            for (LiteralCommandNode node : this.commodore.registeredNodes) {
                AbstractCommodore.removeChild(root, node.getName());
                root.addChild(node);
            }
        }
    }

    private static final class CommandDataSendListener
    implements Listener {
        private final Set<String> aliases;
        private final Set<String> minecraftPrefixedAliases;
        private final Predicate<? super Player> permissionTest;

        CommandDataSendListener(Command pluginCommand, Predicate<? super Player> permissionTest) {
            this.aliases = new HashSet<String>(AbstractCommodore.getAliases(pluginCommand));
            this.minecraftPrefixedAliases = this.aliases.stream().map(alias -> "minecraft:" + alias).collect(Collectors.toSet());
            this.permissionTest = permissionTest;
        }

        @EventHandler
        public void onCommandSend(PlayerCommandSendEvent e) {
            e.getCommands().removeAll(this.minecraftPrefixedAliases);
            if (!this.permissionTest.test((Player)e.getPlayer())) {
                e.getCommands().removeAll(this.aliases);
            }
        }
    }
}

