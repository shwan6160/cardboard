/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.method;

import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL)
public final class AnnotatedMethodHandlerInitiationException
extends RuntimeException {
    AnnotatedMethodHandlerInitiationException(@Nullable Throwable cause) {
        super(cause);
    }
}

