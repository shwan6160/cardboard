/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.luckperms.api.LuckPermsProvider
 *  net.luckperms.api.cacheddata.Result
 *  net.luckperms.api.platform.PlayerAdapter
 *  net.luckperms.api.util.Tristate
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.permissions.Permission
 *  org.bukkit.permissions.PermissionDefault
 *  org.bukkit.permissions.ServerOperator
 */
package com.bergerkiller.bukkit.common.internal.permissions;

import com.bergerkiller.bukkit.common.internal.permissions.PermissionDefaultFinder;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandler;
import java.util.EnumMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.Result;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.permissions.ServerOperator;

class PermissionHandlerLuckPerms5
implements PermissionHandler {
    private final PlayerAdapter<Player> playerAdapter;
    private final ConcurrentHashMap<String, DefaultChecker> fallbackDefaults = new ConcurrentHashMap();
    private static final EnumMap<PermissionDefault, DefaultChecker> DEFAULT_CHECKERS = new EnumMap(PermissionDefault.class);

    public PermissionHandlerLuckPerms5() {
        this.playerAdapter = LuckPermsProvider.get().getPlayerAdapter(Player.class);
    }

    @Override
    public Permission getOrCreatePermission(String permissionNode) {
        Permission perm = Bukkit.getPluginManager().getPermission(permissionNode);
        if (perm == null) {
            perm = new Permission(permissionNode, PermissionDefault.FALSE);
            Bukkit.getPluginManager().addPermission(perm);
        }
        return perm;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permissionNode) {
        Result result;
        permissionNode = permissionNode.toLowerCase(Locale.ENGLISH);
        Permission permission = Bukkit.getPluginManager().getPermission(permissionNode);
        if (permission != null) {
            return sender.hasPermission(permission);
        }
        if (sender instanceof Player && (result = this.playerAdapter.getPermissionData((Object)((Player)sender)).queryPermission(permissionNode)).node() != null) {
            return ((Tristate)result.result()).asBoolean();
        }
        return this.hasDefaultPermission(sender, permissionNode);
    }

    private boolean hasDefaultPermission(CommandSender sender, String permissionNode) {
        return this.fallbackDefaults.computeIfAbsent(permissionNode, s -> this.checker(PermissionDefaultFinder.findDefault(s))).hasPermission(sender);
    }

    DefaultChecker checker(PermissionDefault def) {
        DefaultChecker checker = DEFAULT_CHECKERS.get(def);
        return checker != null ? checker : s -> def.getValue(s.isOp());
    }

    static {
        DEFAULT_CHECKERS.put(PermissionDefault.FALSE, s -> false);
        DEFAULT_CHECKERS.put(PermissionDefault.TRUE, s -> true);
        DEFAULT_CHECKERS.put(PermissionDefault.OP, ServerOperator::isOp);
        DEFAULT_CHECKERS.put(PermissionDefault.NOT_OP, s -> !s.isOp());
    }

    @FunctionalInterface
    private static interface DefaultChecker {
        public boolean hasPermission(CommandSender var1);
    }
}

