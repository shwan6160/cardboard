/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.Commands;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.nullness.qual.NonNull;

@Repeatable(value=Commands.class)
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface Command {
    public static final String ANNOTATION_PATH = "com.bergerkiller.bukkit.common.dep.cloud.annotations.Command";

    public @NonNull String value();

    public @NonNull Class<?> requiredSender() default Object.class;
}

