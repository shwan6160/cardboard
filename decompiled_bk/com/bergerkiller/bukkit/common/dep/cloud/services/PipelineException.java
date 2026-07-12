/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class PipelineException
extends RuntimeException {
    public PipelineException(@NonNull Exception cause) {
        super(cause);
    }

    public PipelineException(@NonNull String message, @NonNull Exception cause) {
        super(message, cause);
    }
}

