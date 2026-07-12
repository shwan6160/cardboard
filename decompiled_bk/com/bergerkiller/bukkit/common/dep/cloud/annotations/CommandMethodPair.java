/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;

final class CommandMethodPair {
    private final Method method;
    private final Command annotation;

    CommandMethodPair(@NonNull Method method, @NonNull Command annotation) {
        this.method = method;
        this.annotation = annotation;
    }

    @NonNull Method method() {
        return this.method;
    }

    @NonNull Command annotation() {
        return this.annotation;
    }
}

