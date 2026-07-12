/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.commands.annotations;

import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresMultiplePermissions;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=CommandRequiresMultiplePermissions.class)
public @interface CommandRequiresPermission {
    public Permission value();
}

