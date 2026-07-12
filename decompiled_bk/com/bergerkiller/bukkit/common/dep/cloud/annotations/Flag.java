/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Flag {
    public @NonNull String value();

    public @NonNull String[] aliases() default {""};

    public @NonNull String parserName() default "";

    public @NonNull String suggestions() default "";

    public @NonNull String description() default "";

    public @NonNull String permission() default "";

    @API(status=API.Status.STABLE)
    public boolean repeatable() default false;
}

