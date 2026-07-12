/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.ArgumentMode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxParser;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class SyntaxParserImpl
implements SyntaxParser {
    private static final Predicate<String> PATTERN_ARGUMENT_LITERAL = Pattern.compile("([A-Za-z0-9\\-_]+)(|([A-Za-z0-9\\-_]+))*").asPredicate();
    private static final Predicate<String> PATTERN_ARGUMENT_REQUIRED = Pattern.compile("<([A-Za-z0-9\\-_]+)>").asPredicate();
    private static final Predicate<String> PATTERN_ARGUMENT_OPTIONAL = Pattern.compile("\\[([A-Za-z0-9\\-_]+)]").asPredicate();

    @Override
    public @NonNull List<@NonNull SyntaxFragment> parseSyntax(@Nullable Method method, @NonNull String syntax) {
        StringTokenizer stringTokenizer = new StringTokenizer(syntax, " ");
        ArrayList<SyntaxFragment> syntaxFragments = new ArrayList<SyntaxFragment>();
        while (stringTokenizer.hasMoreTokens()) {
            ArgumentMode mode;
            String major;
            String token = stringTokenizer.nextToken();
            ArrayList<String> minor = new ArrayList<String>();
            if (PATTERN_ARGUMENT_REQUIRED.test(token)) {
                major = token.substring(1, token.length() - 1);
                mode = ArgumentMode.REQUIRED;
            } else if (PATTERN_ARGUMENT_OPTIONAL.test(token)) {
                major = token.substring(1, token.length() - 1);
                mode = ArgumentMode.OPTIONAL;
            } else if (PATTERN_ARGUMENT_LITERAL.test(token)) {
                String[] literals = token.split("\\|");
                major = literals[0];
                minor.addAll(Arrays.asList(literals).subList(1, literals.length));
                mode = ArgumentMode.LITERAL;
            } else {
                throw new IllegalArgumentException(String.format("Unrecognizable syntax token '%s'", syntax));
            }
            syntaxFragments.add(new SyntaxFragment(major, minor, mode));
        }
        return syntaxFragments;
    }
}

