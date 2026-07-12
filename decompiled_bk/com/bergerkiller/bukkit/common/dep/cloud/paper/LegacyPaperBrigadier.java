/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource
 *  com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent
 *  org.bukkit.command.PluginIdentifiableCommand
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper;

import com.bergerkiller.bukkit.common.dep.cloud.CommandTree;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.BrigadierManagerHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierCommand;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.node.LiteralBrigadierNodeFactory;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.permission.BrigadierPermissionChecker;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBrigadierMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitHelper;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.paper.LegacyPaperCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.paper.PaperBrigadierMappings;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.regex.Pattern;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;

class LegacyPaperBrigadier<C>
implements Listener,
BrigadierManagerHolder<C, BukkitBrigadierCommandSource> {
    private final CloudBrigadierManager<C, BukkitBrigadierCommandSource> brigadierManager;
    private final LegacyPaperCommandManager<C> paperCommandManager;

    LegacyPaperBrigadier(@NonNull LegacyPaperCommandManager<C> paperCommandManager) {
        this.paperCommandManager = paperCommandManager;
        this.brigadierManager = new CloudBrigadierManager<Object, BukkitBrigadierCommandSource>(this.paperCommandManager, SenderMapper.create(sender -> this.paperCommandManager.senderMapper().map(sender.getBukkitSender()), new BukkitBackwardsBrigadierSenderMapper(this.paperCommandManager.senderMapper())));
        BukkitBrigadierMapper<C> mapper = new BukkitBrigadierMapper<C>(this.paperCommandManager.owningPlugin().getLogger(), this.brigadierManager);
        mapper.registerBuiltInMappings();
        PaperBrigadierMappings.register(mapper);
    }

    @Override
    public final boolean hasBrigadierManager() {
        return true;
    }

    @Override
    public final @NonNull CloudBrigadierManager<C, BukkitBrigadierCommandSource> brigadierManager() {
        return this.brigadierManager;
    }

    @EventHandler
    public void onCommandRegister(@NonNull CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        String label;
        if (!(event.getCommand() instanceof PluginIdentifiableCommand)) {
            return;
        }
        if (!((PluginIdentifiableCommand)event.getCommand()).getPlugin().equals((Object)this.paperCommandManager.owningPlugin())) {
            return;
        }
        CommandTree commandTree = this.paperCommandManager.commandTree();
        CommandNode node = commandTree.getNamedNode(label = event.getCommandLabel().contains(":") ? event.getCommandLabel().split(Pattern.quote(":"))[1] : event.getCommandLabel());
        if (node == null) {
            return;
        }
        BrigadierPermissionChecker<Object> permissionChecker = (sender, permission) -> {
            if (commandTree.getNamedNode(label) == null) {
                return false;
            }
            return this.paperCommandManager.testPermission(sender, permission).allowed();
        };
        LiteralBrigadierNodeFactory<C, BukkitBrigadierCommandSource> literalFactory = this.brigadierManager.literalBrigadierNodeFactory();
        event.setLiteral((LiteralCommandNode)literalFactory.createNode(event.getLiteral().getLiteral(), node, (Command)new CloudBrigadierCommand<C, BukkitBrigadierCommandSource>(this.paperCommandManager, this.brigadierManager, command -> BukkitHelper.stripNamespace(this.paperCommandManager, command)), (BrigadierPermissionChecker)permissionChecker));
    }
}

