/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.PluginCommand
 */
package com.bergerkiller.bukkit.common.dep.me.lucko.commodore;

import com.bergerkiller.bukkit.common.dep.me.lucko.commodore.Commodore;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.PluginCommand;

abstract class AbstractCommodore
implements Commodore {
    protected static final Field CUSTOM_SUGGESTIONS_FIELD;
    protected static final Field COMMAND_EXECUTE_FUNCTION_FIELD;
    protected static final Field CHILDREN_FIELD;
    protected static final Field LITERALS_FIELD;
    protected static final Field ARGUMENTS_FIELD;
    protected static final Field[] CHILDREN_FIELDS;
    protected static final Command<?> DUMMY_COMMAND;
    protected static final SuggestionProvider<?> DUMMY_SUGGESTION_PROVIDER;

    AbstractCommodore() {
    }

    protected static void removeChild(RootCommandNode root, String name) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                Map children = (Map)field.get(root);
                children.remove(name);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void setRequiredHackyFieldsRecursively(CommandNode<?> node, SuggestionProvider<?> suggestionProvider) {
        try {
            COMMAND_EXECUTE_FUNCTION_FIELD.set(node, DUMMY_COMMAND);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (suggestionProvider != null && node instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentNode = (ArgumentCommandNode)node;
            try {
                CUSTOM_SUGGESTIONS_FIELD.set(argumentNode, suggestionProvider);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (CommandNode<?> child : node.getChildren()) {
            AbstractCommodore.setRequiredHackyFieldsRecursively(child, suggestionProvider);
        }
    }

    protected static <S> LiteralCommandNode<S> renameLiteralNode(LiteralCommandNode<S> node, String newLiteral) {
        LiteralCommandNode clone = new LiteralCommandNode(newLiteral, node.getCommand(), node.getRequirement(), node.getRedirect(), node.getRedirectModifier(), node.isFork());
        for (CommandNode child : node.getChildren()) {
            clone.addChild(child);
        }
        return clone;
    }

    protected static Collection<String> getAliases(org.bukkit.command.Command command) {
        Objects.requireNonNull(command, "command");
        Stream<String> aliasesStream = Stream.concat(Stream.of(command.getLabel()), command.getAliases().stream());
        if (command instanceof PluginCommand) {
            String fallbackPrefix = ((PluginCommand)command).getPlugin().getName().toLowerCase().trim();
            aliasesStream = aliasesStream.flatMap(alias -> Stream.of(alias, fallbackPrefix + ":" + alias));
        }
        return aliasesStream.distinct().collect(Collectors.toList());
    }

    static {
        try {
            CUSTOM_SUGGESTIONS_FIELD = ArgumentCommandNode.class.getDeclaredField("customSuggestions");
            CUSTOM_SUGGESTIONS_FIELD.setAccessible(true);
            COMMAND_EXECUTE_FUNCTION_FIELD = CommandNode.class.getDeclaredField("command");
            COMMAND_EXECUTE_FUNCTION_FIELD.setAccessible(true);
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            for (Field field : CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD}) {
                field.setAccessible(true);
            }
            DUMMY_COMMAND = ctx -> {
                throw new UnsupportedOperationException();
            };
            DUMMY_SUGGESTION_PROVIDER = (context, builder) -> {
                throw new UnsupportedOperationException();
            };
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

