/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import java.util.LinkedList;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class StringArrayParser<C>
implements ArgumentParser<C, String[]> {
    private static final Pattern FLAG_PATTERN = Pattern.compile("(-[A-Za-z_\\-0-9])|(--[A-Za-z_\\-0-9]*)");
    private final boolean flagYielding;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String[]> stringArrayParser() {
        return ParserDescriptor.of(new StringArrayParser<C>(), String[].class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String[]> flagYieldingStringArrayParser() {
        return ParserDescriptor.of(new StringArrayParser<C>(true), String[].class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, String[]> characterComponent() {
        return CommandComponent.builder().parser(StringArrayParser.stringArrayParser());
    }

    public StringArrayParser() {
        this.flagYielding = false;
    }

    @API(status=API.Status.STABLE)
    public StringArrayParser(boolean flagYielding) {
        this.flagYielding = flagYielding;
    }

    @Override
    public @NonNull ArgumentParseResult<String @NonNull []> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        int size = commandInput.remainingTokens();
        if (this.flagYielding) {
            String string;
            LinkedList<String> result = new LinkedList<String>();
            for (int i = 0; i < size && !(string = commandInput.peekString()).isEmpty() && !FLAG_PATTERN.matcher(string).matches(); ++i) {
                result.add(commandInput.readString());
            }
            return ArgumentParseResult.success(result.toArray(new String[0]));
        }
        String[] result = new String[size];
        for (int i = 0; i < result.length; ++i) {
            result[i] = commandInput.readString();
        }
        return ArgumentParseResult.success(result);
    }
}

