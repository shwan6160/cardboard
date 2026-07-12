/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Parser {
    public String name() default "";

    public String suggestions() default "";
}

