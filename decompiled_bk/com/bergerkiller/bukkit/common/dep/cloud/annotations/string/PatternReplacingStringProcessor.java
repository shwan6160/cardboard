/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.string;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.string.StringProcessor;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PatternReplacingStringProcessor
implements StringProcessor {
    private final Pattern pattern;
    private final Function<MatchResult, String> replacementProvider;

    public PatternReplacingStringProcessor(@NonNull Pattern pattern, @NonNull Function<@NonNull MatchResult, @Nullable String> replacementProvider) {
        this.pattern = pattern;
        this.replacementProvider = replacementProvider;
    }

    @Override
    public @NonNull String processString(@NonNull String input) {
        Matcher matcher = this.pattern.matcher(input);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = this.replacementProvider.apply(matcher);
            matcher.appendReplacement(stringBuffer, replacement == null ? "$0" : replacement);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
}

