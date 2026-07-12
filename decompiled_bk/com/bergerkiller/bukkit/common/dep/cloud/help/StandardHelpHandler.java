/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.help.CommandPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandler;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntry;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.HelpQueryResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.IndexCommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.MultipleCommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.VerboseCommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandInputTokenizer;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class StandardHelpHandler<C>
implements HelpHandler<C> {
    private final CommandManager<C> commandManager;
    private final CommandPredicate<C> commandFilter;

    public StandardHelpHandler(@NonNull CommandManager<C> commandManager, @NonNull CommandPredicate<C> commandPredicate) {
        this.commandManager = commandManager;
        this.commandFilter = commandPredicate;
    }

    @Override
    public @NonNull HelpQueryResult<C> query(@NonNull HelpQuery<C> query) {
        List<CommandEntry<C>> commands = this.commands(query.sender());
        if (query.query().replace(" ", "").isEmpty()) {
            return IndexCommandResult.of(query, commands);
        }
        LinkedList<String> queryFragments = new CommandInputTokenizer(query.query()).tokenize();
        String rootFragment = (String)queryFragments.get(0);
        LinkedList<Command<C>> availableCommands = new LinkedList<Command<C>>();
        HashSet<String> availableCommandLabels = new HashSet<String>();
        boolean exactMatch = false;
        for (CommandEntry<C> entry2 : commands) {
            Command<C> command2 = entry2.command();
            CommandComponent<C> component = command2.rootComponent();
            for (String string : component.aliases()) {
                if (!string.toLowerCase(Locale.ENGLISH).startsWith(rootFragment.toLowerCase(Locale.ENGLISH))) continue;
                availableCommands.add(command2);
                availableCommandLabels.add(component.name());
                break;
            }
            for (String string : component.aliases()) {
                if (!string.equalsIgnoreCase(rootFragment)) continue;
                exactMatch = true;
                break;
            }
            if (!rootFragment.equalsIgnoreCase(component.name())) continue;
            availableCommandLabels.clear();
            availableCommands.clear();
            availableCommandLabels.add(component.name());
            availableCommands.add(command2);
            break;
        }
        if (availableCommands.isEmpty()) {
            return IndexCommandResult.of(query, Collections.emptyList());
        }
        if (!exactMatch || availableCommandLabels.size() > 1) {
            return IndexCommandResult.of(query, availableCommands.stream().map(command -> CommandEntry.of(command, this.commandManager.commandSyntaxFormatter().apply(query.sender(), command.components(), null))).sorted().filter(entry -> this.isAllowed(query.sender(), entry.command())).collect(Collectors.toList()));
        }
        CommandNode<C> node = this.commandManager.commandTree().getNamedNode((String)availableCommandLabels.iterator().next());
        LinkedList traversedNodes = new LinkedList();
        CommandNode<C> head = node;
        int index = 0;
        block3: while (head != null && this.isNodeVisible(head)) {
            traversedNodes.add(head.component());
            if (head.component() != null && head.command() != null && (head.isLeaf() || ++index == queryFragments.size()) && this.isAllowed(query.sender(), head.command())) {
                return VerboseCommandResult.of(query, CommandEntry.of(head.command(), this.commandManager.commandSyntaxFormatter().apply(query.sender(), head.command().components(), null)));
            }
            if (head.children().size() == 1) {
                head = head.children().get(0);
                continue;
            }
            if (index < queryFragments.size()) {
                CommandNode<C> potentialVariable = null;
                for (CommandNode<C> commandNode : head.children()) {
                    if (commandNode.component() == null || commandNode.component().type() != CommandComponent.ComponentType.LITERAL) {
                        if (commandNode.component() == null) continue;
                        potentialVariable = commandNode;
                        continue;
                    }
                    for (String childAlias : commandNode.component().aliases()) {
                        if (!childAlias.equalsIgnoreCase((String)queryFragments.get(index))) continue;
                        head = commandNode;
                        continue block3;
                    }
                }
                if (potentialVariable != null) {
                    head = potentialVariable;
                    continue;
                }
            }
            String currentDescription = this.commandManager.commandSyntaxFormatter().apply(query.sender(), traversedNodes, null);
            LinkedList<String> linkedList = new LinkedList<String>();
            for (CommandNode<C> child : head.children()) {
                if (!this.isNodeVisible(child)) continue;
                LinkedList traversedNodesSub = new LinkedList(traversedNodes);
                if (child.component() != null && child.command() != null && !this.isAllowed(query.sender(), child.command())) continue;
                traversedNodesSub.add(child.component());
                linkedList.add(this.commandManager.commandSyntaxFormatter().apply(query.sender(), traversedNodesSub, child));
            }
            return MultipleCommandResult.of(query, currentDescription, linkedList);
        }
        return IndexCommandResult.of(query, Collections.emptyList());
    }

    protected @NonNull List<@NonNull CommandEntry<C>> commands(@NonNull C sender) {
        return this.commandManager.commands().stream().filter(this.commandFilter).filter(command -> this.isAllowed(sender, (Command<C>)command)).map(command -> CommandEntry.of(command, this.commandManager.commandSyntaxFormatter().apply(sender, command.components(), null))).sorted().collect(Collectors.toList());
    }

    private boolean isAllowed(C sender, Command<C> command) {
        if (command.senderType().isPresent() && !GenericTypeReflector.isSuperType(command.senderType().get().getType(), sender.getClass())) {
            return false;
        }
        return this.commandManager.testPermission(sender, command.commandPermission()).allowed();
    }

    protected boolean isNodeVisible(@NonNull CommandNode<C> node) {
        Command<C> owningCommand;
        CommandComponent<C> component = node.component();
        if (component != null && (owningCommand = node.command()) != null && this.commandFilter.test(owningCommand)) {
            return true;
        }
        for (CommandNode<C> childNode : node.children()) {
            if (!this.isNodeVisible(childNode)) continue;
            return true;
        }
        return false;
    }
}

