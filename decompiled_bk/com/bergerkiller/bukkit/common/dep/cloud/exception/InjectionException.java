/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception;

import org.checkerframework.checker.nullness.qual.NonNull;

public class InjectionException
extends RuntimeException {
    public InjectionException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}

