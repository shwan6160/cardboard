/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.internal;

import java.util.LinkedList;
import java.util.StringTokenizer;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class CommandInputTokenizer {
    private static final String DELIMITER = " ";
    private static final String EMPTY = "";
    private final StringTokenizerFactory stringTokenizerFactory = new StringTokenizerFactory();
    private final String input;

    public CommandInputTokenizer(@NonNull String input) {
        this.input = input;
    }

    public @NonNull LinkedList<@NonNull String> tokenize() {
        StringTokenizer stringTokenizer = this.stringTokenizerFactory.createStringTokenizer();
        LinkedList<String> tokens = new LinkedList<String>();
        while (stringTokenizer.hasMoreElements()) {
            tokens.add(stringTokenizer.nextToken());
        }
        if (this.input.endsWith(DELIMITER)) {
            tokens.add(EMPTY);
        }
        return tokens;
    }

    private final class StringTokenizerFactory {
        private StringTokenizerFactory() {
        }

        private @NonNull StringTokenizer createStringTokenizer() {
            return new StringTokenizer(CommandInputTokenizer.this.input, CommandInputTokenizer.DELIMITER);
        }
    }
}

