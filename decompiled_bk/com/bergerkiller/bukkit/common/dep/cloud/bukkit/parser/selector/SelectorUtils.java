/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  org.bukkit.Bukkit
 *  org.bukkit.NamespacedKey
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser.WrappedBrigadierParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandContextKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.MinecraftArgumentTypes;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SelectorUnsupportedException;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class SelectorUtils {
    private SelectorUtils() {
    }

    private static <C, T> @Nullable ArgumentParser<C, T> createModernParser(boolean single, boolean playersOnly, SelectorMapper<T> mapper) {
        if (CraftBukkitReflection.MAJOR_REVISION < 13) {
            return null;
        }
        WrappedBrigadierParser wrappedBrigParser = new WrappedBrigadierParser(() -> SelectorUtils.createEntityArgument(single, playersOnly), EntityArgumentParseFunction.INSTANCE);
        return new ModernSelectorParser(wrappedBrigParser, mapper);
    }

    private static ArgumentType<Object> createEntityArgument(boolean single, boolean playersOnly) {
        Constructor<?> constructor = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"entity")).getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            return (ArgumentType)constructor.newInstance(single, playersOnly);
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <X extends Throwable> RuntimeException rethrow(Throwable t) throws X {
        throw t;
    }

    @FunctionalInterface
    static interface SelectorMapper<T> {
        public T mapResult(String var1, EntitySelectorWrapper var2) throws Exception;
    }

    private static final class EntityArgumentParseFunction
    implements WrappedBrigadierParser.ParseFunction<Object> {
        static final EntityArgumentParseFunction INSTANCE = new EntityArgumentParseFunction();

        private EntityArgumentParseFunction() {
        }

        @Override
        public Object apply(ArgumentType<Object> type, StringReader reader) throws CommandSyntaxException {
            @Nullable Method specialParse = CraftBukkitReflection.findMethod(type.getClass(), "parse", StringReader.class, Boolean.TYPE);
            if (specialParse == null) {
                return type.parse(reader);
            }
            try {
                return specialParse.invoke(type, reader, true);
            }
            catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof CommandSyntaxException) {
                    throw (CommandSyntaxException)cause;
                }
                throw new RuntimeException(ex);
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class ModernSelectorParser<C, T>
    implements ArgumentParser.FutureArgumentParser<C, T>,
    SuggestionProvider<C> {
        private final WrappedBrigadierParser<C, Object> wrappedBrigadierParser;
        private final SelectorMapper<T> mapper;

        ModernSelectorParser(WrappedBrigadierParser<C, Object> wrapperBrigParser, SelectorMapper<T> mapper) {
            this.wrappedBrigadierParser = wrapperBrigParser;
            this.mapper = mapper;
        }

        @Override
        public CompletableFuture<ArgumentParseResult<T>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
            return CompletableFuture.supplyAsync(() -> {
                CommandInput originalCommandInput = commandInput.copy();
                ArgumentParseResult<Object> result = this.wrappedBrigadierParser.parse(commandContext, commandInput);
                if (result.failure().isPresent()) {
                    return result;
                }
                String input = originalCommandInput.difference(commandInput);
                try {
                    return ArgumentParseResult.success(this.mapper.mapResult(input, new EntitySelectorWrapper(commandContext, result.parsedValue().get())));
                }
                catch (CommandSyntaxException ex) {
                    return ArgumentParseResult.failure(ex);
                }
                catch (Exception ex) {
                    throw SelectorUtils.rethrow(ex);
                }
            }, commandContext.get(BukkitCommandContextKeys.SENDER_SCHEDULER_EXECUTOR));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
            CompletableFuture<Iterable<Suggestion>> completableFuture;
            block7: {
                Object commandSourceStack = commandContext.get("_cloud_brigadier_native_sender");
                @Nullable Field bypassField = CraftBukkitReflection.findField(commandSourceStack.getClass(), "bypassSelectorPermissions");
                boolean prev = false;
                try {
                    if (bypassField != null) {
                        prev = bypassField.getBoolean(commandSourceStack);
                        bypassField.setBoolean(commandSourceStack, true);
                    }
                    completableFuture = CompletableFuture.completedFuture(this.wrappedBrigadierParser.suggestionProvider().suggestionsFuture(commandContext, input).join());
                    if (bypassField == null) break block7;
                }
                catch (Throwable throwable) {
                    try {
                        if (bypassField != null) {
                            bypassField.setBoolean(commandSourceStack, prev);
                        }
                        throw throwable;
                    }
                    catch (ReflectiveOperationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                bypassField.setBoolean(commandSourceStack, prev);
            }
            return completableFuture;
        }
    }

    static final class EntitySelectorWrapper {
        private static volatile @MonotonicNonNull Methods methods;
        private final CommandContext<?> commandContext;
        private final Object selector;

        EntitySelectorWrapper(CommandContext<?> commandContext, Object selector) {
            this.commandContext = commandContext;
            this.selector = selector;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private static Methods methods(CommandContext<?> commandContext, Object selector) {
            if (methods != null) return methods;
            Class<Methods> clazz = Methods.class;
            synchronized (Methods.class) {
                if (methods != null) return methods;
                methods = new Methods(commandContext, selector);
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return methods;
            }
        }

        private Methods methods() {
            return EntitySelectorWrapper.methods(this.commandContext, this.selector);
        }

        Entity singleEntity() {
            return EntitySelectorWrapper.reflectiveOperation(() -> (Entity)this.methods().getBukkitEntity.invoke(this.methods().entity.invoke(this.selector, this.commandContext.get("_cloud_brigadier_native_sender")), new Object[0]));
        }

        Player singlePlayer() {
            return EntitySelectorWrapper.reflectiveOperation(() -> (Player)this.methods().getBukkitEntity.invoke(this.methods().player.invoke(this.selector, this.commandContext.get("_cloud_brigadier_native_sender")), new Object[0]));
        }

        List<Entity> entities() {
            List internalEntities = EntitySelectorWrapper.reflectiveOperation(() -> (List)this.methods().entities.invoke(this.selector, this.commandContext.get("_cloud_brigadier_native_sender")));
            return internalEntities.stream().map(o -> EntitySelectorWrapper.reflectiveOperation(() -> (Entity)this.methods().getBukkitEntity.invoke(o, new Object[0]))).collect(Collectors.toList());
        }

        List<Player> players() {
            List serverPlayers = EntitySelectorWrapper.reflectiveOperation(() -> (List)this.methods().players.invoke(this.selector, this.commandContext.get("_cloud_brigadier_native_sender")));
            return serverPlayers.stream().map(o -> EntitySelectorWrapper.reflectiveOperation(() -> (Player)this.methods().getBukkitEntity.invoke(o, new Object[0]))).collect(Collectors.toList());
        }

        private static <T> T reflectiveOperation(ReflectiveOperation<T> op) {
            try {
                return op.run();
            }
            catch (InvocationTargetException ex) {
                if (ex.getCause() instanceof CommandSyntaxException) {
                    throw SelectorUtils.rethrow(ex.getCause());
                }
                throw new RuntimeException(ex);
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }

        private static final class Methods {
            private @MonotonicNonNull Method getBukkitEntity;
            private @MonotonicNonNull Method entity;
            private @MonotonicNonNull Method player;
            private @MonotonicNonNull Method entities;
            private @MonotonicNonNull Method players;

            Methods(CommandContext<?> commandContext, Object selector) {
                Object nativeSender = commandContext.get("_cloud_brigadier_native_sender");
                Class<?> nativeSenderClass = nativeSender.getClass();
                for (Method method : selector.getClass().getDeclaredMethods()) {
                    Method getBukkitEntity;
                    if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].equals(nativeSenderClass) || !Modifier.isPublic(method.getModifiers())) continue;
                    Class<?> returnType = method.getReturnType();
                    if (List.class.isAssignableFrom(returnType)) {
                        ParameterizedType stringListType = (ParameterizedType)method.getGenericReturnType();
                        Type listType = stringListType.getActualTypeArguments()[0];
                        while (listType instanceof WildcardType) {
                            listType = ((WildcardType)listType).getUpperBounds()[0];
                        }
                        Class<?> clazz = listType instanceof Class ? (Class<?>)listType : GenericTypeReflector.erase(listType);
                        @Nullable Method getBukkitEntity2 = Methods.findGetBukkitEntityMethod(clazz);
                        if (getBukkitEntity2 == null) continue;
                        Class<?> bukkitType = getBukkitEntity2.getReturnType();
                        if (Player.class.isAssignableFrom(bukkitType)) {
                            if (this.players != null) {
                                throw new IllegalStateException();
                            }
                            this.players = method;
                            continue;
                        }
                        if (this.entities != null) {
                            throw new IllegalStateException();
                        }
                        this.entities = method;
                        continue;
                    }
                    if (returnType == Void.TYPE || (getBukkitEntity = Methods.findGetBukkitEntityMethod(returnType)) == null) continue;
                    Class<?> bukkitType = getBukkitEntity.getReturnType();
                    if (Player.class.isAssignableFrom(bukkitType)) {
                        if (this.player != null) {
                            throw new IllegalStateException();
                        }
                        this.player = method;
                        continue;
                    }
                    if (this.entity != null || this.getBukkitEntity != null) {
                        throw new IllegalStateException();
                    }
                    this.entity = method;
                    this.getBukkitEntity = getBukkitEntity;
                }
                Objects.requireNonNull(this.getBukkitEntity, "Failed to locate getBukkitEntity method");
                Objects.requireNonNull(this.player, "Failed to locate findPlayer method");
                Objects.requireNonNull(this.entity, "Failed to locate findEntity method");
                Objects.requireNonNull(this.players, "Failed to locate findPlayers method");
                Objects.requireNonNull(this.entities, "Failed to locate findEntities method");
            }

            private static @Nullable Method findGetBukkitEntityMethod(Class<?> returnType) {
                Method getBukkitEntity;
                try {
                    getBukkitEntity = returnType.getDeclaredMethod("getBukkitEntity", new Class[0]);
                }
                catch (ReflectiveOperationException ex) {
                    try {
                        getBukkitEntity = returnType.getMethod("getBukkitEntity", new Class[0]);
                    }
                    catch (ReflectiveOperationException ex0) {
                        getBukkitEntity = null;
                    }
                }
                return getBukkitEntity;
            }
        }

        @FunctionalInterface
        static interface ReflectiveOperation<T> {
            public T run() throws ReflectiveOperationException;
        }
    }

    static abstract class PlayerSelectorParser<C, T>
    extends SelectorParser<C, T> {
        protected PlayerSelectorParser(boolean single) {
            super(single, true);
        }

        @Override
        protected @NonNull Iterable<@NonNull Suggestion> legacySuggestions(CommandContext<C> commandContext, CommandInput input) {
            ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                CommandSender bukkit = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
                if (bukkit instanceof Player && !((Player)bukkit).canSee(player)) continue;
                suggestions.add(Suggestion.suggestion(player.getName()));
            }
            return suggestions;
        }
    }

    static abstract class EntitySelectorParser<C, T>
    extends SelectorParser<C, T> {
        protected EntitySelectorParser(boolean single) {
            super(single, false);
        }
    }

    private static abstract class SelectorParser<C, T>
    implements ArgumentParser.FutureArgumentParser<C, T>,
    SelectorMapper<T>,
    SuggestionProvider<C> {
        static final Supplier<Object> NO_PLAYERS_EXCEPTION_TYPE = Suppliers.memoize(() -> SelectorParser.findExceptionType("argument.entity.notfound.player"));
        static final Supplier<Object> NO_ENTITIES_EXCEPTION_TYPE = Suppliers.memoize(() -> SelectorParser.findExceptionType("argument.entity.notfound.entity"));
        private final @Nullable ArgumentParser<C, T> modernParser;

        SelectorParser(boolean single, boolean playersOnly) {
            this.modernParser = SelectorUtils.createModernParser(single, playersOnly, this);
        }

        CompletableFuture<ArgumentParseResult<T>> legacyParse(CommandContext<C> commandContext, CommandInput commandInput) {
            return ArgumentParseResult.failureFuture(new SelectorUnsupportedException(commandContext, this.getClass()));
        }

        @NonNull Iterable<@NonNull Suggestion> legacySuggestions(CommandContext<C> commandContext, CommandInput input) {
            return Collections.emptyList();
        }

        @Override
        public CompletableFuture<ArgumentParseResult<T>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
            if (this.modernParser != null) {
                return this.modernParser.parseFuture(commandContext, commandInput);
            }
            return this.legacyParse(commandContext, commandInput);
        }

        @Override
        public CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
            if (this.modernParser != null) {
                return this.modernParser.suggestionProvider().suggestionsFuture(commandContext, input);
            }
            return CompletableFuture.completedFuture(this.legacySuggestions(commandContext, input));
        }

        private static Object findExceptionType(String type) {
            Field[] fields = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"entity")).getDeclaredFields();
            return Arrays.stream(fields).filter(field -> Modifier.isStatic(field.getModifiers()) && field.getType() == SimpleCommandExceptionType.class).map(field -> {
                try {
                    @Nullable Object fieldValue = field.get(null);
                    if (fieldValue == null) {
                        return null;
                    }
                    Field messageField = SimpleCommandExceptionType.class.getDeclaredField("message");
                    messageField.setAccessible(true);
                    if (messageField.get(fieldValue).toString().contains(type)) {
                        return fieldValue;
                    }
                }
                catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            }).filter(Objects::nonNull).findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find exception type '" + type + "'"));
        }

        static final class Thrower {
            private final Object type;

            Thrower(Object simpleCommandExceptionType) {
                this.type = simpleCommandExceptionType;
            }

            void throwIt() {
                throw SelectorUtils.rethrow(((SimpleCommandExceptionType)this.type).create());
            }
        }
    }
}

