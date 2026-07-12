/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent
 *  org.bukkit.command.Command
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.dep.me.lucko.commodore;

import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.AbstractCommodore;
import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.Commodore;
import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

final class PaperCommodore
extends AbstractCommodore
implements Commodore,
Listener {
    private final List<CommodoreCommand> commands = new ArrayList<CommodoreCommand>();

    PaperCommodore(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents((Listener)this, plugin);
    }

    @Override
    public void register(LiteralCommandNode<?> node) {
        Objects.requireNonNull(node, "node");
        this.commands.add(new CommodoreCommand(node, null));
    }

    @Override
    public void register(Command command, LiteralCommandNode<?> node, Predicate<? super Player> permissionTest) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(permissionTest, "permissionTest");
        try {
            PaperCommodore.setRequiredHackyFieldsRecursively(node, DUMMY_SUGGESTION_PROVIDER);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        Collection<String> aliases = PaperCommodore.getAliases(command);
        if (!aliases.contains(node.getLiteral())) {
            node = PaperCommodore.renameLiteralNode(node, command.getName());
        }
        for (String alias : aliases) {
            if (node.getLiteral().equals(alias)) {
                this.commands.add(new CommodoreCommand(node, permissionTest));
                continue;
            }
            CommandNode redirectNode = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal(alias).redirect(node)).build();
            this.commands.add(new CommodoreCommand((LiteralCommandNode)redirectNode, permissionTest));
        }
    }

    @EventHandler
    public void onPlayerSendCommandsEvent(AsyncPlayerSendCommandsEvent<?> event) {
        if (event.isAsynchronous() || !event.hasFiredAsync()) {
            for (CommodoreCommand command : this.commands) {
                command.apply(event.getPlayer(), event.getCommandNode());
            }
        }
    }

    static void ensureSetup() {
    }

    static {
        try {
            Class.forName("com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent");
        }
        catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Not running on modern Paper!", e);
        }
    }

    private static final class CommodoreCommand {
        private final LiteralCommandNode<?> node;
        private final Predicate<? super Player> permissionTest;

        private CommodoreCommand(LiteralCommandNode<?> node, Predicate<? super Player> permissionTest) {
            this.node = node;
            this.permissionTest = permissionTest;
        }

        public void apply(Player player, RootCommandNode<?> root) {
            if (this.permissionTest != null && !this.permissionTest.test((Player)player)) {
                return;
            }
            AbstractCommodore.removeChild(root, this.node.getName());
            root.addChild(this.node);
        }
    }
}

