/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandRegistrationFlag
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  io.papermc.paper.command.brigadier.Commands
 *  io.papermc.paper.plugin.bootstrap.BootstrapContext
 *  io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
 *  io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType
 *  io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.BrigadierManagerHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierCommand;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.permission.BrigadierPermissionChecker;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.PluginHolder;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBrigadierMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitHelper;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.paper.PaperBrigadierMappings;
import com.bergerkiller.bukkit.common.dep.cloud.paper.PluginMetaHolder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.papermc.paper.command.brigadier.CommandRegistrationFlag;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class ModernPaperBrigadier<C, B>
implements CommandRegistrationHandler<C>,
BrigadierManagerHolder<C, CommandSourceStack> {
    private final CommandManager<C> manager;
    private final Runnable lockRegistration;
    private final PluginMetaHolder metaHolder;
    private final CloudBrigadierManager<C, CommandSourceStack> brigadierManager;
    private final Map<String, Set<String>> aliases = new ConcurrentHashMap<String, Set<String>>();
    private final Set<Command<C>> registeredCommands = new HashSet<Command<C>>();
    private volatile @Nullable Commands commands;
    private static @MonotonicNonNull Method commandnodeRemoveMethod = null;
    private static @MonotonicNonNull Field commandsInvalidField = null;

    ModernPaperBrigadier(Class<B> baseType, CommandManager<C> manager, SenderMapper<B, C> senderMapper, Runnable lockRegistration) {
        this.manager = manager;
        this.lockRegistration = lockRegistration;
        if (manager instanceof PluginMetaHolder) {
            this.metaHolder = (PluginMetaHolder)((Object)manager);
        } else if (manager instanceof PluginHolder) {
            this.metaHolder = PluginMetaHolder.fromPluginHolder((PluginHolder)((Object)manager));
        } else {
            throw new IllegalArgumentException(manager.toString());
        }
        this.brigadierManager = new CloudBrigadierManager<Object, CommandSourceStack>(this.manager, SenderMapper.create(source -> {
            if (baseType.equals(CommandSender.class)) {
                return senderMapper.map(source.getSender());
            }
            return senderMapper.map(source);
        }, sender -> {
            if (baseType.equals(CommandSender.class)) {
                return (CommandSourceStack)new BukkitBackwardsBrigadierSenderMapper(senderMapper).apply(sender);
            }
            return (CommandSourceStack)senderMapper.reverse(sender);
        }));
        BukkitBrigadierMapper<C> mapper = new BukkitBrigadierMapper<C>(Logger.getLogger(this.metaHolder.owningPluginMeta().getName()), this.brigadierManager);
        mapper.registerBuiltInMappings();
        PaperBrigadierMappings.register(mapper);
    }

    void registerPlugin(Plugin plugin) {
        plugin.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.COMMANDS, this::register);
    }

    void registerBootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.COMMANDS, this::register);
    }

    private void register(ReloadableRegistrarEvent<Commands> event) {
        Commands commands;
        this.lockRegistration.run();
        this.commands = commands = (Commands)event.registrar();
        this.aliases.clear();
        for (CommandNode<C> rootNode : this.manager.commandTree().rootNodes()) {
            this.registerCommand(commands, rootNode);
        }
    }

    private void registerCommand(Commands commands, CommandNode<C> rootNode) {
        Set registered = commands.registerWithFlags(this.metaHolder.owningPluginMeta(), this.createRootNode(rootNode, rootNode.component().name()), this.findBukkitDescription(rootNode), new ArrayList<String>(rootNode.component().alternativeAliases()), new HashSet<CommandRegistrationFlag>(Collections.singletonList(CommandRegistrationFlag.FLATTEN_ALIASES)));
        this.aliases.put(rootNode.component().name(), registered);
    }

    private LiteralCommandNode<CommandSourceStack> createRootNode(CommandNode<C> rootNode, String label) {
        BrigadierPermissionChecker<Object> permissionChecker = (sender, permission) -> {
            if (this.manager.commandTree().getNamedNode(rootNode.component().name()) == null) {
                return false;
            }
            return this.manager.testPermission(sender, permission).allowed();
        };
        return this.brigadierManager.literalBrigadierNodeFactory().createNode(label, (CommandNode)rootNode, (com.mojang.brigadier.Command)new CloudBrigadierCommand<C, CommandSourceStack>(this.manager, this.brigadierManager, command -> BukkitHelper.stripNamespace(this.metaHolder.owningPluginMeta().getName(), command)), (BrigadierPermissionChecker)permissionChecker);
    }

    private String findBukkitDescription(CommandNode<C> node) {
        if (node.command() != null) {
            return BukkitHelper.description(node.command());
        }
        for (CommandNode<C> child : node.children()) {
            @Nullable String result = this.findBukkitDescription(child);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    @Override
    public boolean hasBrigadierManager() {
        return true;
    }

    @Override
    public @NonNull CloudBrigadierManager<C, CommandSourceStack> brigadierManager() {
        return this.brigadierManager;
    }

    @Override
    public boolean registerCommand(@NonNull Command<C> command) {
        boolean ret;
        if (!this.registeredCommands.add(command)) {
            return true;
        }
        @Nullable Commands commands = this.commands;
        if (commands == null) {
            return true;
        }
        if (this.aliases.containsKey(command.rootComponent().name())) {
            CommandDispatcher dispatcher = ModernPaperBrigadier.unsafeGet(commands, Commands::getDispatcher);
            Set<String> registered = this.aliases.get(command.rootComponent().name());
            LiteralCommandNode<CommandSourceStack> newRoot = this.createRootNode(this.manager.commandTree().getNamedNode(command.rootComponent().name()), command.rootComponent().name());
            for (String label : registered) {
                com.mojang.brigadier.tree.CommandNode node = dispatcher.getRoot().getChild(label);
                for (com.mojang.brigadier.tree.CommandNode newChild : newRoot.getChildren()) {
                    node.addChild(newChild);
                }
            }
        } else {
            ModernPaperBrigadier.unsafeOperation(commands, cmds -> this.registerCommand((Commands)cmds, this.manager.commandTree().getNamedNode(command.rootComponent().name())));
        }
        this.resendCommands();
        @Nullable Set<String> registered = this.aliases.get(command.rootComponent().name());
        boolean bl = ret = registered != null && !registered.isEmpty();
        if (!ret) {
            this.registeredCommands.remove(command);
        }
        return ret;
    }

    private void unregisterRoot(Commands commands, String label) {
        @Nullable Set<String> removed = this.aliases.remove(label);
        if (removed == null || removed.isEmpty()) {
            return;
        }
        this.registeredCommands.removeIf(command -> command.rootComponent().name().equals(label));
        try {
            if (commandnodeRemoveMethod == null) {
                commandnodeRemoveMethod = com.mojang.brigadier.tree.CommandNode.class.getMethod("removeCommand", String.class);
                commandnodeRemoveMethod.setAccessible(true);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to find removeCommand method", e);
        }
        ModernPaperBrigadier.unsafeOperation(commands, cmds -> {
            CommandDispatcher dispatcher = cmds.getDispatcher();
            RootCommandNode root = dispatcher.getRoot();
            for (String removedLabel : removed) {
                try {
                    commandnodeRemoveMethod.invoke(root, removedLabel);
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to delete node " + removedLabel, e);
                }
            }
        });
    }

    @Override
    public void unregisterRootCommand(@NonNull CommandComponent<C> rootCommand) {
        @Nullable Commands commands = this.commands;
        if (commands == null) {
            return;
        }
        this.unregisterRoot(commands, rootCommand.name());
        this.resendCommands();
    }

    private void resendCommands() {
        for (Player player : this.metaHolder.owningPlugin().getServer().getOnlinePlayers()) {
            player.updateCommands();
        }
    }

    private static void unsafeOperation(Commands commands, Consumer<Commands> task) {
        ModernPaperBrigadier.unsafeGet(commands, cmds -> {
            task.accept((Commands)cmds);
            return null;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static <T> T unsafeGet(Commands commands, Function<Commands, T> task) {
        try {
            if (commandsInvalidField == null) {
                commandsInvalidField = commands.getClass().getDeclaredField("invalid");
                commandsInvalidField.setAccessible(true);
            }
            boolean prev = commandsInvalidField.getBoolean(commands);
            try {
                commandsInvalidField.setBoolean(commands, false);
                Commands t = task.apply(commands);
                return (T)t;
            }
            finally {
                commandsInvalidField.setBoolean(commands, prev);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to perform unsafe command operation", e);
        }
    }
}

