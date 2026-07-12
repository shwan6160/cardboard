/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.util;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class StringUtils {
    private StringUtils() {
    }

    public static int countCharOccurrences(@NonNull String haystack, char needle) {
        int occurrences = 0;
        for (int i = 0; i < haystack.length(); ++i) {
            if (haystack.charAt(i) != needle) continue;
            ++occurrences;
        }
        return occurrences;
    }

    public static @NonNull String replaceAll(@NonNull String string, @NonNull Pattern pattern, @NonNull Function<@NonNull MatchResult, @NonNull String> replacer) {
        Matcher matcher = pattern.matcher(string);
        matcher.reset();
        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacer.apply(matcher);
                matcher.appendReplacement(sb, replacement);
            } while (result = matcher.find());
            matcher.appendTail(sb);
            return sb.toString();
        }
        return string;
    }

    public static @Nullable String trimBeforeLastSpace(String suggestion, String input) {
        int lastSpace = input.lastIndexOf(32);
        if (lastSpace == -1) {
            return suggestion;
        }
        if (suggestion.toLowerCase(Locale.ROOT).startsWith(input.toLowerCase(Locale.ROOT).substring(0, lastSpace))) {
            return suggestion.substring(lastSpace + 1);
        }
        return null;
    }

    public static @Nullable String trimBeforeLastSpace(String suggestion, CommandInput commandInput) {
        String input = commandInput.isEmpty(true) ? "" : commandInput.copy().skipWhitespace().remainingInput();
        return StringUtils.trimBeforeLastSpace(suggestion, input);
    }
}

