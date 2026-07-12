/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.cloud;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.cloud.ThrowingBiConsumer;
import com.bergerkiller.bukkit.common.cloud.captions.BKCommonLibCaptionRegistry;
import com.bergerkiller.bukkit.common.cloud.parsers.SoundEffectParser;
import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.PreprocessorMapper;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.ComponentPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandExecutionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InjectionException;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjector;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.ImmutableMinecraftHelp;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.MinecraftHelp;
import com.bergerkiller.bukkit.common.dep.cloud.paper.LegacyPaperCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameter;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.StringParser;
import com.bergerkiller.bukkit.common.dep.cloud.services.PipelineException;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitAudiences;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.localization.LocalizationEnum;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CloudSimpleHandler {
    private BukkitCommandManager<CommandSender> manager;
    private AnnotationParser<CommandSender> annotationParser;
    private BukkitAudiences bukkitAudiences;
    private final Set<Class<?>> exceptionTypes = new HashSet();

    public boolean isEnabled() {
        return this.manager != null;
    }

    public void enable(Plugin plugin) {
        boolean brigDisabled;
        try {
            this.manager = new LegacyPaperCommandManager<CommandSender>(plugin, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity());
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to initialize the command manager", e);
        }
        this.manager.captionRegistry().registerProvider(BKCommonLibCaptionRegistry.provider());
        this.manager.parserRegistry().registerParserSupplier(new TypeToken<ResourceKey<SoundEffect>>(){}, parserParameters -> new SoundEffectParser());
        boolean bl = brigDisabled = CommonPlugin.hasInstance() && CommonPlugin.getInstance().isCloudBrigadierDisabled();
        if (!brigDisabled && this.manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            boolean isStable;
            if (Common.evaluateMCVersion(">=", "1.19.1")) {
                try {
                    Class<?> eventClass = Class.forName("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");
                    eventClass.getMethod("isRawCommand", new Class[0]);
                    isStable = true;
                }
                catch (Throwable t) {
                    isStable = false;
                }
            } else {
                isStable = true;
            }
            if (isStable) {
                try {
                    this.manager.registerBrigadier();
                    CloudBrigadierManager<CommandSender, ?> brig = this.manager.brigadierManager();
                    brig.setNativeNumberSuggestions(false);
                    try {
                        Method registerMethod = Class.forName("com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParserProxy").getMethod("registerBrigadier", CloudBrigadierManager.class);
                        registerMethod.setAccessible(true);
                        try {
                            registerMethod.invoke(null, brig);
                        }
                        catch (InvocationTargetException ex) {
                            throw ex.getCause();
                        }
                    }
                    catch (Throwable t) {
                        plugin.getLogger().log(Level.WARNING, "Failed to register the quoted argument parser with brigadier", t);
                    }
                    if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                        try {
                            SoundEffectParser.registerBrigadier(brig);
                        }
                        catch (Throwable t) {
                            plugin.getLogger().log(Level.WARNING, "Failed to register sound effect auto-completion for brigadier", t);
                        }
                    }
                }
                catch (BukkitCommandManager.BrigadierInitializationException ex) {
                    plugin.getLogger().log(Level.WARNING, "Failed to register commands using brigadier, using fallback instead. Error:", ex);
                }
            }
        }
        this.annotationParser = new AnnotationParser<CommandSender>(this.manager, CommandSender.class);
        this.suggest("argname", (CommandContext<CommandSender> context, CommandInput b) -> Collections.singletonList("<argument>"));
        this.handle(CommandExecutionException.class, this::handleException);
        this.handle(PipelineException.class, this::handleException);
        this.handle(InjectionException.class, this::handleException);
        this.handle(CloudLocalizedException.class, (sender, exception) -> sender.sendMessage(exception.getMessage()));
        this.suggest("playername", (CommandContext<CommandSender> context, CommandInput input) -> {
            String prefix = input.readString();
            List names = Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(p -> p.startsWith(prefix)).collect(Collectors.toList());
            if (!names.isEmpty()) {
                return names;
            }
            return Stream.of(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).filter(Objects::nonNull).filter(p -> p.startsWith(prefix)).collect(Collectors.toList());
        });
        this.inject(plugin.getClass(), plugin);
        this.bukkitAudiences = BukkitAudiences.create(plugin);
    }

    private void handleException(CommandSender sender, Throwable exception) throws Throwable {
        Throwable cause = exception.getCause();
        if (this.exceptionTypes.contains(cause.getClass())) {
            throw cause;
        }
        throw exception;
    }

    public AnnotationParser<CommandSender> getParser() {
        return this.annotationParser;
    }

    public CommandManager<CommandSender> getManager() {
        return this.manager;
    }

    public void postProcess(CommandPostprocessor<CommandSender> processor) {
        this.manager.registerCommandPostProcessor(processor);
    }

    public <T> void parse(ParserDescriptor<CommandSender, T> descriptor) {
        this.manager.parserRegistry().registerParser(descriptor);
    }

    public <T> void parse(Class<T> type, Function<ParserParameters, ArgumentParser<CommandSender, ?>> supplier) {
        this.manager.parserRegistry().registerParserSupplier(TypeToken.get(type), supplier);
    }

    public <T> void parse(TypeToken<T> type, Function<ParserParameters, ArgumentParser<CommandSender, ?>> supplier) {
        this.manager.parserRegistry().registerParserSupplier(type, supplier);
    }

    public void parse(String name, Function<ParserParameters, ArgumentParser<CommandSender, ?>> supplier) {
        this.manager.parserRegistry().registerNamedParserSupplier(name, supplier);
    }

    public void suggest(String name, List<String> suggestions) {
        this.suggest(name, (CommandContext<CommandSender> sender, CommandInput arg) -> suggestions);
    }

    public void suggest(String name, Supplier<List<String>> suggestionsProvider) {
        this.suggest(name, (CommandContext<CommandSender> sender, CommandInput arg) -> (Iterable)suggestionsProvider.get());
    }

    public void suggest(String name, BlockingSuggestionProvider.Strings<CommandSender> suggestionsProvider) {
        this.manager.parserRegistry().registerSuggestionProvider(name, suggestionsProvider);
    }

    public <T> void inject(Class<T> clazz, T value) {
        this.injector(clazz, (context, annotations) -> value);
    }

    public <T> void injector(Class<T> clazz, ParameterInjector<CommandSender, T> injector) {
        this.manager.parameterInjectorRegistry().registerInjector(clazz, injector);
    }

    public <A extends Annotation, T> void annotationParameter(Class<A> annotation, ParserParameter<T> parameter, T value) {
        this.manager.parserRegistry().registerAnnotationMapper(annotation, (a, typeToken) -> ParserParameters.single(parameter, value));
    }

    public <A extends Annotation, T> void annotationParameter(Class<A> annotation, ParserParameter<T> parameter, Function<A, T> valueMapper) {
        this.manager.parserRegistry().registerAnnotationMapper(annotation, (a, typeToken) -> ParserParameters.single(parameter, valueMapper.apply(a)));
    }

    public <A extends Annotation> void preprocessAnnotation(Class<A> annotation, PreprocessorMapper<A, CommandSender> preprocessorMapper) {
        this.annotationParser.registerPreprocessorMapper(annotation, preprocessorMapper);
    }

    public <A extends Annotation> void preprocessAnnotation(Class<A> annotation, ComponentPreprocessor<CommandSender> preprocessorMapper) {
        this.preprocessAnnotation(annotation, (A a) -> preprocessorMapper);
    }

    public <T extends Throwable> void handle(Class<T> exceptionType, ThrowingBiConsumer<CommandSender, T> handler) {
        this.exceptionTypes.add(exceptionType);
        this.manager.exceptionController().registerHandler(exceptionType, ctx -> handler.accept((CommandSender)ctx.context().sender(), ctx.exception()));
    }

    public <T extends Throwable> void handleMessage(Class<T> exceptionType, String message) {
        Caption caption = Caption.of(message);
        this.handle(exceptionType, (sender, exception) -> {
            String translated = this.manager.captionRegistry().caption(caption, (CommandSender)sender);
            sender.sendMessage(translated);
        });
    }

    public <T> void annotations(T annotationsClassInstance) {
        this.annotationParser.parse(annotationsClassInstance);
    }

    public void captionFromLocalization(Class<? extends LocalizationEnum> localizationDefaults) {
        for (LocalizationEnum locale : CommonUtil.getClassConstants(localizationDefaults)) {
            this.caption(locale.getName(), locale.get(new String[0]).replace("%0%", "{input}"));
        }
    }

    public void caption(String regex, Function<CommandSender, String> messageFactory) {
        Caption caption = Caption.of(regex);
        CaptionProvider<CommandSender> provider = CaptionProvider.forCaption(caption, messageFactory);
        this.manager.captionRegistry().registerProvider(provider);
    }

    public void caption(String regex, String value) {
        this.caption(regex, (CommandSender sender) -> value);
    }

    public Command<CommandSender> helpCommand(List<String> filterPrefix, String helpDescription) {
        return this.helpCommand(filterPrefix, helpDescription, builder -> builder);
    }

    public Command<CommandSender> helpCommand(List<String> filterPrefix, String helpDescription, Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> modifier) {
        String helpCmd = "/" + String.join((CharSequence)" ", filterPrefix) + " help";
        MinecraftHelp<CommandSender> help = this.help(helpCmd, filterPrefix);
        Command.Builder<Object> command = Command.newBuilder(filterPrefix.get(0), CommandMeta.empty(), new String[0]);
        command = command.commandDescription(Description.of(helpDescription));
        for (int i = 1; i < filterPrefix.size(); ++i) {
            command = command.literal(filterPrefix.get(i), new String[0]);
        }
        command = command.literal("help", new String[0]);
        command = command.optional("query", StringParser.greedyStringParser());
        command = command.handler(context -> {
            String query = context.getOrDefault("query", "");
            help.queryCommands(query, (CommandSender)context.sender());
        });
        command = modifier.apply(command);
        Command<Object> builtCommand = command.build();
        this.manager.command(builtCommand);
        return builtCommand;
    }

    public MinecraftHelp<CommandSender> help(String commandPrefix, List<String> filterPrefix) {
        ImmutableMinecraftHelp<CommandSender> help = MinecraftHelp.builder().commandManager(this.manager).audienceProvider(this.bukkitAudiences::sender).commandPrefix(commandPrefix).commandFilter(command -> {
            List args = command.components();
            if (args.size() < filterPrefix.size()) {
                return false;
            }
            for (int i = 0; i < filterPrefix.size(); ++i) {
                if (args.get(i).name().equals(filterPrefix.get(i))) continue;
                return false;
            }
            return true;
        }).build();
        return help;
    }
}

