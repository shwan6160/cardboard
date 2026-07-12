/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.CloudCapability;
import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandBuilderSource;
import com.bergerkiller.bukkit.common.dep.cloud.CommandFactory;
import com.bergerkiller.bukkit.common.dep.cloud.CommandTree;
import com.bergerkiller.bukkit.common.dep.cloud.DefaultExceptionHandlers;
import com.bergerkiller.bukkit.common.dep.cloud.StandardCommandExecutor;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionsProvider;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.context.StandardCommandContextFactory;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionController;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutor;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.AcceptingCommandPostprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.AcceptingCommandPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.help.CommandPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandler;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandlerFactory;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.StandardParserRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.permission.AndPermission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.OrPermission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResult;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PredicatePermission;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePipeline;
import com.bergerkiller.bukkit.common.dep.cloud.services.State;
import com.bergerkiller.bukkit.common.dep.cloud.setting.Configurable;
import com.bergerkiller.bukkit.common.dep.cloud.setting.ManagerSetting;
import com.bergerkiller.bukkit.common.dep.cloud.state.RegistrationState;
import com.bergerkiller.bukkit.common.dep.cloud.state.Stateful;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.DelegatingSuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.FilteringSuggestionProcessor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProcessor;
import com.bergerkiller.bukkit.common.dep.cloud.syntax.CommandSyntaxFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.syntax.StandardCommandSyntaxFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Triplet;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public abstract class CommandManager<C>
implements Stateful<RegistrationState>,
CommandBuilderSource<C> {
    private final Configurable<ManagerSetting> settings = Configurable.enumConfigurable(ManagerSetting.class);
    private final ServicePipeline servicePipeline = ServicePipeline.builder().build();
    private final ParserRegistry<C> parserRegistry = new StandardParserRegistry();
    private final Collection<Command<C>> commands = new LinkedList<Command<C>>();
    private final ParameterInjectorRegistry<C> parameterInjectorRegistry = new ParameterInjectorRegistry();
    private final CommandTree<C> commandTree;
    private final SuggestionFactory<C, ? extends Suggestion> suggestionFactory;
    private final Set<CloudCapability> capabilities = new HashSet<CloudCapability>();
    private final ExceptionController<C> exceptionController = new ExceptionController();
    private final CommandExecutor<C> commandExecutor;
    private CaptionFormatter<C, String> captionVariableReplacementHandler = CaptionFormatter.placeholderReplacing();
    private CommandSyntaxFormatter<C> commandSyntaxFormatter = new StandardCommandSyntaxFormatter(this);
    private SuggestionProcessor<C> suggestionProcessor = new FilteringSuggestionProcessor();
    private CommandRegistrationHandler<C> commandRegistrationHandler;
    private CaptionRegistry<C> captionRegistry;
    private HelpHandlerFactory<C> helpHandlerFactory = HelpHandlerFactory.standard(this);
    private SuggestionMapper<? extends Suggestion> mapper = SuggestionMapper.identity();
    private final AtomicReference<RegistrationState> state = new AtomicReference<RegistrationState>(RegistrationState.BEFORE_REGISTRATION);

    protected CommandManager(@NonNull ExecutionCoordinator<C> executionCoordinator, @NonNull CommandRegistrationHandler<C> commandRegistrationHandler) {
        StandardCommandContextFactory commandContextFactory = new StandardCommandContextFactory(this);
        this.commandTree = CommandTree.newTree(this);
        this.commandRegistrationHandler = commandRegistrationHandler;
        this.suggestionFactory = new DelegatingSuggestionFactory<C, Suggestion>(this, this.commandTree, commandContextFactory, executionCoordinator, suggestion -> this.mapper.map(suggestion));
        this.commandExecutor = new StandardCommandExecutor<C>(this, executionCoordinator, commandContextFactory);
        this.servicePipeline.registerServiceType(new TypeToken<CommandPreprocessor<C>>(){}, new AcceptingCommandPreprocessor());
        this.servicePipeline.registerServiceType(new TypeToken<CommandPostprocessor<C>>(){}, new AcceptingCommandPostprocessor());
        this.captionRegistry = CaptionRegistry.captionRegistry();
        this.captionRegistry.registerProvider(new StandardCaptionsProvider());
        this.parameterInjectorRegistry().registerInjector(CommandContext.class, (context, annotationAccessor) -> context);
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandExecutor<C> commandExecutor() {
        return this.commandExecutor;
    }

    @API(status=API.Status.STABLE)
    public @NonNull SuggestionFactory<C, ? extends Suggestion> suggestionFactory() {
        return this.suggestionFactory;
    }

    public @NonNull SuggestionMapper<? extends Suggestion> suggestionMapper() {
        return this.mapper;
    }

    public void appendSuggestionMapper(@NonNull SuggestionMapper<? extends Suggestion> mapper) {
        this.suggestionMapper(this.suggestionMapper().then(mapper));
    }

    public void suggestionMapper(@NonNull SuggestionMapper<? extends Suggestion> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public @This @NonNull CommandManager<C> command(@NonNull Command<? extends C> command) {
        if (!this.transitionIfPossible(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING) && !this.isCommandRegistrationAllowed()) {
            throw new IllegalStateException("Unable to register commands because the manager is no longer in a registration state. Your platform may allow unsafe registrations by enabling the appropriate manager setting.");
        }
        this.commandTree.insertCommand(command);
        this.commands.add(command);
        return this;
    }

    @API(status=API.Status.STABLE)
    public @This @NonNull CommandManager<C> command(@NonNull CommandFactory<C> commandFactory) {
        commandFactory.createCommands(this).forEach(this::command);
        return this;
    }

    public @NonNull CommandManager<C> command(@NonNull Command.Builder<? extends C> command) {
        return this.command(command.manager(this).build());
    }

    @API(status=API.Status.STABLE)
    public @NonNull CaptionFormatter<C, String> captionFormatter() {
        return this.captionVariableReplacementHandler;
    }

    @API(status=API.Status.STABLE)
    public void captionFormatter(@NonNull CaptionFormatter<C, String> captionFormatter) {
        this.captionVariableReplacementHandler = captionFormatter;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandSyntaxFormatter<C> commandSyntaxFormatter() {
        return this.commandSyntaxFormatter;
    }

    @API(status=API.Status.STABLE)
    public void commandSyntaxFormatter(@NonNull CommandSyntaxFormatter<C> commandSyntaxFormatter) {
        this.commandSyntaxFormatter = commandSyntaxFormatter;
    }

    public @NonNull CommandRegistrationHandler<C> commandRegistrationHandler() {
        return this.commandRegistrationHandler;
    }

    @API(status=API.Status.STABLE)
    protected final void commandRegistrationHandler(@NonNull CommandRegistrationHandler<C> commandRegistrationHandler) {
        this.requireState(RegistrationState.BEFORE_REGISTRATION);
        this.commandRegistrationHandler = commandRegistrationHandler;
    }

    @API(status=API.Status.STABLE)
    protected final void registerCapability(@NonNull CloudCapability capability) {
        this.capabilities.add(capability);
    }

    @API(status=API.Status.STABLE)
    public boolean hasCapability(@NonNull CloudCapability capability) {
        return this.capabilities.contains(capability);
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull CloudCapability> capabilities() {
        return Collections.unmodifiableSet(new HashSet<CloudCapability>(this.capabilities));
    }

    @API(status=API.Status.STABLE)
    public @NonNull PermissionResult testPermission(@NonNull C sender, @NonNull Permission permission) {
        if (permission instanceof PredicatePermission) {
            return ((PredicatePermission)permission).testPermission(sender);
        }
        if (permission instanceof OrPermission) {
            for (Permission innerPermission : permission.permissions()) {
                PermissionResult result = this.testPermission(sender, innerPermission);
                if (!result.allowed()) continue;
                return result;
            }
            return PermissionResult.denied(permission);
        }
        if (permission instanceof AndPermission) {
            for (Permission innerPermission : permission.permissions()) {
                PermissionResult result = this.testPermission(sender, innerPermission);
                if (result.allowed()) continue;
                return result;
            }
            return PermissionResult.allowed(permission);
        }
        return PermissionResult.of(permission.isEmpty() || this.hasPermission(sender, permission.permissionString()), permission);
    }

    @API(status=API.Status.STABLE)
    public final @NonNull CaptionRegistry<C> captionRegistry() {
        return this.captionRegistry;
    }

    @API(status=API.Status.STABLE)
    public final void captionRegistry(@NonNull CaptionRegistry<C> captionRegistry) {
        this.captionRegistry = captionRegistry;
    }

    public abstract boolean hasPermission(@NonNull C var1, @NonNull String var2);

    @API(status=API.Status.EXPERIMENTAL)
    public void deleteRootCommand(@NonNull String rootCommand) throws CloudCapability.CloudCapabilityMissingException {
        if (!this.hasCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION)) {
            throw new CloudCapability.CloudCapabilityMissingException(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
        }
        CommandNode<C> node = this.commandTree.getNamedNode(rootCommand);
        if (node == null || node.component() == null) {
            return;
        }
        this.commandRegistrationHandler.unregisterRootCommand(node.component());
        this.commandTree.deleteRecursively(node, true, this.commands::remove);
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull String> rootCommands() {
        return this.commandTree.rootNodes().stream().map(CommandNode::component).filter(Objects::nonNull).filter(component -> component.type() == CommandComponent.ComponentType.LITERAL).map(CommandComponent::name).collect(Collectors.toList());
    }

    @Override
    public final @NonNull Command.Builder<C> decorateBuilder(@NonNull Command.Builder<C> builder) {
        return builder.manager(this);
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull CommandComponent.Builder<C, T> componentBuilder(@NonNull Class<T> type, @NonNull String name) {
        return CommandComponent.ofType(type, name).commandManager(this);
    }

    public @NonNull CommandFlag.Builder<C, Void> flagBuilder(@NonNull String name) {
        return CommandFlag.builder(name);
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandTree<C> commandTree() {
        return this.commandTree;
    }

    @Override
    public @NonNull CommandMeta createDefaultCommandMeta() {
        return CommandMeta.empty();
    }

    public void registerCommandPreProcessor(@NonNull CommandPreprocessor<C> processor) {
        this.servicePipeline.registerServiceImplementation(new TypeToken<CommandPreprocessor<C>>(){}, processor, Collections.emptyList());
    }

    public void registerCommandPostProcessor(@NonNull CommandPostprocessor<C> processor) {
        this.servicePipeline.registerServiceImplementation(new TypeToken<CommandPostprocessor<C>>(){}, processor, Collections.emptyList());
    }

    @API(status=API.Status.STABLE)
    public State preprocessContext(@NonNull CommandContext<C> context, @NonNull CommandInput commandInput) {
        this.servicePipeline.pump(CommandPreprocessingContext.of(context, commandInput)).through(new TypeToken<CommandPreprocessor<C>>(){}).complete();
        return context.optional("__COMMAND_PRE_PROCESSED__").orElse("").isEmpty() ? State.REJECTED : State.ACCEPTED;
    }

    public State postprocessContext(@NonNull CommandContext<C> context, @NonNull Command<C> command) {
        this.servicePipeline.pump(CommandPostprocessingContext.of(context, command)).through(new TypeToken<CommandPostprocessor<C>>(){}).complete();
        return context.optional("__COMMAND_POST_PROCESSED__").orElse("").isEmpty() ? State.REJECTED : State.ACCEPTED;
    }

    public @NonNull SuggestionProcessor<C> suggestionProcessor() {
        return this.suggestionProcessor;
    }

    public void suggestionProcessor(@NonNull SuggestionProcessor<C> suggestionProcessor) {
        this.suggestionProcessor = suggestionProcessor;
    }

    @API(status=API.Status.STABLE)
    public @NonNull ParserRegistry<C> parserRegistry() {
        return this.parserRegistry;
    }

    public final @NonNull ParameterInjectorRegistry<C> parameterInjectorRegistry() {
        return this.parameterInjectorRegistry;
    }

    @API(status=API.Status.STABLE)
    public final @NonNull ExceptionController<C> exceptionController() {
        return this.exceptionController;
    }

    @API(status=API.Status.STABLE)
    public final @NonNull Collection<@NonNull Command<C>> commands() {
        return Collections.unmodifiableCollection(this.commands);
    }

    @API(status=API.Status.STABLE)
    public final @NonNull HelpHandler<C> createHelpHandler() {
        return this.helpHandlerFactory.createHelpHandler(cmd -> true);
    }

    @API(status=API.Status.STABLE)
    public final @NonNull HelpHandler<C> createHelpHandler(@NonNull CommandPredicate<C> filter) {
        return this.helpHandlerFactory.createHelpHandler(filter);
    }

    @API(status=API.Status.STABLE)
    public final @NonNull HelpHandlerFactory<C> helpHandlerFactory() {
        return this.helpHandlerFactory;
    }

    @API(status=API.Status.STABLE)
    public final void helpHandlerFactory(@NonNull HelpHandlerFactory<C> helpHandlerFactory) {
        this.helpHandlerFactory = helpHandlerFactory;
    }

    @API(status=API.Status.STABLE)
    public @NonNull Configurable<ManagerSetting> settings() {
        return this.settings;
    }

    @Override
    public final @NonNull RegistrationState state() {
        return this.state.get();
    }

    @Override
    public final boolean transitionIfPossible(@NonNull RegistrationState in, @NonNull RegistrationState out) {
        return this.state.compareAndSet(in, out) || this.state.get() == out;
    }

    @API(status=API.Status.STABLE)
    protected final void lockRegistration() {
        if (this.state() == RegistrationState.BEFORE_REGISTRATION) {
            this.transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.AFTER_REGISTRATION);
            return;
        }
        this.transitionOrThrow(RegistrationState.REGISTERING, RegistrationState.AFTER_REGISTRATION);
    }

    @API(status=API.Status.STABLE)
    public boolean isCommandRegistrationAllowed() {
        return this.settings().get(ManagerSetting.ALLOW_UNSAFE_REGISTRATION) || this.state.get() != RegistrationState.AFTER_REGISTRATION;
    }

    protected void registerDefaultExceptionHandlers(@NonNull Consumer<Triplet<CommandContext<C>, Caption, List<@NonNull CaptionVariable>>> messageSender, @NonNull Consumer<Pair<String, Throwable>> logger) {
        DefaultExceptionHandlers<C> defaultExceptionHandlers = new DefaultExceptionHandlers<C>(messageSender, logger, this.exceptionController);
        defaultExceptionHandlers.register();
    }
}

