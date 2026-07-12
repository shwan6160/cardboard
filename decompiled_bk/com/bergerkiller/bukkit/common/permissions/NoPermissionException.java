/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.permissions;

public class NoPermissionException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String permission;

    public NoPermissionException() {
        this("");
    }

    public NoPermissionException(String permission) {
        super(permission.isEmpty() ? "No permission" : "No '" + permission + "' permission");
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }
}

