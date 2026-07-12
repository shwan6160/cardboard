/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface CaptionFormatter<C, T> {
    public static <C> @NonNull CaptionFormatter<C, String> patternReplacing(@NonNull Pattern pattern) {
        return new PatternReplacingCaptionFormatter(pattern);
    }

    public static <C> @NonNull CaptionFormatter<C, String> placeholderReplacing() {
        return new PatternReplacingCaptionFormatter(CaptionFormatter.placeholderPattern());
    }

    public static Pattern placeholderPattern() {
        return Pattern.compile("<(\\S+)>");
    }

    default public @NonNull T formatCaption(@NonNull Caption captionKey, @NonNull C recipient, @NonNull String caption, CaptionVariable ... variables) {
        return this.formatCaption(captionKey, recipient, caption, Arrays.asList(variables));
    }

    public @NonNull T formatCaption(@NonNull Caption var1, @NonNull C var2, @NonNull String var3, @NonNull List<@NonNull CaptionVariable> var4);

    public static final class PatternReplacingCaptionFormatter<C>
    implements CaptionFormatter<C, String> {
        private final Pattern pattern;

        private PatternReplacingCaptionFormatter(@NonNull Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public @NonNull String formatCaption(@NonNull Caption captionKey, @NonNull C recipient, @NonNull String caption, @NonNull List<@NonNull CaptionVariable> variables) {
            HashMap<String, String> replacements = new HashMap<String, String>();
            for (CaptionVariable variable : variables) {
                replacements.put(variable.key(), variable.value());
            }
            Matcher matcher = this.pattern.matcher(caption);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = (String)replacements.get(matcher.group(1));
                matcher.appendReplacement(stringBuffer, replacement == null ? "$0" : replacement);
            }
            matcher.appendTail(stringBuffer);
            return stringBuffer.toString();
        }
    }
}

