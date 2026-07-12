/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CommandContainer {
    public static final String ANNOTATION_PATH = "com.bergerkiller.bukkit.common.dep.cloud.annotations.processing.CommandContainer";

    public int priority() default 1;
}

