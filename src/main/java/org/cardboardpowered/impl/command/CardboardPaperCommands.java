package org.cardboardpowered.impl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandRegistrationFlag;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner;
import io.papermc.paper.plugin.lifecycle.event.registrar.PaperRegistrar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CardboardPaperCommands implements Commands, PaperRegistrar<LifecycleEventOwner> {

    private final CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher;
    private LifecycleEventOwner currentOwner;

    public CardboardPaperCommands(CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void setCurrentContext(LifecycleEventOwner owner) {
        this.currentOwner = owner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        System.out.println("DEBUG getDispatcher called");
        return (CommandDispatcher<CommandSourceStack>) (Object) this.dispatcher;
    }

    @Override
    public Set<String> register(LiteralCommandNode<CommandSourceStack> node, String description, Collection<String> aliases) {
        System.out.println("DEBUG register(LiteralCommandNode) called: " + (node != null ? node.getLiteral() : "null"));
        return this.register(null, node, description, aliases);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> register(PluginMeta pluginMeta, LiteralCommandNode<CommandSourceStack> node, String description, Collection<String> aliases) {
        System.out.println("DEBUG register(PluginMeta, LiteralCommandNode) called: " + (node != null ? node.getLiteral() : "null") + " description: " + description + " aliases: " + aliases);
        this.dispatcher.getRoot().addChild((com.mojang.brigadier.tree.CommandNode<net.minecraft.commands.CommandSourceStack>) (Object) node);
        Set<String> registered = new HashSet<>();
        registered.add(node.getLiteral());
        if (aliases != null) {
            for (String alias : aliases) {
                LiteralCommandNode<net.minecraft.commands.CommandSourceStack> aliasNode = com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(alias)
                    .redirect((com.mojang.brigadier.tree.CommandNode<net.minecraft.commands.CommandSourceStack>) (Object) node)
                    .build();
                this.dispatcher.getRoot().addChild(aliasNode);
                registered.add(alias);
            }
        }
        return registered;
    }

    @Override
    public Set<String> registerWithFlags(PluginMeta pluginMeta, LiteralCommandNode<CommandSourceStack> node, String description, Collection<String> aliases, Set<CommandRegistrationFlag> flags) {
        System.out.println("DEBUG registerWithFlags called: " + (node != null ? node.getLiteral() : "null"));
        return this.register(pluginMeta, node, description, aliases);
    }

    @Override
    public Set<String> register(String label, String description, Collection<String> aliases, BasicCommand command) {
        System.out.println("DEBUG register(BasicCommand) called: " + label);
        return this.register(null, label, description, aliases, command);
    }

    @Override
    public Set<String> register(PluginMeta pluginMeta, String label, String description, Collection<String> aliases, BasicCommand command) {
        System.out.println("DEBUG register(PluginMeta, BasicCommand) called: " + label);
        return Collections.emptySet();
    }
}
