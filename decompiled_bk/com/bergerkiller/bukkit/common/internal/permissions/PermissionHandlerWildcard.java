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

import com.bergerkiller.bukkit.common.ToggledState;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionDefaultFinder;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandler;
import com.bergerkiller.bukkit.common.internal.permissions.SinglePermissionChecker;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

class PermissionHandlerWildcard
implements PermissionHandler,
SinglePermissionChecker {
    private boolean hasSuperWildcardSupport = false;
    private final ToggledState needsWildcardCheck = new ToggledState(false);

    PermissionHandlerWildcard() {
    }

    protected boolean detectSuperWildcardSupport() {
        return false;
    }

    private boolean hasSuperWildcardSupport() {
        if (this.needsWildcardCheck.clear()) {
            this.hasSuperWildcardSupport = this.detectSuperWildcardSupport();
        }
        return this.hasSuperWildcardSupport;
    }

    @Override
    public Permission getOrCreatePermission(String node) {
        Permission perm = Bukkit.getPluginManager().getPermission(node);
        if (perm == null) {
            PermissionDefault permDefault = PermissionDefaultFinder.findDefault(node);
            perm = new Permission(node, permDefault);
            Bukkit.getPluginManager().addPermission(perm);
        }
        return perm;
    }

    @Override
    public boolean hasPermissionNoRecurse(CommandSender sender, String permission) {
        Permission perm = this.getOrCreatePermission(permission);
        return sender.hasPermission(perm);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String[] permissionNode) {
        if (this.hasSuperWildcardSupport()) {
            return this.hasPermissionNoRecurse(sender, StringUtil.join(".", permissionNode).toLowerCase(Locale.ENGLISH));
        }
        return this.permCheckWildcard(sender, permissionNode);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permissionNode) {
        String lowerNode = permissionNode.toLowerCase(Locale.ENGLISH);
        if (this.hasPermissionNoRecurse(sender, lowerNode)) {
            return true;
        }
        return !this.hasSuperWildcardSupport() && this.permCheckWildcard(sender, lowerNode);
    }
}

