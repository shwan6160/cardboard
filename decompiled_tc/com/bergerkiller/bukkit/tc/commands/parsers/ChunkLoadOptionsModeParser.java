/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.properties.standard.type.ChunkLoadOptions;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChunkLoadOptionsModeParser
implements ArgumentParser<CommandSender, ChunkLoadOptions.Mode>,
BlockingSuggestionProvider.Strings<CommandSender> {
    public static ParserDescriptor<CommandSender, ChunkLoadOptions.Mode> chunkLoadOptionsModeParser() {
        return ParserDescriptor.of((ArgumentParser)new ChunkLoadOptionsModeParser(), ChunkLoadOptions.Mode.class);
    }

    public @NonNull ArgumentParseResult< @NonNull ChunkLoadOptions.Mode> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        Optional<ChunkLoadOptions.Mode> parsed = ChunkLoadOptions.Mode.fromName(commandInput.peekString());
        if (!parsed.isPresent()) {
            return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_INPUT_CHUNK_LOADING_MODE_INVALID, new String[]{commandInput.peekString()}));
        }
        commandInput.readString();
        return ArgumentParseResult.success((Object)((Object)parsed.get()));
    }

    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput input) {
        return ChunkLoadOptions.Mode.getAllNames();
    }
}

