/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.cloud.parsers;

import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.cloud.parsers.UnquotedCharacterFilter;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMapping;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMappingBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.StringParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;

final class QuotedArgumentParserProxy<C, T>
implements ArgumentParser<C, T>,
SuggestionProvider<C> {
    private final ArgumentParser<C, String> baseParser = new StringParser(StringParser.StringMode.QUOTED);
    private final QuotedArgumentParser<C, T> parser;

    public QuotedArgumentParserProxy(QuotedArgumentParser<C, T> parser) {
        this.parser = parser;
    }

    public QuotedArgumentParser<C, T> getParser() {
        return this.parser;
    }

    public UnquotedCharacterFilter getCharFilter() {
        return this.parser.isStrictQuoteEscaping() ? UnquotedCharacterFilter.STRICT : UnquotedCharacterFilter.PERMISSIVE;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        ArgumentParseResult<String> baseResult = this.baseParser.parse(commandContext, commandInput);
        Optional<String> baseParsed = baseResult.parsedValue();
        if (baseParsed.isPresent()) {
            return this.parser.parseQuotedString(commandContext, baseParsed.get());
        }
        return baseResult;
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this;
    }

    @Override
    public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> context, CommandInput commandInput) {
        String input = commandInput.lastRemainingToken();
        if (input.startsWith("\"")) {
            CommandInput unescapedInput = CommandInput.of(UnquotedCharacterFilter.unescapeString(input));
            return this.createSuggestions(context, unescapedInput, (suggestions, newSuggestions) -> {
                for (Suggestion suggestion : suggestions) {
                    newSuggestions.add(suggestion.withSuggestion(UnquotedCharacterFilter.escapeString(suggestion.suggestion())));
                }
            });
        }
        if (input.isEmpty()) {
            UnquotedCharacterFilter filter = this.getCharFilter();
            return this.createSuggestions(context, commandInput, (suggestions, newSuggestions) -> {
                for (Suggestion suggestion : suggestions) {
                    newSuggestions.add(suggestion.withSuggestion(filter.escapeStringIfNeeded(suggestion.suggestion())));
                }
            });
        }
        UnquotedCharacterFilter filter = this.getCharFilter();
        return this.createSuggestions(context, commandInput, (suggestions, newSuggestions) -> {
            for (Suggestion suggestion : suggestions) {
                if (!filter.isStringAllowed(suggestion.suggestion())) continue;
                newSuggestions.add(suggestion);
            }
        });
    }

    private CompletableFuture<? extends Iterable<? extends Suggestion>> createSuggestions(CommandContext<C> commandContext, CommandInput commandInput, BiConsumer<Iterable<? extends Suggestion>, List<Suggestion>> mapperAndFilter) {
        return this.parser.suggestionProvider().suggestionsFuture(commandContext, commandInput).thenApplyAsync(suggestions -> {
            ArrayList newSuggestions = suggestions instanceof Collection ? new ArrayList(((Collection)suggestions).size()) : new ArrayList();
            mapperAndFilter.accept((Iterable<? extends Suggestion>)suggestions, newSuggestions);
            return newSuggestions;
        });
    }

    public static void appendEscapedStringToBuilder(StringBuilder builder, String str, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; ++i) {
            QuotedArgumentParserProxy.appendEscapedCharToBuilder(builder, str.charAt(i));
        }
    }

    public static void appendEscapedCharToBuilder(StringBuilder builder, char c) {
        if (c == '\\' || c == '\"') {
            builder.append('\\');
        }
        builder.append(c);
    }

    public static void registerBrigadier(CloudBrigadierManager<?, ?> brig) throws Exception {
        Class<?> stringArgumentTypeClass = Class.forName("com.mojang.brigadier.arguments.StringArgumentType");
        Class<?> argumentTypeClass = Class.forName("com.mojang.brigadier.arguments.ArgumentType");
        Object quotedStringArgumentType = stringArgumentTypeClass.getMethod("string", new Class[0]).invoke(null, new Object[0]);
        BrigadierMappingBuilder builder = BrigadierMapping.builder();
        builder = builder.cloudSuggestions();
        builder = (BrigadierMappingBuilder)BrigadierMappingBuilder.class.getMethod("toConstant", argumentTypeClass).invoke(builder, quotedStringArgumentType);
        brig.mappings().registerMapping(QuotedArgumentParserProxy.class, builder.build());
    }
}

