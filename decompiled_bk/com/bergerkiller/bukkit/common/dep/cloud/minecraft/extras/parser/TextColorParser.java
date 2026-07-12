/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.parser;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserContributor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.NamedTextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class TextColorParser<C>
implements ArgumentParser<C, TextColor>,
BlockingSuggestionProvider.Strings<C> {
    private static final Pattern LEGACY_PREDICATE = Pattern.compile("&[0-9a-fA-F]");
    private static final Pattern HEX_PREDICATE = Pattern.compile("#?([a-fA-F0-9]{1,6})");
    private static final Collection<Pair<Character, NamedTextColor>> COLORS = Arrays.asList(Pair.of(Character.valueOf('0'), NamedTextColor.BLACK), Pair.of(Character.valueOf('1'), NamedTextColor.DARK_BLUE), Pair.of(Character.valueOf('2'), NamedTextColor.DARK_GREEN), Pair.of(Character.valueOf('3'), NamedTextColor.DARK_AQUA), Pair.of(Character.valueOf('4'), NamedTextColor.DARK_RED), Pair.of(Character.valueOf('5'), NamedTextColor.DARK_PURPLE), Pair.of(Character.valueOf('6'), NamedTextColor.GOLD), Pair.of(Character.valueOf('7'), NamedTextColor.GRAY), Pair.of(Character.valueOf('8'), NamedTextColor.DARK_GRAY), Pair.of(Character.valueOf('9'), NamedTextColor.BLUE), Pair.of(Character.valueOf('a'), NamedTextColor.GREEN), Pair.of(Character.valueOf('b'), NamedTextColor.AQUA), Pair.of(Character.valueOf('c'), NamedTextColor.RED), Pair.of(Character.valueOf('d'), NamedTextColor.LIGHT_PURPLE), Pair.of(Character.valueOf('e'), NamedTextColor.YELLOW), Pair.of(Character.valueOf('f'), NamedTextColor.WHITE));

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, TextColor> textColorParser() {
        return ParserDescriptor.of(new TextColorParser<C>(), TextColor.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, TextColor> textColorComponent() {
        return CommandComponent.builder().parser(TextColorParser.textColorParser());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull TextColor> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.peekString();
        if (LEGACY_PREDICATE.matcher(input).matches()) {
            commandInput.moveCursor(1);
            char code = Character.toLowerCase(commandInput.read());
            for (Pair<Character, NamedTextColor> pair : COLORS) {
                if (pair.first().charValue() != code) continue;
                return ArgumentParseResult.success((TextColor)pair.second());
            }
            commandInput.moveCursor(-2);
        }
        for (Pair<Character, NamedTextColor> pair : COLORS) {
            if (!pair.second().toString().equalsIgnoreCase(commandInput.peekString())) continue;
            commandInput.readString();
            return ArgumentParseResult.success((TextColor)pair.second());
        }
        if (HEX_PREDICATE.matcher(commandInput.peekString()).matches()) {
            if (commandInput.peek() == '#') {
                commandInput.moveCursor(1);
            }
            return ArgumentParseResult.success(TextColor.color(commandInput.readInteger(16)));
        }
        return ArgumentParseResult.failure(new TextColorParseException(commandContext, input));
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        LinkedList<String> suggestions = new LinkedList<String>();
        String token = input.readString();
        String tokenLower = token.toLowerCase(Locale.ROOT);
        boolean matchedName = false;
        for (String name : NamedTextColor.NAMES.keys()) {
            if (name.contains(tokenLower)) {
                matchedName = true;
            }
            suggestions.add(name);
        }
        if (!matchedName && !token.isEmpty() && (token.equals("#") || HEX_PREDICATE.matcher(token).matches() && token.length() < (token.startsWith("#") ? 7 : 6))) {
            char c;
            for (c = 'a'; c <= 'f'; c = (char)(c + '\u0001')) {
                suggestions.add(String.format("%s%c", token, Character.valueOf(c)));
            }
            for (c = '0'; c <= '9'; c = (char)(c + '\u0001')) {
                suggestions.add(String.format("%s%c", token, Character.valueOf(c)));
            }
        }
        return suggestions;
    }

    private static final class TextColorParseException
    extends ParserException {
        private TextColorParseException(@NonNull CommandContext<?> commandContext, @NonNull String input) {
            super(TextColorParser.class, commandContext, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR, CaptionVariable.of("input", input));
        }
    }

    @API(status=API.Status.INTERNAL)
    public static final class Contributor
    implements ParserContributor {
        @Override
        public <C> void contribute(ParserRegistry<C> registry) {
            try {
                registry.registerParser(TextColorParser.textColorParser());
            }
            catch (Exception | LinkageError throwable) {
                // empty catch block
            }
        }
    }
}

