/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.permission;

import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResult;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="PermissionResult", generator="Immutables")
@Immutable
final class PermissionResultImpl
implements PermissionResult {
    private final boolean allowed;
    private final @NonNull Permission permission;

    private PermissionResultImpl(boolean allowed, @NonNull Permission permission) {
        this.allowed = allowed;
        this.permission = Objects.requireNonNull(permission, "permission");
    }

    private PermissionResultImpl(PermissionResultImpl original, boolean allowed, @NonNull Permission permission) {
        this.allowed = allowed;
        this.permission = permission;
    }

    @Override
    public boolean allowed() {
        return this.allowed;
    }

    @Override
    public @NonNull Permission permission() {
        return this.permission;
    }

    public final PermissionResultImpl withAllowed(boolean value) {
        if (this.allowed == value) {
            return this;
        }
        return new PermissionResultImpl(this, value, this.permission);
    }

    public final PermissionResultImpl withPermission(@NonNull Permission value) {
        if (this.permission == value) {
            return this;
        }
        @NonNull Permission newValue = Objects.requireNonNull(value, "permission");
        return new PermissionResultImpl(this, this.allowed, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof PermissionResultImpl && this.equalTo(0, (PermissionResultImpl)another);
    }

    private boolean equalTo(int synthetic, PermissionResultImpl another) {
        return this.allowed == another.allowed && this.permission.equals(another.permission);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Boolean.hashCode(this.allowed);
        h += (h << 5) + this.permission.hashCode();
        return h;
    }

    public String toString() {
        return "PermissionResult{allowed=" + this.allowed + ", permission=" + this.permission + "}";
    }

    public static PermissionResultImpl of(boolean allowed, @NonNull Permission permission) {
        return new PermissionResultImpl(allowed, permission);
    }

    public static PermissionResultImpl copyOf(PermissionResult instance) {
        if (instance instanceof PermissionResultImpl) {
            return (PermissionResultImpl)instance;
        }
        return PermissionResultImpl.of(instance.allowed(), instance.permission());
    }
}

