/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.string;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface StringProcessor {
    public static @NonNull StringProcessor noOp() {
        return new NoOpStringProcessor();
    }

    public @NonNull String processString(@NonNull String var1);

    public static final class NoOpStringProcessor
    implements StringProcessor {
        @Override
        public @NonNull String processString(@NonNull String input) {
            return input;
        }
    }
}

