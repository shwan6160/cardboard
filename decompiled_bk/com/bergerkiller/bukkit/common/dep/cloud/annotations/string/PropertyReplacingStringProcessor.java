/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.string;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.string.PatternReplacingStringProcessor;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PropertyReplacingStringProcessor
extends PatternReplacingStringProcessor {
    public static final Pattern PROPERTY_REGEX = Pattern.compile("\\$\\{(\\S+)}");

    public PropertyReplacingStringProcessor(@NonNull Function<@NonNull String, @Nullable String> replacementProvider) {
        super(PROPERTY_REGEX, new PropertyReplacementProvider(replacementProvider));
    }

    private static final class PropertyReplacementProvider
    implements Function<MatchResult, String> {
        private final Function<String, String> replacementProvider;

        private PropertyReplacementProvider(@NonNull Function<String, String> replacementProvider) {
            this.replacementProvider = replacementProvider;
        }

        @Override
        public @Nullable String apply(@NonNull MatchResult matchResult) {
            return this.replacementProvider.apply(matchResult.group(1));
        }
    }
}

