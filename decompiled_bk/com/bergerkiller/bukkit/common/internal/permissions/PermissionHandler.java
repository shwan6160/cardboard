/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.permissions.Permission
 */
package com.bergerkiller.bukkit.common.internal.permissions;

import java.util.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public interface PermissionHandler {
    public Permission getOrCreatePermission(String var1);

    public boolean hasPermission(CommandSender var1, String var2);

    default public boolean hasPermission(CommandSender sender, String[] permissionNodePath) {
        return this.hasPermission(sender, String.join((CharSequence)".", permissionNodePath).toLowerCase(Locale.ENGLISH));
    }
}

