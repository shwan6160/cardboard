/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class LiteralParser<C>
implements ArgumentParser<C, String>,
BlockingSuggestionProvider.Strings<C> {
    private final Set<String> allAcceptedAliases = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    private final Set<String> alternativeAliases = new HashSet<String>();
    private final String name;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String> literal(@NonNull String name, String ... aliases) {
        return ParserDescriptor.of(new LiteralParser<C>(name, aliases), String.class);
    }

    private LiteralParser(@NonNull String name, String ... aliases) {
        LiteralParser.validateNames(name, aliases);
        this.name = name;
        this.allAcceptedAliases.add(this.name);
        this.allAcceptedAliases.addAll(Arrays.asList(aliases));
        this.alternativeAliases.addAll(Arrays.asList(aliases));
    }

    @Override
    public @NonNull ArgumentParseResult<String> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String string = commandInput.peekString();
        if (this.allAcceptedAliases.contains(string)) {
            commandInput.readString();
            return ArgumentParseResult.success(this.name);
        }
        return ArgumentParseResult.failure(new IllegalArgumentException(string));
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return Collections.singletonList(this.name);
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull String> aliases() {
        return Collections.unmodifiableCollection(this.allAcceptedAliases);
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull String> alternativeAliases() {
        return Collections.unmodifiableCollection(this.alternativeAliases);
    }

    public void insertAlias(@NonNull String alias) {
        LiteralParser.validateNames("valid", new String[]{alias});
        this.allAcceptedAliases.add(alias);
        this.alternativeAliases.add(alias);
    }

    private static void validateNames(String name, @NonNull String[] aliases) {
        @Nullable List<String> errors = null;
        errors = LiteralParser.validateName(name, false, errors);
        for (String alias : aliases) {
            errors = LiteralParser.validateName(alias, true, errors);
        }
        if (errors != null && !errors.isEmpty()) {
            throw new IllegalArgumentException(String.join((CharSequence)"\n", errors));
        }
    }

    private static @Nullable List<String> validateName(@NonNull String name, boolean alias, @Nullable List<String> errors) {
        int found = name.codePoints().filter(Character::isWhitespace).findFirst().orElse(Integer.MIN_VALUE);
        if (found != Integer.MIN_VALUE) {
            if (errors == null) {
                errors = new ArrayList<String>();
            }
            errors.add(String.format("%s '%s' is invalid: contains whitespace", alias ? "Alias" : "Name", name));
        }
        return errors;
    }
}

