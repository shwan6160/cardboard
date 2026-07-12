/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Permission {
    public @NonNull String[] value() default {};

    public @NonNull Mode mode() default Mode.ANY_OF;

    public static enum Mode {
        ANY_OF,
        ALL_OF;


        @NonNull com.bergerkiller.bukkit.common.dep.cloud.permission.Permission combine(@NonNull Stream<com.bergerkiller.bukkit.common.dep.cloud.permission.Permission> permissions) {
            List<com.bergerkiller.bukkit.common.dep.cloud.permission.Permission> permissionList = permissions.collect(Collectors.toList());
            if (this == ANY_OF) {
                return com.bergerkiller.bukkit.common.dep.cloud.permission.Permission.anyOf(permissionList);
            }
            return com.bergerkiller.bukkit.common.dep.cloud.permission.Permission.allOf(permissionList);
        }
    }
}

