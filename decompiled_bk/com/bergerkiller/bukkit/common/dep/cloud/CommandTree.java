/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.context.ParsingContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.AmbiguousNodeException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.ArgumentParseException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidCommandSenderException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidSyntaxException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.NoCommandInLeafException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.NoPermissionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.NoSuchCommandException;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.internal.SuggestionContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlagParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.LiteralParser;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResult;
import com.bergerkiller.bukkit.common.dep.cloud.setting.ManagerSetting;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.util.CompletableFutures;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class CommandTree<C> {
    private final Object commandLock = new Object();
    private final CommandNode<C> internalTree = new CommandNode(null);
    private final CommandManager<C> commandManager;

    private CommandTree(@NonNull CommandManager<C> commandManager) {
        this.commandManager = commandManager;
    }

    public static <C> @NonNull CommandTree<C> newTree(@NonNull CommandManager<C> commandManager) {
        return new CommandTree<C>(commandManager);
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandManager<C> commandManager() {
        return this.commandManager;
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull CommandNode<C>> rootNodes() {
        return this.internalTree.children();
    }

    public @Nullable CommandNode<C> getNamedNode(@Nullable String name) {
        for (CommandNode<C> node : this.rootNodes()) {
            CommandComponent<C> component = node.component();
            if (component == null || component.type() != CommandComponent.ComponentType.LITERAL) continue;
            for (String alias : component.aliases()) {
                if (!alias.equalsIgnoreCase(name)) continue;
                return node;
            }
        }
        return null;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CompletableFuture<@Nullable Command<C>> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput, @NonNull Executor parsingExecutor) {
        return CompletableFutures.scheduleOn(parsingExecutor, () -> this.parseDirect(commandContext, commandInput, parsingExecutor)).thenApply(command -> {
            if (command != null) {
                commandContext.command((Command)command);
            }
            return command;
        });
    }

    private @NonNull CompletableFuture<@Nullable Command<C>> parseDirect(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput, @NonNull Executor parsingExecutor) {
        if (this.internalTree.isLeaf() && this.internalTree.component() == null) {
            return CompletableFutures.failedFuture(new NoSuchCommandException(commandContext.sender(), new ArrayList(), commandInput.peekString()));
        }
        return this.parseCommand(new ArrayList<CommandComponent<C>>(), commandContext, commandInput, this.internalTree, parsingExecutor).thenCompose(command -> {
            if (command != null && command.senderType().isPresent() && !GenericTypeReflector.isSuperType(command.senderType().get().getType(), commandContext.sender().getClass())) {
                return CompletableFutures.failedFuture(new InvalidCommandSenderException(commandContext.sender(), command.senderType().get().getType(), (List<CommandComponent<?>>)new ArrayList(command.components()), (Command<?>)command));
            }
            return CompletableFuture.completedFuture(command);
        });
    }

    private @NonNull CompletableFuture<@Nullable Command<C>> parseCommand(@NonNull List<@NonNull CommandComponent<C>> parsedArguments, @NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput, @NonNull CommandNode<C> root, @NonNull Executor executor) {
        Optional<PermissionResult> permissionResult = this.determineAccess(commandContext.sender(), root);
        if (!permissionResult.isPresent()) {
            return CompletableFutures.failedFuture(new InvalidCommandSenderException(commandContext.sender(), root.nodeMeta().get(CommandNode.META_KEY_SENDER_TYPES), this.getComponentChain(root), null));
        }
        if (permissionResult.get().denied()) {
            return CompletableFutures.failedFuture(new NoPermissionException(permissionResult.get(), commandContext.sender(), this.getComponentChain(root)));
        }
        CompletableFuture<@Nullable Command<C>> parsedChild = this.attemptParseUnambiguousChild(parsedArguments, commandContext, root, commandInput, executor);
        if (parsedChild != null) {
            return parsedChild;
        }
        if (root.children().isEmpty()) {
            CommandComponent<C> rootComponent = root.component();
            if (rootComponent == null || root.command() == null || !commandInput.isEmpty()) {
                return CompletableFutures.failedFuture(new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), parsedArguments, root), commandContext.sender(), this.getComponentChain(root)));
            }
            return CompletableFuture.completedFuture(root.command());
        }
        CompletionStage<Object> childCompletable = CompletableFuture.completedFuture(null);
        for (CommandNode<C> child : new ArrayList<CommandNode<C>>(root.children())) {
            if (child.component() == null) continue;
            childCompletable = childCompletable.thenCompose(previousResult -> {
                if (previousResult != null) {
                    return CompletableFuture.completedFuture(previousResult);
                }
                CommandComponent component = Objects.requireNonNull(child.component());
                ParsingContext parsingContext = commandContext.createParsingContext(component);
                commandInput.skipWhitespace(1);
                CommandInput currentInput = commandInput.copy();
                parsingContext.markStart();
                return component.parser().parseFuture(commandContext, commandInput).thenComposeAsync(result -> {
                    parsingContext.markEnd();
                    parsingContext.success(!result.failure().isPresent());
                    parsingContext.consumedInput(currentInput, commandInput);
                    if (result.parsedValue().isPresent()) {
                        parsedArguments.add(component);
                        return this.parseCommand(parsedArguments, commandContext, commandInput, child, executor);
                    }
                    if (result.failure().isPresent()) {
                        commandInput.cursor(currentInput.cursor());
                    }
                    return CompletableFuture.completedFuture(null);
                }, executor);
            });
        }
        return childCompletable.thenCompose(completedCommand -> {
            if (completedCommand != null) {
                return CompletableFuture.completedFuture(completedCommand);
            }
            if (root.equals(this.internalTree)) {
                return CompletableFutures.failedFuture(new NoSuchCommandException(commandContext.sender(), this.getChain(root).stream().map(CommandNode::component).collect(Collectors.toList()), commandInput.peekString()));
            }
            CommandComponent rootComponent = root.component();
            if (rootComponent != null && root.command() != null && commandInput.isEmpty()) {
                Command command = root.command();
                PermissionResult check = this.commandManager.testPermission(commandContext.sender(), command.commandPermission());
                if (check.denied()) {
                    return CompletableFutures.failedFuture(new NoPermissionException(check, commandContext.sender(), this.getComponentChain(root)));
                }
                return CompletableFuture.completedFuture(root.command());
            }
            return CompletableFutures.failedFuture(new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), parsedArguments, root), commandContext.sender(), this.getComponentChain(root)));
        });
    }

    private @Nullable CompletableFuture<@Nullable Command<C>> attemptParseUnambiguousChild(@NonNull List<@NonNull CommandComponent<C>> parsedArguments, @NonNull CommandContext<C> commandContext, @NonNull CommandNode<C> root, @NonNull CommandInput commandInput, @NonNull Executor executor) {
        C sender = commandContext.sender();
        List<CommandNode<C>> children = root.children();
        if (!commandInput.isEmpty() && this.matchesLiteral(children, commandInput.peekString())) {
            return null;
        }
        List argumentNodes = children.stream().filter(n -> n.component() != null && n.component().type() != CommandComponent.ComponentType.LITERAL).collect(Collectors.toList());
        if (argumentNodes.size() > 1) {
            throw new IllegalStateException("Unexpected ambiguity detected, number of dynamic child nodes should not exceed 1");
        }
        if (argumentNodes.isEmpty()) {
            return null;
        }
        CommandNode child = (CommandNode)argumentNodes.get(0);
        Optional<PermissionResult> childCheck = this.determineAccess(sender, child);
        if (!childCheck.isPresent()) {
            return CompletableFutures.failedFuture(new InvalidCommandSenderException(sender, child.nodeMeta().get(CommandNode.META_KEY_SENDER_TYPES), this.getComponentChain(child), null));
        }
        if (!commandInput.isEmpty() && childCheck.get().denied()) {
            return CompletableFutures.failedFuture(new NoPermissionException(childCheck.get(), sender, this.getComponentChain(child)));
        }
        if (child.component() == null) {
            return null;
        }
        ArgumentParseResult<?> argumentValue = null;
        if (commandInput.isEmpty() && child.component().type() != CommandComponent.ComponentType.FLAG) {
            CommandComponent childComponent = Objects.requireNonNull(child.component());
            if (childComponent.hasDefaultValue()) {
                DefaultValue<C, ?> defaultValue = Objects.requireNonNull(childComponent.defaultValue(), "defaultValue");
                if (defaultValue instanceof DefaultValue.ParsedDefaultValue) {
                    return this.attemptParseUnambiguousChild(parsedArguments, commandContext, root, commandInput.appendString(((DefaultValue.ParsedDefaultValue)defaultValue).value()), executor);
                }
                argumentValue = defaultValue.evaluateDefault(commandContext);
            } else {
                if (!child.component().required()) {
                    if (child.command() == null) {
                        CommandNode node = child;
                        while (!node.isLeaf()) {
                            CommandComponent nodeComponent = (node = node.children().get(0)).component();
                            if (nodeComponent == null || node.command() == null) continue;
                            child.command(node.command());
                        }
                    }
                    return CompletableFuture.completedFuture(child.command());
                }
                if (child.isLeaf()) {
                    CommandComponent<C> rootComponent = root.component();
                    if (rootComponent == null || root.command() == null) {
                        List components = Objects.requireNonNull(child.command()).components();
                        return CompletableFutures.failedFuture(new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), components, child), sender, this.getComponentChain(root)));
                    }
                    Command<C> command = root.command();
                    PermissionResult check = this.commandManager().testPermission(sender, command.commandPermission());
                    if (check.allowed()) {
                        return CompletableFuture.completedFuture(command);
                    }
                    return CompletableFutures.failedFuture(new NoPermissionException(check, sender, this.getComponentChain(root)));
                }
                CommandComponent<C> rootComponent = root.component();
                if (rootComponent == null || root.command() == null) {
                    return CompletableFutures.failedFuture(new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), parsedArguments, root), sender, this.getComponentChain(root)));
                }
                Command<C> command = Objects.requireNonNull(root.command());
                PermissionResult check = this.commandManager().testPermission(sender, command.commandPermission());
                if (check.allowed()) {
                    return CompletableFuture.completedFuture(command);
                }
                return CompletableFutures.failedFuture(new NoPermissionException(check, sender, this.getComponentChain(root)));
            }
        }
        CommandComponent component = Objects.requireNonNull(child.component());
        CompletionStage parseResult = argumentValue != null ? (argumentValue.parsedValue().isPresent() ? CompletableFuture.completedFuture(argumentValue.parsedValue().get()) : CompletableFutures.failedFuture(this.argumentParseException(commandContext, child, argumentValue))) : this.parseArgument(commandContext, child, commandInput, executor).thenApply(result -> result.parsedValue().orElse(null));
        return ((CompletableFuture)parseResult).thenComposeAsync(value -> {
            if (value == null) {
                return CompletableFuture.completedFuture(null);
            }
            commandContext.store(component.name(), value);
            if (child.isLeaf()) {
                if (commandInput.isEmpty()) {
                    return CompletableFuture.completedFuture(child.command());
                }
                return CompletableFutures.failedFuture(new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), parsedArguments, child), sender, this.getComponentChain(root)));
            }
            parsedArguments.add(Objects.requireNonNull(child.component()));
            return this.parseCommand(parsedArguments, commandContext, commandInput, child, executor);
        }, executor);
    }

    private boolean matchesLiteral(@NonNull List<@NonNull CommandNode<C>> children, @NonNull String input) {
        return children.stream().map(CommandNode::component).filter(Objects::nonNull).filter(n -> n.type() == CommandComponent.ComponentType.LITERAL).flatMap(arg -> Stream.concat(Stream.of(arg.name()), arg.aliases().stream())).anyMatch(arg -> arg.equals(input));
    }

    private @NonNull CompletableFuture<ArgumentParseResult<?>> parseArgument(@NonNull CommandContext<C> commandContext, @NonNull CommandNode<C> node, @NonNull CommandInput commandInput, @NonNull Executor executor) {
        ParsingContext<C> parsingContext = commandContext.createParsingContext(node.component());
        parsingContext.markStart();
        ArgumentParseResult<Boolean> preParseResult = node.component().preprocess(commandContext, commandInput);
        if (preParseResult.failure().isPresent() || !preParseResult.parsedValue().orElse(false).booleanValue()) {
            parsingContext.markEnd();
            parsingContext.success(false);
            if (preParseResult.failure().isPresent()) {
                return CompletableFutures.failedFuture(this.argumentParseException(commandContext, node, preParseResult));
            }
            return CompletableFuture.completedFuture(preParseResult);
        }
        commandInput.skipWhitespace(1);
        CommandInput currentInput = commandInput.copy();
        return node.component().parser().parseFuture(commandContext, commandInput).thenComposeAsync(result -> {
            parsingContext.consumedInput(currentInput, commandInput);
            parsingContext.markEnd();
            parsingContext.success(false);
            if (result.failure().isPresent()) {
                commandInput.cursor(currentInput.cursor());
                return CompletableFutures.failedFuture(this.argumentParseException(commandContext, node, (ArgumentParseResult<?>)result));
            }
            return CompletableFuture.completedFuture(result);
        }, executor);
    }

    private @NonNull ArgumentParseException argumentParseException(CommandContext<C> commandContext, CommandNode<C> node, ArgumentParseResult<?> result) {
        return new ArgumentParseException(result.failure().get(), commandContext.sender(), this.getComponentChain(node));
    }

    @API(status=API.Status.STABLE)
    public <S extends Suggestion> @NonNull CompletableFuture<@NonNull Suggestions<C, S>> getSuggestions(@NonNull CommandContext<C> context, @NonNull CommandInput commandInput, @NonNull SuggestionMapper<S> mapper, @NonNull Executor executor) {
        return CompletableFutures.scheduleOn(executor, () -> this.getSuggestionsDirect(context, commandInput, mapper, executor));
    }

    private <S extends Suggestion> @NonNull CompletableFuture<@NonNull Suggestions<C, S>> getSuggestionsDirect(@NonNull CommandContext<C> context, @NonNull CommandInput commandInput, @NonNull SuggestionMapper<S> mapper, @NonNull Executor executor) {
        SuggestionContext suggestionCtx = new SuggestionContext(this.commandManager.suggestionProcessor(), context, commandInput, mapper);
        return this.getSuggestions(suggestionCtx, commandInput, this.internalTree, executor).thenApply($ -> suggestionCtx.makeSuggestions());
    }

    private @NonNull CompletableFuture<SuggestionContext<C, ?>> getSuggestions(@NonNull SuggestionContext<C, ?> context, @NonNull CommandInput commandInput, @NonNull CommandNode<C> root, @NonNull Executor executor) {
        if (!this.determineAccess(context.commandContext().sender(), root).map(PermissionResult::allowed).orElse(false).booleanValue()) {
            return CompletableFuture.completedFuture(context);
        }
        List<CommandNode<C>> children = root.children();
        List staticArguments = children.stream().filter(n -> n.component() != null).filter(n -> n.component().type() == CommandComponent.ComponentType.LITERAL).collect(Collectors.toList());
        if (!commandInput.isEmpty()) {
            commandInput.skipWhitespace(1);
        }
        if (!staticArguments.isEmpty() && !commandInput.isEmpty(true)) {
            CommandInput commandInputCopy = commandInput.copy();
            for (CommandNode commandNode : staticArguments) {
                CommandComponent childComponent = commandNode.component();
                if (childComponent == null) continue;
                ArgumentParseResult<?> result = childComponent.parser().parse(context.commandContext(), commandInput);
                if (result.failure().isPresent()) {
                    commandInput.cursor(commandInputCopy.cursor());
                }
                if (!result.parsedValue().isPresent()) continue;
                if (commandInput.isEmpty()) break;
                return this.getSuggestions(context, commandInput, commandNode, executor);
            }
            commandInput.cursor(commandInputCopy.cursor());
        }
        CompletionStage<SuggestionContext<C, Object>> suggestionFuture = CompletableFuture.completedFuture(context);
        if (commandInput.remainingTokens() <= 1) {
            for (CommandNode commandNode : staticArguments) {
                suggestionFuture = ((CompletableFuture)suggestionFuture).thenCompose(ctx -> this.addSuggestionsForLiteralArgument(context, node, commandInput));
            }
        }
        for (CommandNode commandNode : root.children()) {
            if (commandNode.component() == null || commandNode.component().type() == CommandComponent.ComponentType.LITERAL) continue;
            suggestionFuture = ((CompletableFuture)suggestionFuture).thenCompose(ctx -> this.addSuggestionsForDynamicArgument(context, commandInput, child, executor, false));
        }
        return suggestionFuture;
    }

    private CompletableFuture<SuggestionContext<C, ?>> addSuggestionsForLiteralArgument(@NonNull SuggestionContext<C, ?> context, @NonNull CommandNode<C> node, @NonNull CommandInput input) {
        if (!this.determineAccess(context.commandContext().sender(), node).map(PermissionResult::allowed).orElse(false).booleanValue()) {
            return CompletableFuture.completedFuture(context);
        }
        CommandComponent<C> component = Objects.requireNonNull(node.component());
        return component.suggestionProvider().suggestionsFuture(context.commandContext(), input.copy()).thenApply(suggestionsToAdd -> {
            String string = input.peekString();
            for (Suggestion suggestion : suggestionsToAdd) {
                if (suggestion.suggestion().equals(string) || !suggestion.suggestion().startsWith(string)) continue;
                context.addSuggestion(suggestion);
            }
            return context;
        });
    }

    private @NonNull CompletableFuture<SuggestionContext<C, ?>> addSuggestionsForDynamicArgument(@NonNull SuggestionContext<C, ?> context, @NonNull CommandInput commandInput, @NonNull CommandNode<C> child, @NonNull Executor executor, boolean inFlag) {
        CompletionStage<Object> parsingFuture;
        boolean preParseSuccess;
        CommandComponent<C> component = child.component();
        if (component == null) {
            return CompletableFuture.completedFuture(context);
        }
        if (!inFlag && component.parser() instanceof CommandFlagParser) {
            CommandFlagParser parser = (CommandFlagParser)component.parser();
            return parser.parseCurrentFlag(context.commandContext(), commandInput, executor).thenCompose(lastFlag -> {
                if (lastFlag.isPresent()) {
                    context.commandContext().store(CommandFlagParser.FLAG_META_KEY, (String)lastFlag.get());
                } else {
                    context.commandContext().remove(CommandFlagParser.FLAG_META_KEY);
                }
                return this.addSuggestionsForDynamicArgument(context, commandInput, child, executor, true);
            });
        }
        if (commandInput.isEmpty() || commandInput.remainingTokens() == 1 || child.isLeaf() && child.component().parser() instanceof AggregateParser || child.isLeaf() && child.component().parser() instanceof CommandFlagParser) {
            return this.addArgumentSuggestions(context, child, commandInput, executor);
        }
        CommandInput commandInputOriginal = commandInput.copy();
        ArgumentParseResult<Boolean> preParseResult = component.preprocess(context.commandContext(), commandInput);
        boolean bl = preParseSuccess = !preParseResult.failure().isPresent() && preParseResult.parsedValue().orElse(false) != false;
        if (!preParseSuccess) {
            parsingFuture = CompletableFuture.completedFuture(null);
        } else {
            ParsingContext<C> parsingContext = context.commandContext().createParsingContext(child.component());
            parsingContext.markStart();
            CommandInput preParseInput = commandInput.copy();
            parsingFuture = child.component().parser().parseFuture(context.commandContext(), commandInput).thenComposeAsync(result -> {
                Optional parsedValue = result.parsedValue();
                boolean parseSuccess = parsedValue.isPresent();
                if (result.failure().isPresent()) {
                    commandInput.cursor(preParseInput.cursor());
                    return this.addArgumentSuggestions(context, child, commandInput, executor);
                }
                if (child.isLeaf()) {
                    if (!commandInput.isEmpty()) {
                        return CompletableFuture.completedFuture(context);
                    }
                    commandInput.cursor(commandInputOriginal.cursor());
                    this.addArgumentSuggestions(context, child, commandInput, executor);
                }
                if (parseSuccess && (!commandInput.isEmpty() || commandInput.input().endsWith(" "))) {
                    if (commandInput.isEmpty()) {
                        commandInput.moveCursor(-1);
                    }
                    context.commandContext().store(child.component().name(), parsedValue.get());
                    parsingContext.success(true);
                    return this.getSuggestions(context, commandInput, child, executor);
                }
                if (!parseSuccess && commandInputOriginal.remainingTokens() > 1) {
                    commandInput.cursor(commandInputOriginal.cursor());
                    return CompletableFuture.completedFuture(context);
                }
                return CompletableFuture.completedFuture(null);
            }, executor);
        }
        return parsingFuture.thenCompose(previousResult -> {
            if (previousResult != null) {
                return CompletableFuture.completedFuture(previousResult);
            }
            commandInput.cursor(commandInputOriginal.cursor());
            if (!preParseSuccess && commandInput.remainingTokens() > 1) {
                return CompletableFuture.completedFuture(context);
            }
            return this.addArgumentSuggestions(context, child, commandInput, executor);
        });
    }

    private @NonNull CompletableFuture<SuggestionContext<C, ?>> addArgumentSuggestions(@NonNull SuggestionContext<C, ?> context, @NonNull CommandNode<C> node, @NonNull CommandInput input, @NonNull Executor executor) {
        CommandComponent<C> component = Objects.requireNonNull(node.component());
        return this.addArgumentSuggestions(context, component, input, executor).thenCompose(ctx -> {
            boolean isParsingFlag;
            boolean bl = isParsingFlag = component.type() == CommandComponent.ComponentType.FLAG && !node.children().isEmpty() && (!input.hasRemainingInput() || input.peek() != '-') && !context.commandContext().optional(CommandFlagParser.FLAG_META_KEY).isPresent();
            if (!isParsingFlag) {
                return CompletableFuture.completedFuture(ctx);
            }
            return CompletableFuture.allOf((CompletableFuture[])node.children().stream().map(child -> this.addArgumentSuggestions(context, Objects.requireNonNull(child.component()), input, executor)).toArray(CompletableFuture[]::new)).thenApply(v -> ctx);
        });
    }

    private CompletableFuture<SuggestionContext<C, ?>> addArgumentSuggestions(@NonNull SuggestionContext<C, ?> context, @NonNull CommandComponent<C> component, @NonNull CommandInput input, @NonNull Executor executor) {
        return ((CompletableFuture)component.suggestionProvider().suggestionsFuture(context.commandContext(), input.copy()).thenAcceptAsync(context::addSuggestions, executor)).thenApply(in -> context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insertCommand(@NonNull Command<C> command) {
        Object object = this.commandLock;
        synchronized (object) {
            CommandComponent<C> flagComponent = command.flagComponent();
            List<CommandComponent<C>> nonFlagArguments = command.nonFlagArguments();
            int flagStartIdx = this.flagStartIndex(nonFlagArguments);
            CommandNode<C> node = this.internalTree;
            for (int i = 0; i < nonFlagArguments.size(); ++i) {
                CommandComponent<C> component = nonFlagArguments.get(i);
                CommandNode<C> tempNode = node.getChild(component);
                if (tempNode == null) {
                    tempNode = node.addChild(component);
                } else if (component.type() == CommandComponent.ComponentType.LITERAL && tempNode.component() != null) {
                    for (String alias : component.aliases()) {
                        ((LiteralParser)tempNode.component().parser()).insertAlias(alias);
                    }
                }
                if (!node.children().isEmpty()) {
                    node.sortChildren();
                }
                tempNode.parent(node);
                node = tempNode;
                if (flagComponent == null || i < flagStartIdx) continue;
                tempNode = node.addChild(flagComponent);
                tempNode.parent(node);
                node = tempNode;
            }
            CommandComponent<C> nodeComponent = node.component();
            if (nodeComponent != null) {
                if (node.command() != null) {
                    throw new IllegalStateException(String.format("Duplicate command chains detected. Node '%s' already has an owning command (%s)", node, node.command()));
                }
                node.command(command);
            }
            this.verifyAndRegister();
        }
    }

    private int flagStartIndex(@NonNull List<CommandComponent<C>> components) {
        if (this.commandManager.settings().get(ManagerSetting.LIBERAL_FLAG_PARSING)) {
            for (int i = components.size() - 1; i >= 0; --i) {
                if (components.get(i).type() != CommandComponent.ComponentType.LITERAL) continue;
                return i;
            }
        }
        return components.size() - 1;
    }

    private Optional<PermissionResult> determineAccess(@NonNull C sender, @NonNull CommandNode<C> node) {
        Map<Type, Permission> accessMap = node.nodeMeta().getOrNull(CommandNode.META_KEY_ACCESS);
        if (accessMap == null) {
            throw new IllegalStateException("Expected access requirements to be propagated");
        }
        HashSet<Permission> failed = new HashSet<Permission>();
        for (Map.Entry<Type, Permission> entry : accessMap.entrySet()) {
            if (!GenericTypeReflector.isSuperType(entry.getKey(), sender.getClass())) continue;
            PermissionResult result = this.commandManager.testPermission(sender, entry.getValue());
            if (result.allowed()) {
                return Optional.of(result);
            }
            failed.add(entry.getValue());
        }
        if (failed.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(PermissionResult.denied(Permission.anyOf(failed)));
    }

    private void verifyAndRegister() {
        this.internalTree.children().stream().map(CommandNode::component).forEach(component -> {
            if (component.type() != CommandComponent.ComponentType.LITERAL) {
                throw new IllegalStateException("Top level command argument cannot be a variable");
            }
        });
        this.checkAmbiguity(this.internalTree);
        this.getLeaves(this.internalTree).forEach(leaf -> {
            if (leaf.command() == null) {
                throw new NoCommandInLeafException(leaf.component());
            }
            Command owningCommand = leaf.command();
            this.commandManager.commandRegistrationHandler().registerCommand(owningCommand);
        });
        this.getExecutorNodes(this.internalTree).forEach(this::propagateRequirements);
    }

    @API(status=API.Status.INTERNAL)
    public @NonNull CommandNode<C> rootNode() {
        return this.internalTree;
    }

    private void propagateRequirements(@NonNull CommandNode<C> leafNode) {
        Permission commandPermission = leafNode.command().commandPermission();
        Object senderType = leafNode.command().senderType().map(TypeToken::getType).orElse(null);
        if (senderType == null) {
            senderType = Object.class;
        }
        List<CommandNode<C>> chain = this.getChain(leafNode);
        Collections.reverse(chain);
        for (CommandNode<C> commandArgumentNode : chain) {
            Set senderTypes = commandArgumentNode.nodeMeta().computeIfAbsent(CommandNode.META_KEY_SENDER_TYPES, $ -> new HashSet());
            CommandTree.updateSenderRequirements(senderTypes, (Type)senderType);
            Map accessMap = commandArgumentNode.nodeMeta().computeIfAbsent(CommandNode.META_KEY_ACCESS, $ -> new HashMap());
            CommandTree.updateAccess(accessMap, (Type)senderType, commandPermission);
        }
    }

    private static void updateAccess(Map<Type, Permission> senderTypes, Type senderType, Permission commandPermission) {
        senderTypes.compute(senderType, (key, existing) -> {
            if (existing == null) {
                return commandPermission;
            }
            return Permission.anyOf(existing, commandPermission);
        });
    }

    private static void updateSenderRequirements(Set<Type> senderTypes, Type senderType) {
        boolean add = true;
        Iterator<Type> iterator = senderTypes.iterator();
        while (iterator.hasNext()) {
            Type existingType = iterator.next();
            if (GenericTypeReflector.isSuperType(existingType, senderType)) {
                add = false;
                break;
            }
            if (!GenericTypeReflector.isSuperType(senderType, existingType)) continue;
            iterator.remove();
            break;
        }
        if (add) {
            senderTypes.add(senderType);
        }
    }

    private void checkAmbiguity(@NonNull CommandNode<C> node) throws AmbiguousNodeException {
        if (node.isLeaf()) {
            return;
        }
        List childVariableArguments = node.children().stream().filter(n -> n.component() != null).filter(n -> n.component().type() != CommandComponent.ComponentType.LITERAL).collect(Collectors.toList());
        if (childVariableArguments.size() > 1) {
            CommandNode child = (CommandNode)childVariableArguments.get(0);
            throw new AmbiguousNodeException(node, child, node.children().stream().filter(n -> n.component() != null).collect(Collectors.toList()));
        }
        List childStaticArguments = node.children().stream().filter(n -> n.component() != null).filter(n -> n.component().type() == CommandComponent.ComponentType.LITERAL).collect(Collectors.toList());
        HashSet<String> checkedLiterals = new HashSet<String>();
        for (CommandNode child : childStaticArguments) {
            for (String nameOrAlias : child.component().aliases()) {
                if (checkedLiterals.add(nameOrAlias)) continue;
                throw new AmbiguousNodeException(node, child, node.children().stream().filter(n -> n.component() != null).collect(Collectors.toList()));
            }
        }
        node.children().forEach(this::checkAmbiguity);
    }

    @API(status=API.Status.INTERNAL)
    public @NonNull List<@NonNull CommandNode<C>> getLeavesRaw(@NonNull CommandNode<C> node) {
        LinkedList<CommandNode<C>> leaves = new LinkedList<CommandNode<C>>();
        if (node.isLeaf()) {
            if (node.component() != null) {
                leaves.add(node);
            }
        } else {
            node.children().forEach(child -> leaves.addAll(this.getLeavesRaw((CommandNode<C>)child)));
        }
        return leaves;
    }

    private @NonNull List<@NonNull CommandNode<C>> getExecutorNodes(@NonNull CommandNode<C> node) {
        LinkedList<CommandNode<C>> leaves = new LinkedList<CommandNode<C>>();
        if (node.command() != null) {
            leaves.add(node);
        }
        for (CommandNode<C> child : node.children()) {
            leaves.addAll(this.getExecutorNodes(child));
        }
        return leaves;
    }

    @API(status=API.Status.INTERNAL)
    public @NonNull List<@NonNull CommandNode<C>> getLeaves(@NonNull CommandNode<C> node) {
        return this.getLeavesRaw(node).stream().filter(n -> n.component() != null).collect(Collectors.toList());
    }

    private @NonNull List<@NonNull CommandComponent<?>> getComponentChain(@NonNull CommandNode<C> end) {
        return this.getChain(end).stream().map(CommandNode::component).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private @NonNull List<@NonNull CommandNode<C>> getChain(@Nullable CommandNode<C> end) {
        LinkedList<CommandNode<C>> chain = new LinkedList<CommandNode<C>>();
        for (CommandNode<C> tail = end; tail != null; tail = tail.parent()) {
            chain.add(tail);
        }
        Collections.reverse(chain);
        return chain;
    }

    void deleteRecursively(@NonNull CommandNode<C> node, boolean root, Consumer<Command<C>> commandConsumer) {
        Command<C> owner;
        for (CommandNode<C> child : new ArrayList<CommandNode<C>>(node.children())) {
            this.deleteRecursively(child, false, commandConsumer);
        }
        @Nullable CommandComponent<C> component = node.component();
        Command<C> command = owner = component == null ? null : node.command();
        if (owner != null) {
            commandConsumer.accept(owner);
        }
        this.removeNode(node, root);
    }

    private void removeNode(@NonNull CommandNode<C> node, boolean root) {
        if (root) {
            this.internalTree.removeChild(node);
        } else {
            Objects.requireNonNull(node.parent(), "parent").removeChild(node);
        }
    }
}

