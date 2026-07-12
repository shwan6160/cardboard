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
@Generated(from="Permission", generator="Immutables")
@Immutable
final class PermissionImpl
implements Permission {
    private final @NonNull String permissionString;

    private PermissionImpl(@NonNull String permissionString) {
        this.permissionString = Objects.requireNonNull(permissionString, "permissionString");
    }

    private PermissionImpl(PermissionImpl original, @NonNull String permissionString) {
        this.permissionString = permissionString;
    }

    @Override
    public @NonNull String permissionString() {
        return this.permissionString;
    }

    public final PermissionImpl withPermissionString(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "permissionString");
        if (this.permissionString.equals(newValue)) {
            return this;
        }
        return new PermissionImpl(this, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof PermissionImpl && this.equalTo(0, (PermissionImpl)another);
    }

    private boolean equalTo(int synthetic, PermissionImpl another) {
        return this.permissionString.equals(another.permissionString);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.permissionString.hashCode();
        return h;
    }

    public String toString() {
        return "Permission{permissionString=" + this.permissionString + "}";
    }

    public static PermissionImpl of(@NonNull String permissionString) {
        return new PermissionImpl(permissionString);
    }

    public static PermissionImpl copyOf(Permission instance) {
        if (instance instanceof PermissionImpl) {
            return (PermissionImpl)instance;
        }
        return PermissionImpl.of(instance.permissionString());
    }
}

