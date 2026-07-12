/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Material
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MaterialParser<C>
implements ArgumentParser<C, Material>,
BlockingSuggestionProvider<C> {
    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Material> materialParser() {
        return ParserDescriptor.of(new MaterialParser<C>(), Material.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, Material> materialComponent() {
        return CommandComponent.builder().parser(MaterialParser.materialParser());
    }

    @Override
    public @NonNull ArgumentParseResult<Material> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        try {
            Material material = Material.valueOf((String)input.toUpperCase(Locale.ROOT));
            return ArgumentParseResult.success(material);
        }
        catch (IllegalArgumentException exception) {
            return ArgumentParseResult.failure(new MaterialParseException(input, commandContext));
        }
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return Arrays.stream(Material.values()).map(Enum::name).map(String::toLowerCase).map(Suggestion::suggestion).collect(Collectors.toList());
    }

    public static final class MaterialParseException
    extends ParserException {
        private final String input;

        public MaterialParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(MaterialParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_MATERIAL, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}

