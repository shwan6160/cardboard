/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.NamespacedKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.ArrayList;
import java.util.Objects;
import org.apiguardian.api.API;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class NamespacedKeyParser<C>
implements ArgumentParser<C, NamespacedKey>,
BlockingSuggestionProvider.Strings<C> {
    private final boolean requireExplicitNamespace;
    private final String defaultNamespace;

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, NamespacedKey> namespacedKeyParser() {
        return NamespacedKeyParser.namespacedKeyParser(false);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, NamespacedKey> namespacedKeyParser(boolean requireExplicitNamespace) {
        return NamespacedKeyParser.namespacedKeyParser(requireExplicitNamespace, "minecraft");
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, NamespacedKey> namespacedKeyParser(boolean requireExplicitNamespace, @NonNull String defaultNamespace) {
        return ParserDescriptor.of(new NamespacedKeyParser<C>(requireExplicitNamespace, defaultNamespace), NamespacedKey.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, NamespacedKey> namespacedKeyComponent() {
        return CommandComponent.builder().parser(NamespacedKeyParser.namespacedKeyParser());
    }

    public NamespacedKeyParser(boolean requireExplicitNamespace, String defaultNamespace) {
        this.requireExplicitNamespace = requireExplicitNamespace;
        this.defaultNamespace = defaultNamespace;
    }

    @Override
    public @NonNull ArgumentParseResult<NamespacedKey> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        int maxSemi;
        String input = commandInput.peekString();
        String[] split = input.split(":");
        int n = maxSemi = split.length > 1 ? 1 : 0;
        if (input.length() - input.replace(":", "").length() > maxSemi) {
            return ArgumentParseResult.failure(new NamespacedKeyParseException(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY, input, commandContext));
        }
        try {
            NamespacedKey ret;
            if (split.length == 1) {
                if (this.requireExplicitNamespace) {
                    return ArgumentParseResult.failure(new NamespacedKeyParseException(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE, input, commandContext));
                }
                ret = new NamespacedKey(this.defaultNamespace, commandInput.readString());
            } else if (split.length == 2) {
                ret = new NamespacedKey(commandInput.readUntilAndSkip(':'), commandInput.readString());
            } else {
                return ArgumentParseResult.failure(new NamespacedKeyParseException(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY, input, commandContext));
            }
            return ArgumentParseResult.success(ret);
        }
        catch (IllegalArgumentException ex) {
            Caption caption = ex.getMessage().contains("namespace") ? BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NAMESPACE : BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY;
            return ArgumentParseResult.failure(new NamespacedKeyParseException(caption, input, commandContext));
        }
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(this.defaultNamespace + ":");
        String token = input.peekString();
        if (!token.contains(":") && !token.isEmpty()) {
            ret.add(token + ":");
        }
        return ret;
    }

    private static <C> void registerParserSupplier(@NonNull CommandManager<C> commandManager) {
        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(NamespacedKey.class), params -> new NamespacedKeyParser(params.has(BukkitParserParameters.REQUIRE_EXPLICIT_NAMESPACE), params.get(BukkitParserParameters.DEFAULT_NAMESPACE, "minecraft")));
    }

    public static final class NamespacedKeyParseException
    extends ParserException {
        private final String input;

        public NamespacedKeyParseException(@NonNull Caption caption, @NonNull String input, @NonNull CommandContext<?> context) {
            super(NamespacedKeyParser.class, context, caption, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NamespacedKeyParseException that = (NamespacedKeyParseException)o;
            return this.input.equals(that.input) && this.errorCaption().equals(that.errorCaption());
        }

        public int hashCode() {
            return Objects.hash(this.input, this.errorCaption());
        }
    }
}

