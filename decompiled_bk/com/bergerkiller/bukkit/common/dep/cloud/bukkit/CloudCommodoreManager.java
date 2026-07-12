/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommand;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitPluginRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBrigadierMapper;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.Commodore;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.CommodoreProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

class CloudCommodoreManager<C>
extends BukkitPluginRegistrationHandler<C> {
    private final BukkitCommandManager<C> commandManager;
    private final CloudBrigadierManager<C, Object> brigadierManager;
    private final Commodore commodore;

    CloudCommodoreManager(@NonNull BukkitCommandManager<C> commandManager) {
        if (!CommodoreProvider.isSupported()) {
            throw new IllegalStateException("CommodoreProvider reports isSupported = false");
        }
        this.commandManager = commandManager;
        this.commodore = CommodoreProvider.getCommodore(commandManager.owningPlugin());
        this.brigadierManager = new CloudBrigadierManager<Object, Object>(commandManager, SenderMapper.create(sender -> {
            CommandSender bukkitSender = CloudCommodoreManager.getBukkitSender(sender);
            return this.commandManager.senderMapper().map(bukkitSender);
        }, new BukkitBackwardsBrigadierSenderMapper(this.commandManager.senderMapper())));
        BukkitBrigadierMapper<C> mapper = new BukkitBrigadierMapper<C>(this.commandManager.owningPlugin().getLogger(), this.brigadierManager);
        mapper.registerBuiltInMappings();
    }

    @Override
    protected void registerExternal(@NonNull String label, @NonNull Command<?> command, @NonNull BukkitCommand<C> bukkitCommand) {
        this.registerWithCommodore(label, command);
    }

    @Override
    protected void unregisterExternal(@NonNull String label) {
        this.unregisterWithCommodore(label);
    }

    protected @NonNull CloudBrigadierManager<C, Object> brigadierManager() {
        return this.brigadierManager;
    }

    private void registerWithCommodore(@NonNull String label, @NonNull Command<C> command) {
        CommandNode literalCommandNode = this.brigadierManager.literalBrigadierNodeFactory().createNode(label, (Command)command, o -> 1, (sender, commandPermission) -> {
            if (this.commandManager.commandTree().getNamedNode(label) == null) {
                return false;
            }
            return this.commandManager.testPermission(sender, commandPermission).allowed();
        });
        CommandNode<?> existingNode = this.getDispatcher().findNode(Collections.singletonList(label));
        if (existingNode != null) {
            this.mergeChildren(existingNode, literalCommandNode);
        } else {
            this.commodore.register((LiteralCommandNode<?>)literalCommandNode);
        }
    }

    private void unregisterWithCommodore(@NonNull String label) {
        CommandDispatcher<?> dispatcher = this.getDispatcher();
        CommandNode<?> node = dispatcher.findNode(Collections.singletonList(label));
        if (node == null) {
            return;
        }
        try {
            Method removeChild;
            Class<?> commodoreImpl = this.commodore.getClass();
            try {
                removeChild = commodoreImpl.getDeclaredMethod("removeChild", RootCommandNode.class, String.class);
            }
            catch (NoSuchMethodException ex) {
                removeChild = commodoreImpl.getSuperclass().getDeclaredMethod("removeChild", RootCommandNode.class, String.class);
            }
            removeChild.setAccessible(true);
            removeChild.invoke(null, dispatcher.getRoot(), node.getName());
            Field registeredNodesField = commodoreImpl.getDeclaredField("registeredNodes");
            registeredNodesField.setAccessible(true);
            List registeredNodes = (List)registeredNodesField.get(this.commodore);
            registeredNodes.remove(node);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Failed to unregister command '%s' with commodore", label), e);
        }
    }

    private void mergeChildren(CommandNode<?> existingNode, CommandNode<?> node) {
        for (CommandNode<?> child : node.getChildren()) {
            CommandNode<?> existingChild = existingNode.getChild(child.getName());
            if (existingChild == null) {
                existingNode.addChild(child);
                continue;
            }
            this.mergeChildren(existingChild, child);
        }
    }

    private CommandDispatcher<?> getDispatcher() {
        try {
            Method getDispatcherMethod = this.commodore.getClass().getDeclaredMethod("getDispatcher", new Class[0]);
            getDispatcherMethod.setAccessible(true);
            return (CommandDispatcher)getDispatcherMethod.invoke((Object)this.commodore, new Object[0]);
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static CommandSender getBukkitSender(@NonNull Object commandSourceStack) {
        Objects.requireNonNull(commandSourceStack, "commandSourceStack");
        try {
            Method getBukkitSenderMethod = commandSourceStack.getClass().getDeclaredMethod("getBukkitSender", new Class[0]);
            getBukkitSenderMethod.setAccessible(true);
            return (CommandSender)getBukkitSenderMethod.invoke(commandSourceStack, new Object[0]);
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}

