/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.permissions.Permission
 *  org.bukkit.permissions.PermissionDefault
 */
package com.bergerkiller.bukkit.common.internal.permissions;

import com.bergerkiller.bukkit.common.internal.permissions.SinglePermissionChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

class PermissionDefaultFinder
implements SinglePermissionChecker {
    private boolean hasTRUE;
    private boolean hasOP;
    private boolean hasNOTOP;

    PermissionDefaultFinder() {
    }

    public static PermissionDefault findDefault(String permissionNode) {
        PermissionDefaultFinder finder = new PermissionDefaultFinder();
        finder.permCheckWildcard(null, new StringBuilder(permissionNode.length()), permissionNode.split("\\."), 0);
        return finder.getDefault();
    }

    public PermissionDefault getDefault() {
        if (this.hasTRUE) {
            return PermissionDefault.TRUE;
        }
        if (this.hasOP) {
            return PermissionDefault.OP;
        }
        if (this.hasNOTOP) {
            return PermissionDefault.NOT_OP;
        }
        return PermissionDefault.FALSE;
    }

    @Override
    public boolean hasPermissionNoRecurse(CommandSender sender, String permission) {
        Permission perm = Bukkit.getPluginManager().getPermission(permission);
        if (perm == null) {
            return false;
        }
        switch (perm.getDefault()) {
            case TRUE: {
                this.hasTRUE = true;
                break;
            }
            case OP: {
                this.hasOP = true;
                break;
            }
            case NOT_OP: {
                this.hasNOTOP = true;
                break;
            }
        }
        if (this.hasOP && this.hasNOTOP) {
            this.hasTRUE = true;
        }
        return this.hasTRUE;
    }
}

