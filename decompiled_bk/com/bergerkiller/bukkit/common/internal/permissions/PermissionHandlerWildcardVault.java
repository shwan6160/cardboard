/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.permissions.Permission
 *  org.bukkit.permissions.PermissionDefault
 */
package com.bergerkiller.bukkit.common.internal.permissions;

import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandlerWildcard;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

class PermissionHandlerWildcardVault
extends PermissionHandlerWildcard {
    private static final String PERMISSION_TEST_NODE_ROOT = "bkcommonlib.permission.testnode";
    private static final String PERMISSION_TEST_NODE = "bkcommonlib.permission.testnode.test";
    private static final String PERMISSION_TEST_NODE_ALL = "bkcommonlib.permission.testnode.*";
    private final net.milkbowl.vault.permission.Permission vault;

    public PermissionHandlerWildcardVault(net.milkbowl.vault.permission.Permission vault) {
        this.vault = vault;
    }

    @Override
    public boolean hasPermissionNoRecurse(CommandSender sender, String permission) {
        if (this.vault.hasSuperPermsCompat()) {
            return super.hasPermissionNoRecurse(sender, permission);
        }
        Permission perm = this.getOrCreatePermission(permission);
        if (perm.getDefault().getValue(sender.isOp())) {
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player)sender;
            return this.vault.playerHas(p.getWorld(), p.getName(), permission);
        }
        return this.vault.has(sender, permission);
    }

    @Override
    protected boolean detectSuperWildcardSupport() {
        int i;
        if (this.vault.getClass().getName().equals("me.TechsCode.UltraPermissions.hooks.pluginHooks.VaultPermissionHook")) {
            return true;
        }
        if (this.vault.getClass().getName().equals("me.lucko.luckperms.bukkit.vault.LuckPermsVaultPermission")) {
            return true;
        }
        Permission perm = this.getOrCreatePermission(PERMISSION_TEST_NODE);
        perm.setDefault(PermissionDefault.FALSE);
        int maxTries = 10000;
        String world = null;
        String testPlayerNameBase = "TestPlayer";
        StringBuilder testPlayerNameBldr = new StringBuilder("TestPlayer");
        String testPlayerName = "TestPlayer";
        for (i = 0; i < 10000; ++i) {
            testPlayerNameBldr.setLength("TestPlayer".length());
            testPlayerNameBldr.append(i);
            testPlayerName = testPlayerNameBldr.toString();
            try {
                if (this.vault.playerHas(world, testPlayerName, PERMISSION_TEST_NODE)) continue;
                break;
            }
            catch (Throwable t) {
                i = 10000;
            }
        }
        boolean hasSupport = false;
        if (i < 9999) {
            try {
                this.vault.playerAdd(world, testPlayerName, PERMISSION_TEST_NODE_ALL);
                try {
                    hasSupport = this.vault.playerHas(world, testPlayerName, PERMISSION_TEST_NODE);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    this.vault.playerRemove(world, testPlayerName, PERMISSION_TEST_NODE_ALL);
                }
                catch (Throwable throwable) {}
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        Bukkit.getPluginManager().removePermission(perm);
        return hasSupport;
    }
}

