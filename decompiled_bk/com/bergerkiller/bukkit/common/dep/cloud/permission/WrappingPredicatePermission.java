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
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResult;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PredicatePermission;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
final class WrappingPredicatePermission<C>
implements PredicatePermission<C> {
    private final CloudKey<Void> key;
    private final Predicate<C> predicate;

    WrappingPredicatePermission(@NonNull CloudKey<Void> key, @NonNull Predicate<C> predicate) {
        this.key = key;
        this.predicate = predicate;
    }

    @Override
    public @NonNull PermissionResult testPermission(@NonNull C sender) {
        return PermissionResult.of(this.predicate.test(sender), this);
    }

    @Override
    public @NonNull CloudKey<Void> key() {
        return this.key;
    }

    public String toString() {
        return this.key.name();
    }
}

