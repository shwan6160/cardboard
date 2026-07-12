/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.permission;

import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class AndPermission
implements Permission {
    private final Set<Permission> permissions;

    AndPermission(@NonNull Set<Permission> permissions) {
        if (permissions.isEmpty()) {
            throw new IllegalArgumentException("AndPermission may not have an empty set of permissions");
        }
        this.permissions = Collections.unmodifiableSet(permissions);
    }

    @Override
    public @NonNull Collection<@NonNull Permission> permissions() {
        return this.permissions;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NonNull String permissionString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Permission> iterator = this.permissions.iterator();
        while (iterator.hasNext()) {
            Permission permission = iterator.next();
            stringBuilder.append('(').append(permission.permissionString()).append(')');
            if (!iterator.hasNext()) continue;
            stringBuilder.append(" & ");
        }
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AndPermission that = (AndPermission)o;
        return this.permissions.equals(that.permissions);
    }

    public int hashCode() {
        return Objects.hash(this.permissions());
    }

    public @NonNull String toString() {
        return this.permissionString();
    }
}

