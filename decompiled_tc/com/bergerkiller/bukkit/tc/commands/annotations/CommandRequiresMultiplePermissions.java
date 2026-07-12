/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.commands.annotations;

import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CommandRequiresMultiplePermissions {
    public CommandRequiresPermission[] value();
}

