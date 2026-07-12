/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.registry.RegistryAccess
 *  io.papermc.paper.registry.RegistryKey
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Keyed
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.parser;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.NamespacedKeyParser;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.paper.parser.RegistryEntryImpl;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.MappedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeFactory;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.EXPERIMENTAL)
public final class RegistryEntryParser<C, E extends Keyed>
implements ArgumentParser<C, RegistryEntry<E>>,
SuggestionProvider<C>,
MappedArgumentParser<C, NamespacedKey, RegistryEntry<E>> {
    private final ParserDescriptor<C, NamespacedKey> keyParser = NamespacedKeyParser.namespacedKeyParser();
    private final RegistryKey<E> registryKey;

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C, E extends Keyed> @NonNull ParserDescriptor<C, RegistryEntry<E>> registryEntryParser(RegistryKey<E> registryKey, TypeToken<E> elementType) {
        return ParserDescriptor.of(new RegistryEntryParser<C, E>(registryKey), TypeToken.get(TypeFactory.parameterizedClass(RegistryEntry.class, elementType.getType())));
    }

    public RegistryEntryParser(RegistryKey<E> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public @NonNull ArgumentParseResult<RegistryEntry<@NonNull E>> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        return this.keyParser.parser().parse(commandContext, commandInput).flatMapSuccess((T key) -> {
            Registry registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
            Keyed value = registry.get(key);
            if (value == null) {
                return ArgumentParseResult.failure(new ParseException(key.asString(), this.registryKey, commandContext));
            }
            return ArgumentParseResult.success(RegistryEntryImpl.of(value, key));
        });
    }

    @Override
    public @NonNull ArgumentParser<C, NamespacedKey> baseParser() {
        return this.keyParser.parser();
    }

    @Override
    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        ArrayList completions = new ArrayList();
        Registry registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
        registry.stream().map(arg_0 -> ((Registry)registry).getKeyOrThrow(arg_0)).forEach(key -> {
            if (input.hasRemainingInput() && key.getNamespace().equals("minecraft")) {
                completions.add(Suggestion.suggestion(key.getKey()));
            }
            completions.add(Suggestion.suggestion(key.getNamespace() + ':' + key.getKey()));
        });
        return CompletableFuture.completedFuture(completions);
    }

    @Value.Immutable
    public static interface RegistryEntry<E> {
        public E value();

        public NamespacedKey key();
    }

    public static final class ParseException
    extends ParserException {
        private final String input;
        private final RegistryKey<Object> registryKey;

        public ParseException(@NonNull String input, @NonNull RegistryKey<Object> registryKey, @NonNull CommandContext<?> context) {
            super(RegistryEntryParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_REGISTRY_ENTRY_MISSING, CaptionVariable.of("input", input), CaptionVariable.of("registry", registryKey.key().asString()));
            this.input = input;
            this.registryKey = registryKey;
        }

        public @NonNull String input() {
            return this.input;
        }

        public @NonNull RegistryKey<Object> registryKey() {
            return this.registryKey;
        }
    }
}

