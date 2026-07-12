/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.ArgumentMode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.BuilderDecorator;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.BuilderModifier;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandMethodExecutionHandlerFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Default;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueRegistryImpl;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DescriptionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.MetaFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.MethodCommandExecutionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.MethodDefaultValueFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.PreprocessorMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.ProxiedBy;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Regex;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxParserImpl;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler.ArgumentAssembler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler.ArgumentAssemblerImpl;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler.FlagAssembler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler.FlagAssemblerImpl;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.CommandDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.exception.ExceptionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.exception.ExceptionHandlerFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.ArgumentExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.CommandExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.CommandExtractorImpl;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.FlagExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.FlagExtractorImpl;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.StandardArgumentExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.injection.RawArgs;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.parser.MethodArgumentParserFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.parser.Parser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.processing.CommandContainer;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.string.StringProcessor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.SuggestionProviderFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.RegexPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandInputTokenizer;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMetaBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class AnnotationParser<C> {
    private static final Comparator<Class<?>> COMMAND_CONTAINER_COMPARATOR = Comparator.comparingInt(clazz -> {
        CommandContainer commandContainer = clazz.getAnnotation(CommandContainer.class);
        if (commandContainer == null) {
            return 1;
        }
        return commandContainer.priority();
    }).reversed();
    public static final String INFERRED_ARGUMENT_NAME = "__INFERRED_ARGUMENT_NAME__";
    private final CommandManager<C> manager;
    private final Map<Class<? extends Annotation>, AnnotationMapper<?>> annotationMappers;
    private final Map<Class<? extends Annotation>, PreprocessorMapper<?, C>> preprocessorMappers;
    private final Map<Class<? extends Annotation>, BuilderModifier<?, C>> builderModifiers;
    private final List<BuilderDecorator<C>> builderDecorators;
    private final Map<Predicate<Method>, CommandMethodExecutionHandlerFactory<C>> commandMethodFactories;
    private final TypeToken<C> commandSenderType;
    private final MetaFactory metaFactory;
    private StringProcessor stringProcessor;
    private SyntaxParser syntaxParser;
    private ArgumentExtractor argumentExtractor;
    private ArgumentAssembler<C> argumentAssembler;
    private FlagExtractor flagExtractor;
    private FlagAssembler flagAssembler;
    private CommandExtractor commandExtractor;
    private SuggestionProviderFactory<C> suggestionProviderFactory;
    private MethodArgumentParserFactory<C> methodArgumentParserFactory;
    private ExceptionHandlerFactory<C> exceptionHandlerFactory;
    private DescriptionMapper descriptionMapper;
    private DefaultValueRegistry<C> defaultValueRegistry;

    public AnnotationParser(@NonNull CommandManager<C> manager, @NonNull Class<C> commandSenderClass, @NonNull Function<@NonNull ParserParameters, @NonNull CommandMeta> metaMapper) {
        this(manager, TypeToken.get(commandSenderClass), metaMapper);
    }

    public AnnotationParser(@NonNull CommandManager<C> manager, @NonNull Class<C> commandSenderClass) {
        this(manager, TypeToken.get(commandSenderClass), (ParserParameters parameters) -> CommandMeta.empty());
    }

    public AnnotationParser(@NonNull CommandManager<C> manager, @NonNull TypeToken<C> commandSenderClass) {
        this(manager, commandSenderClass, (ParserParameters parameters) -> CommandMeta.empty());
    }

    @API(status=API.Status.STABLE)
    public AnnotationParser(@NonNull CommandManager<C> manager, @NonNull TypeToken<C> commandSenderType, @NonNull Function<@NonNull ParserParameters, @NonNull CommandMeta> metaMapper) {
        this.commandSenderType = commandSenderType;
        this.manager = manager;
        this.metaFactory = new MetaFactory(this, metaMapper);
        this.annotationMappers = new HashMap();
        this.preprocessorMappers = new HashMap();
        this.builderModifiers = new HashMap();
        this.commandMethodFactories = new HashMap<Predicate<Method>, CommandMethodExecutionHandlerFactory<C>>();
        this.flagExtractor = new FlagExtractorImpl(this);
        this.flagAssembler = new FlagAssemblerImpl(manager);
        this.syntaxParser = new SyntaxParserImpl();
        this.descriptionMapper = DescriptionMapper.simple();
        this.argumentExtractor = StandardArgumentExtractor.create(this);
        this.argumentAssembler = new ArgumentAssemblerImpl(this);
        this.commandExtractor = new CommandExtractorImpl(this);
        this.suggestionProviderFactory = SuggestionProviderFactory.defaultFactory();
        this.methodArgumentParserFactory = MethodArgumentParserFactory.defaultFactory();
        this.exceptionHandlerFactory = ExceptionHandlerFactory.defaultFactory();
        this.builderDecorators = new ArrayList<BuilderDecorator<C>>();
        this.defaultValueRegistry = new DefaultValueRegistryImpl();
        this.registerBuilderModifier(CommandDescription.class, (description, builder) -> builder.commandDescription(com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescription.commandDescription(this.mapDescription(description.value()))));
        this.registerPreprocessorMapper(Regex.class, annotation -> RegexPreprocessor.of(this.processString(annotation.value()), Caption.of(this.processString(annotation.failureCaption()))));
        this.manager.parameterInjectorRegistry().registerInjector(String[].class, (context, annotations) -> annotations.annotation(RawArgs.class) == null ? null : new CommandInputTokenizer(context.rawInput().remainingInput()).tokenize().toArray(new String[0]));
        this.stringProcessor = StringProcessor.noOp();
    }

    static <A extends Annotation> @Nullable A getAnnotationRecursively(@NonNull AnnotationAccessor annotations, @NonNull Class<A> clazz, @NonNull Set<Class<? extends Annotation>> checkedAnnotations) {
        A innerCandidate = null;
        for (Annotation annotation : annotations.annotations()) {
            A inner;
            if (!checkedAnnotations.add(annotation.annotationType())) continue;
            if (annotation.annotationType().equals(clazz)) {
                return (A)annotation;
            }
            if (annotation.annotationType().getPackage().getName().startsWith("java.lang") || (inner = AnnotationParser.getAnnotationRecursively(AnnotationAccessor.of(annotation.annotationType()), clazz, checkedAnnotations)) == null) continue;
            innerCandidate = inner;
        }
        return innerCandidate;
    }

    static <A extends Annotation> @Nullable A getMethodOrClassAnnotation(@NonNull Method method, @NonNull Class<A> clazz) {
        A annotation = AnnotationParser.getAnnotationRecursively(AnnotationAccessor.of(method), clazz, new HashSet<Class<? extends Annotation>>());
        if (annotation == null) {
            annotation = AnnotationParser.getAnnotationRecursively(AnnotationAccessor.of(method.getDeclaringClass()), clazz, new HashSet<Class<? extends Annotation>>());
        }
        return annotation;
    }

    static <A extends Annotation> boolean methodOrClassHasAnnotation(@NonNull Method method, @NonNull Class<A> clazz) {
        return AnnotationParser.getMethodOrClassAnnotation(method, clazz) != null;
    }

    public @NonNull CommandManager<C> manager() {
        return this.manager;
    }

    @API(status=API.Status.STABLE)
    public void registerCommandExecutionMethodFactory(@NonNull Predicate<@NonNull Method> predicate, @NonNull CommandMethodExecutionHandlerFactory<C> factory) {
        this.commandMethodFactories.put(predicate, factory);
    }

    public <A extends Annotation> void registerBuilderModifier(@NonNull Class<A> annotation, @NonNull BuilderModifier<A, C> builderModifier) {
        this.builderModifiers.put(annotation, builderModifier);
    }

    @API(status=API.Status.STABLE)
    public void registerBuilderDecorator(@NonNull BuilderDecorator<C> decorator) {
        this.builderDecorators.add(decorator);
    }

    public <A extends Annotation> void registerAnnotationMapper(@NonNull Class<A> annotation, @NonNull AnnotationMapper<A> mapper) {
        this.annotationMappers.put(annotation, mapper);
    }

    @API(status=API.Status.STABLE)
    public <A extends Annotation> void registerPreprocessorMapper(@NonNull Class<A> annotation, @NonNull PreprocessorMapper<A, C> preprocessorMapper) {
        this.preprocessorMappers.put(annotation, preprocessorMapper);
    }

    @API(status=API.Status.STABLE)
    public @NonNull Map<@NonNull Class<? extends Annotation>, @NonNull PreprocessorMapper<?, C>> preprocessorMappers() {
        return Collections.unmodifiableMap(this.preprocessorMappers);
    }

    public @NonNull StringProcessor stringProcessor() {
        return this.stringProcessor;
    }

    public void stringProcessor(@NonNull StringProcessor stringProcessor) {
        this.stringProcessor = stringProcessor;
    }

    public @NonNull String processString(@NonNull String input) {
        return this.stringProcessor().processString(input);
    }

    public @NonNull String[] processStrings(@NonNull String[] strings) {
        return (String[])Arrays.stream(strings).map(this::processString).toArray(String[]::new);
    }

    @API(status=API.Status.STABLE)
    public @NonNull List<@NonNull String> processStrings(@NonNull Collection<@NonNull String> strings) {
        return strings.stream().map(this::processString).collect(Collectors.toList());
    }

    public @NonNull SyntaxParser syntaxParser() {
        return this.syntaxParser;
    }

    @API(status=API.Status.STABLE)
    public void syntaxParser(@NonNull SyntaxParser syntaxParser) {
        this.syntaxParser = syntaxParser;
    }

    @API(status=API.Status.STABLE)
    public @NonNull ArgumentExtractor argumentExtractor() {
        return this.argumentExtractor;
    }

    @API(status=API.Status.STABLE)
    public void argumentExtractor(@NonNull ArgumentExtractor argumentExtractor) {
        this.argumentExtractor = argumentExtractor;
    }

    @API(status=API.Status.STABLE)
    public @NonNull ArgumentAssembler<C> argumentAssembler() {
        return this.argumentAssembler;
    }

    @API(status=API.Status.STABLE)
    public void argumentAssembler(@NonNull ArgumentAssembler<C> argumentAssembler) {
        this.argumentAssembler = argumentAssembler;
    }

    @API(status=API.Status.STABLE)
    public @NonNull FlagExtractor flagExtractor() {
        return this.flagExtractor;
    }

    @API(status=API.Status.STABLE)
    public void flagExtractor(@NonNull FlagExtractor flagExtractor) {
        this.flagExtractor = flagExtractor;
    }

    @API(status=API.Status.STABLE)
    public @NonNull FlagAssembler flagAssembler() {
        return this.flagAssembler;
    }

    @API(status=API.Status.STABLE)
    public void flagAssembler(@NonNull FlagAssembler flagAssembler) {
        this.flagAssembler = flagAssembler;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandExtractor commandExtractor() {
        return this.commandExtractor;
    }

    @API(status=API.Status.STABLE)
    public void commandExtractor(@NonNull CommandExtractor commandExtractor) {
        this.commandExtractor = commandExtractor;
    }

    @API(status=API.Status.STABLE)
    public @NonNull SuggestionProviderFactory<C> suggestionProviderFactory() {
        return this.suggestionProviderFactory;
    }

    @API(status=API.Status.STABLE)
    public void suggestionProviderFactory(@NonNull SuggestionProviderFactory<C> suggestionProviderFactory) {
        this.suggestionProviderFactory = suggestionProviderFactory;
    }

    @API(status=API.Status.EXPERIMENTAL)
    public @NonNull MethodArgumentParserFactory<C> methodArgumentParserFactory() {
        return this.methodArgumentParserFactory;
    }

    @API(status=API.Status.EXPERIMENTAL)
    public void methodArgumentParserFactory(@NonNull MethodArgumentParserFactory<C> methodArgumentParserFactory) {
        this.methodArgumentParserFactory = methodArgumentParserFactory;
    }

    @API(status=API.Status.STABLE)
    public @NonNull ExceptionHandlerFactory<C> exceptionHandlerFactory() {
        return this.exceptionHandlerFactory;
    }

    @API(status=API.Status.STABLE)
    public void exceptionHandlerFactory(@NonNull ExceptionHandlerFactory<C> exceptionHandlerFactory) {
        this.exceptionHandlerFactory = exceptionHandlerFactory;
    }

    @API(status=API.Status.STABLE)
    public @NonNull DescriptionMapper descriptionMapper() {
        return this.descriptionMapper;
    }

    @API(status=API.Status.STABLE)
    public void descriptionMapper(@NonNull DescriptionMapper descriptionMapper) {
        this.descriptionMapper = descriptionMapper;
    }

    public @NonNull DefaultValueRegistry<C> defaultValueRegistry() {
        return this.defaultValueRegistry;
    }

    public void defaultValueRegistry(@NonNull DefaultValueRegistry<C> defaultValueRegistry) {
        this.defaultValueRegistry = Objects.requireNonNull(defaultValueRegistry, "defaultValueRegistry");
    }

    public @NonNull Collection<@NonNull Command<C>> parseContainers() throws Exception {
        return this.parseContainers(this.getClass().getClassLoader());
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull Command<C>> parseContainers(@NonNull ClassLoader classLoader) throws Exception {
        List classNames;
        try (InputStream stream = classLoader.getResourceAsStream("META-INF/commands/org.incendo.cloud.annotations.processing.CommandContainer");){
            if (stream == null) {
                List<Command<C>> list = Collections.emptyList();
                return list;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));){
                classNames = reader.lines().distinct().collect(Collectors.toList());
            }
        }
        ArrayList classes = new ArrayList();
        for (String className : classNames) {
            classes.add(Class.forName(className, true, classLoader));
        }
        classes.sort(COMMAND_CONTAINER_COMPARATOR);
        LinkedList<Object> instances = new LinkedList<Object>();
        for (Class clazz : classes) {
            Object instance;
            try {
                instance = clazz.getConstructor(AnnotationParser.class).newInstance(this);
            }
            catch (NoSuchMethodException ignored) {
                try {
                    instance = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (NoSuchMethodException e) {
                    throw new IllegalStateException(String.format("Command container %s has no valid constructors", clazz), e);
                }
            }
            instances.add(instance);
        }
        return this.parse(instances);
    }

    public @NonNull Collection<@NonNull Command<C>> parse(Object ... instances) {
        return this.parse(Arrays.asList(instances));
    }

    public @NonNull Collection<@NonNull Command<C>> parse(@NonNull Collection<@NonNull Object> instances) {
        for (Object instance : instances) {
            this.parseDefaultValues(instance);
        }
        for (Object instance : instances) {
            this.parseSuggestions(instance);
        }
        for (Object instance : instances) {
            this.parseParsers(instance);
        }
        for (Object instance : instances) {
            this.parseExceptionHandlers(instance);
        }
        ArrayList<Command<C>> result = new ArrayList<Command<C>>();
        for (Object instance : instances) {
            Collection<CommandDescriptor> commandDescriptors = this.commandExtractor.extractCommands(instance);
            Collection<Command<C>> commands = this.construct(instance, commandDescriptors);
            for (Command<C> command : commands) {
                this.manager.command(command);
            }
            result.addAll(commands);
        }
        return Collections.unmodifiableList(result);
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.annotations.*"})
    public @NonNull Description mapDescription(@NonNull String string) {
        return this.descriptionMapper.map(this.processString(string));
    }

    private <T> void parseSuggestions(@NonNull T instance) {
        for (Method method : instance.getClass().getMethods()) {
            boolean valid;
            Suggestions suggestions = method.getAnnotation(Suggestions.class);
            if (suggestions == null) continue;
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            boolean bl = valid = Iterable.class.isAssignableFrom(method.getReturnType()) || method.getReturnType().equals(Stream.class) || method.getReturnType().equals(CompletableFuture.class) || method.getReturnType().getSimpleName().equals("Sequence") || method.getParameterCount() == 3;
            if (!valid) {
                throw new IllegalArgumentException(String.format("@Suggestions annotated method '%s' in class '%s' does not have the correct signature", method.getName(), instance.getClass().getCanonicalName()));
            }
            try {
                this.manager.parserRegistry().registerSuggestionProvider(this.processString(suggestions.value()), this.suggestionProviderFactory.createSuggestionProvider(instance, method, this.manager.parameterInjectorRegistry()));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <T> void parseExceptionHandlers(@NonNull T instance) {
        for (Method method : instance.getClass().getMethods()) {
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            if (exceptionHandler == null) continue;
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            try {
                this.manager.exceptionController().registerHandler(exceptionHandler.value(), this.exceptionHandlerFactory.createExceptionHandler(instance, method, this.manager.parameterInjectorRegistry()));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <T> void parseDefaultValues(@NonNull T instance) {
        for (Method method : instance.getClass().getMethods()) {
            Default defaultValue = method.getAnnotation(Default.class);
            if (defaultValue == null) continue;
            String name = defaultValue.name().isEmpty() ? method.getName() : defaultValue.name();
            this.defaultValueRegistry().register(name, new MethodDefaultValueFactory(method, instance));
        }
    }

    private <T> void parseParsers(@NonNull T instance) {
        for (Method method : instance.getClass().getMethods()) {
            Parser parser = method.getAnnotation(Parser.class);
            if (parser == null) continue;
            try {
                String suggestions = this.processString(parser.suggestions());
                SuggestionProvider suggestionProvider = suggestions.isEmpty() ? SuggestionProvider.noSuggestions() : this.manager.parserRegistry().getSuggestionProvider(suggestions).orElseThrow(() -> new NullPointerException(String.format("Cannot find the suggestion provider with name '%s'", suggestions)));
                ParserDescriptor<C, ?> parserDescriptor = this.methodArgumentParserFactory.createArgumentParser(suggestionProvider, instance, method, this.manager.parameterInjectorRegistry());
                String name = this.processString(parser.name());
                if (name.isEmpty()) {
                    this.manager.parserRegistry().registerParser(parserDescriptor);
                    continue;
                }
                this.manager.parserRegistry().registerNamedParser(name, parserDescriptor);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private @NonNull Collection<@NonNull Command<C>> construct(@NonNull Object instance, @NonNull Collection<@NonNull CommandDescriptor> commandDescriptors) {
        return commandDescriptors.stream().flatMap(descriptor -> this.constructCommands(instance, (CommandDescriptor)descriptor).stream()).collect(Collectors.toList());
    }

    private @NonNull Collection<@NonNull Command<C>> constructCommands(@NonNull Object instance, @NonNull CommandDescriptor commandDescriptor) {
        Permission permission;
        AnnotationAccessor classAnnotations = AnnotationAccessor.of(instance.getClass());
        ArrayList<Command<C>> commands = new ArrayList<Command<C>>();
        Method method = commandDescriptor.method();
        CommandManager<Object> manager = this.manager;
        CommandMetaBuilder metaBuilder = CommandMeta.builder().with(this.metaFactory.apply(method));
        Command.Builder<Object> builder = manager.commandBuilder(commandDescriptor.commandToken(), commandDescriptor.syntax().get(0).minor(), metaBuilder.build());
        for (BuilderDecorator<C> decorator : this.builderDecorators) {
            builder = decorator.decorate(builder);
        }
        Collection<ArgumentDescriptor> arguments = this.argumentExtractor.extractArguments(commandDescriptor.syntax(), method);
        Collection<FlagDescriptor> flagDescriptors = this.flagExtractor.extractFlags(method);
        Collection flags = flagDescriptors.stream().map(this.flagAssembler()::assembleFlag).collect(Collectors.toList());
        Map<String, CommandComponent<C>> commandComponents = this.constructComponents(arguments, commandDescriptor);
        boolean commandNameFound = false;
        for (SyntaxFragment syntaxFragment : commandDescriptor.syntax()) {
            if (!commandNameFound) {
                commandNameFound = true;
                continue;
            }
            if (syntaxFragment.argumentMode() == ArgumentMode.LITERAL) {
                builder = builder.literal(syntaxFragment.major(), syntaxFragment.minor().toArray(new String[0]));
                continue;
            }
            CommandComponent<C> component = commandComponents.get(syntaxFragment.major());
            if (component == null) {
                throw new IllegalArgumentException(String.format("Found no mapping for argument '%s' in method '%s'", syntaxFragment.major(), method.getName()));
            }
            builder = builder.argument(component);
        }
        Class<?> senderType = null;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Argument.class) || !GenericTypeReflector.isSuperType(this.commandSenderType.getType(), parameter.getType())) continue;
            senderType = parameter.getType();
            break;
        }
        if ((permission = AnnotationParser.getMethodOrClassAnnotation(method, Permission.class)) != null) {
            String[] permissions = permission.value();
            if (permissions.length == 1) {
                builder = builder.permission(this.processString(permissions[0]));
            } else if (permissions.length > 1) {
                builder = builder.permission(permission.mode().combine(Arrays.stream(permissions).map(this::processString).map(com.bergerkiller.bukkit.common.dep.cloud.permission.Permission::permission)));
            }
        }
        if (commandDescriptor.requiredSender() != Object.class) {
            builder = builder.senderType(commandDescriptor.requiredSender());
        } else if (senderType != null) {
            builder = builder.senderType(senderType);
        }
        try {
            MethodCommandExecutionHandler.CommandMethodContext<C> context = new MethodCommandExecutionHandler.CommandMethodContext<C>(instance, commandComponents, arguments, flagDescriptors, method, this);
            MethodCommandExecutionHandler<C> commandExecutionHandler = new MethodCommandExecutionHandler<C>(context);
            for (Map.Entry entry : this.commandMethodFactories.entrySet()) {
                if (!((Predicate)entry.getKey()).test(method)) continue;
                commandExecutionHandler = ((CommandMethodExecutionHandlerFactory)entry.getValue()).createExecutionHandler(context);
                break;
            }
            builder = builder.handler(commandExecutionHandler);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to construct command execution handler", e);
        }
        for (CommandFlag flag : flags) {
            builder = builder.flag(flag);
        }
        for (Annotation annotation : AnnotationAccessor.of(classAnnotations, AnnotationAccessor.of(method)).annotations()) {
            BuilderModifier<?, Object> builderModifier = this.builderModifiers.get(annotation.annotationType());
            if (builderModifier == null) continue;
            builder = builderModifier.modifyBuilder(annotation, builder);
        }
        Command<Object> builtCommand = builder.build();
        commands.add(builtCommand);
        if (method.isAnnotationPresent(ProxiedBy.class)) {
            manager.command(this.constructProxy(method.getAnnotation(ProxiedBy.class), builtCommand));
        }
        return commands;
    }

    private @NonNull Map<@NonNull String, @NonNull CommandComponent<C>> constructComponents(@NonNull Collection<@NonNull ArgumentDescriptor> arguments, @NonNull CommandDescriptor commandDescriptor) {
        return arguments.stream().map(argumentDescriptor -> this.argumentAssembler.assembleArgument(this.findSyntaxFragment(commandDescriptor.syntax(), this.processString(argumentDescriptor.name())), (ArgumentDescriptor)argumentDescriptor)).map(component -> Pair.of(component.name(), component)).collect(Collectors.toMap(Pair::first, Pair::second));
    }

    private @NonNull Command<C> constructProxy(@NonNull ProxiedBy proxyAnnotation, @NonNull Command<C> command) {
        String proxy = this.processString(proxyAnnotation.value());
        if (proxy.contains(" ")) {
            throw new IllegalArgumentException("@ProxiedBy proxies may only contain single literals");
        }
        return this.manager.commandBuilder(proxy, command.commandMeta(), new String[0]).proxies(command).build();
    }

    private @NonNull SyntaxFragment findSyntaxFragment(@NonNull List<@NonNull SyntaxFragment> fragments, @NonNull String argumentName) {
        return fragments.stream().filter(fragment -> fragment.argumentMode() != ArgumentMode.LITERAL).filter(fragment -> fragment.major().equals(argumentName)).findFirst().orElseThrow(() -> new IllegalArgumentException("Argument is not declared in syntax: " + argumentName));
    }

    @NonNull Map<Class<? extends @NonNull Annotation>, AnnotationMapper<?>> annotationMappers() {
        return this.annotationMappers;
    }
}

