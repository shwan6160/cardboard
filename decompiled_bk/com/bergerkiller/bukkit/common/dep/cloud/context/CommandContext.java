/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.context;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.context.ParsingContext;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.key.MutableCloudKeyContainer;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.FlagContext;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class CommandContext<C>
implements MutableCloudKeyContainer {
    private final List<ParsingContext<C>> parsingContexts = new LinkedList<ParsingContext<C>>();
    private final FlagContext flagContext = FlagContext.create();
    private final Map<CloudKey<?>, Object> internalStorage = new HashMap();
    private final C commandSender;
    private final boolean suggestions;
    private final CaptionRegistry<C> captionRegistry;
    private final CommandManager<C> commandManager;
    private volatile @MonotonicNonNull Command<C> currentCommand = null;

    @API(status=API.Status.STABLE)
    public CommandContext(@NonNull C commandSender, @NonNull CommandManager<C> commandManager) {
        this(false, commandSender, commandManager);
    }

    @API(status=API.Status.STABLE)
    public CommandContext(boolean suggestions, @NonNull C commandSender, @NonNull CommandManager<C> commandManager) {
        this.commandSender = commandSender;
        this.suggestions = suggestions;
        this.commandManager = commandManager;
        this.captionRegistry = commandManager.captionRegistry();
    }

    public @NonNull String formatCaption(@NonNull Caption caption, CaptionVariable ... variables) {
        return this.formatCaption(this.commandManager.captionFormatter(), caption, variables);
    }

    public @NonNull String formatCaption(@NonNull Caption caption, @NonNull List<@NonNull CaptionVariable> variables) {
        return this.formatCaption(this.commandManager.captionFormatter(), caption, variables);
    }

    public <T> @NonNull T formatCaption(@NonNull CaptionFormatter<C, T> formatter, @NonNull Caption caption, CaptionVariable ... variables) {
        return formatter.formatCaption(caption, this.commandSender, this.captionRegistry.caption(caption, this.commandSender), variables);
    }

    public <T> @NonNull T formatCaption(@NonNull CaptionFormatter<C, T> formatter, @NonNull Caption caption, @NonNull List<@NonNull CaptionVariable> variables) {
        return formatter.formatCaption(caption, this.commandSender, this.captionRegistry.caption(caption, this.commandSender), variables);
    }

    @API(status=API.Status.STABLE)
    public @NonNull C sender() {
        return this.commandSender;
    }

    @API(status=API.Status.STABLE)
    public boolean hasPermission(@NonNull Permission permission) {
        return this.commandManager.testPermission(this.commandSender, permission).allowed();
    }

    @API(status=API.Status.STABLE)
    public boolean hasPermission(@NonNull String permission) {
        return this.commandManager.hasPermission(this.commandSender, permission);
    }

    public boolean isSuggestions() {
        return this.suggestions;
    }

    public <T> void store(@NonNull String key, T value) {
        this.internalStorage.put(CloudKey.of(key), value);
    }

    public <T> void store(@NonNull CloudKey<T> key, T value) {
        this.internalStorage.put(key, value);
    }

    @Override
    public boolean contains(@NonNull CloudKey<?> key) {
        return this.internalStorage.containsKey(key);
    }

    public <T> @NonNull Optional<T> optional(@NonNull CloudKey<T> key) {
        Object value = this.internalStorage.get(key);
        if (value != null) {
            Object castedValue = value;
            return Optional.of(castedValue);
        }
        return Optional.empty();
    }

    public <T> @NonNull Optional<T> optional(@NonNull String key) {
        Object value = this.internalStorage.get(CloudKey.of(key));
        if (value != null) {
            Object castedValue = value;
            return Optional.of(castedValue);
        }
        return Optional.empty();
    }

    @Override
    public void remove(@NonNull CloudKey<?> key) {
        this.internalStorage.remove(key);
    }

    public <T> T computeIfAbsent(@NonNull CloudKey<T> key, @NonNull Function<CloudKey<T>, T> defaultFunction) {
        Object castedValue = this.internalStorage.computeIfAbsent(key, (? super K k) -> defaultFunction.apply((CloudKey)k));
        return (T)castedValue;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandInput rawInput() {
        return this.getOrDefault("__raw_input__", CommandInput.empty()).copy();
    }

    @API(status=API.Status.MAINTAINED)
    public @NonNull ParsingContext<C> createParsingContext(@NonNull CommandComponent<C> component) {
        ParsingContext<C> parsingContext = new ParsingContext<C>(component);
        this.parsingContexts.add(parsingContext);
        return parsingContext;
    }

    @API(status=API.Status.MAINTAINED)
    public @NonNull ParsingContext<C> parsingContext(@NonNull CommandComponent<C> component) {
        return this.parsingContexts.stream().filter(context -> context.component().equals(component)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    @API(status=API.Status.MAINTAINED)
    public @NonNull ParsingContext<C> parsingContext(int position) {
        return this.parsingContexts.get(position);
    }

    @API(status=API.Status.MAINTAINED)
    public @NonNull ParsingContext<C> parsingContext(String name) {
        return this.parsingContexts.stream().filter(context -> context.component().name().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    @API(status=API.Status.MAINTAINED)
    public @NonNull List<@NonNull ParsingContext<@NonNull C>> parsingContexts() {
        return Collections.unmodifiableList(this.parsingContexts);
    }

    public @NonNull FlagContext flags() {
        return this.flagContext;
    }

    public @NonNull Command<C> command() {
        if (this.currentCommand == null) {
            throw new IllegalStateException("The current command is only available once a command has been parsed. Mainly from execution handlers and post processors.");
        }
        return this.currentCommand;
    }

    @API(status=API.Status.INTERNAL)
    public void command(@NonNull Command<C> command) {
        this.currentCommand = Objects.requireNonNull(command, "command");
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Optional<T> inject(@NonNull Class<T> clazz) {
        if (this.commandManager == null) {
            throw new UnsupportedOperationException("Cannot retrieve injectable values from a command context that is not associated with a command manager");
        }
        return this.commandManager.parameterInjectorRegistry().getInjectable(clazz, this, AnnotationAccessor.empty());
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Optional<T> inject(@NonNull TypeToken<T> type) {
        if (this.commandManager == null) {
            throw new UnsupportedOperationException("Cannot retrieve injectable values from a command context that is not associated with a command manager");
        }
        return this.commandManager.parameterInjectorRegistry().getInjectable(type, this, AnnotationAccessor.empty());
    }

    @Override
    public final @NonNull Map<CloudKey<?>, ? extends @NonNull Object> all() {
        return Collections.unmodifiableMap(this.internalStorage);
    }
}

