/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface BuilderDecorator<C> {
    public static <C> @NonNull BuilderDecorator<C> defaultDescription(@NonNull CommandDescription description) {
        return builder -> builder.commandDescription(description);
    }

    public static <C> @NonNull BuilderDecorator<C> defaultPermission(@NonNull Permission permission) {
        return builder -> builder.permission(permission);
    }

    public static <C> @NonNull BuilderDecorator<C> applicable( @NonNull Command.Builder.Applicable<C> applicable) {
        return builder -> builder.apply(applicable);
    }

    public  @NonNull Command.Builder<C> decorate( @NonNull Command.Builder<C> var1);
}

