/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@API(status=API.Status.STABLE)
public @interface ExceptionHandler {
    public @NonNull Class<? extends Throwable> value();
}

