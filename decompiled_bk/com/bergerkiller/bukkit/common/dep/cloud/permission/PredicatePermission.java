/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.permission;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyHolder;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResult;
import com.bergerkiller.bukkit.common.dep.cloud.permission.WrappingPredicatePermission;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface PredicatePermission<C>
extends Permission,
CloudKeyHolder<Void> {
    public static <C> PredicatePermission<C> of(@NonNull CloudKey<Void> key, @NonNull Predicate<C> predicate) {
        return new WrappingPredicatePermission<C>(key, predicate);
    }

    public static <C> PredicatePermission<C> of(final @NonNull Predicate<C> predicate) {
        return new PredicatePermission<C>(){

            @Override
            public @NonNull PermissionResult testPermission(@NonNull C sender) {
                return PermissionResult.of(predicate.test(sender), this);
            }
        };
    }

    @Override
    default public @NonNull CloudKey<Void> key() {
        return CloudKey.of(this.getClass().getSimpleName());
    }

    @Override
    default public @NonNull String permissionString() {
        return this.key().name();
    }

    @API(status=API.Status.STABLE)
    public @NonNull PermissionResult testPermission(@NonNull C var1);

    @Override
    default public boolean isEmpty() {
        return false;
    }
}

