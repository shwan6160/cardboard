/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.cloud.parsers;

import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParserProxy;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProviderHolder;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface QuotedArgumentParser<C, T>
extends SuggestionProviderHolder<C> {
    public static <C, T> QuotedArgumentParser<C, T> getFromParser(ArgumentParser<C, T> parser) {
        if (parser instanceof QuotedArgumentParserProxy) {
            return ((QuotedArgumentParserProxy)parser).getParser();
        }
        return null;
    }

    default public ArgumentParser<C, T> createParser() {
        return new QuotedArgumentParserProxy(this);
    }

    default public ParserDescriptor<C, T> createDescriptor(TypeToken<T> outputType) {
        return ParserDescriptor.of(this.createParser(), outputType);
    }

    default public ParserDescriptor<C, T> createDescriptor(Class<T> outputType) {
        return ParserDescriptor.of(this.createParser(), outputType);
    }

    default public boolean isStrictQuoteEscaping() {
        return false;
    }

    public ArgumentParseResult<T> parseQuotedString(CommandContext<C> var1, String var2);

    @Override
    default public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this instanceof SuggestionProvider ? (SuggestionProvider)((Object)this) : SuggestionProvider.noSuggestions();
    }

    public static <C> SuggestionProvider<C> permissiveQuotedSuggestions(final SuggestionProvider<C> base) {
        return new QuotedArgumentParser<C, String>(){

            @Override
            public boolean isStrictQuoteEscaping() {
                return false;
            }

            @Override
            public ArgumentParseResult<String> parseQuotedString(CommandContext<C> commandContext, String inputString) {
                return ArgumentParseResult.success(inputString);
            }

            @Override
            public SuggestionProvider<C> suggestionProvider() {
                return base;
            }
        }.createParser().suggestionProvider();
    }

    public static <C> SuggestionProvider<C> strictQuotedSuggestions(final SuggestionProvider<C> base) {
        return new QuotedArgumentParser<C, String>(){

            @Override
            public boolean isStrictQuoteEscaping() {
                return true;
            }

            @Override
            public ArgumentParseResult<String> parseQuotedString(CommandContext<C> commandContext, String inputString) {
                return ArgumentParseResult.success(inputString);
            }

            @Override
            public SuggestionProvider<C> suggestionProvider() {
                return base;
            }
        }.createParser().suggestionProvider();
    }
}

