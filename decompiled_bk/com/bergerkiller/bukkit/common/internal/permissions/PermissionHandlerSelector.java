/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.permissions;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandler;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandlerLuckPerms5;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandlerWildcard;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandlerWildcardVault;
import com.bergerkiller.bukkit.common.softdependency.SoftDependency;
import com.bergerkiller.bukkit.common.softdependency.SoftServiceDependency;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;

public class PermissionHandlerSelector {
    private PermissionHandler handler = null;
    private final SoftDependency<Plugin> luckoPerms5;
    private final SoftServiceDependency<Permission> vaultPermissions;
    private final List<Option> options;
    private Option currOption = null;

    public PermissionHandlerSelector(CommonPlugin plugin) {
        this.luckoPerms5 = SoftDependency.build((Plugin)plugin, "LuckPerms").withInitializer(p -> {
            try {
                Class.forName("net.luckperms.api.LuckPermsProvider");
                return p;
            }
            catch (Throwable t) {
                return null;
            }
        }).whenEnable(this::detectPermOption).whenDisable(this::detectPermOption).create();
        this.vaultPermissions = new SoftServiceDependency<Permission>((Plugin)plugin, "net.milkbowl.vault.permission.Permission"){

            @Override
            protected Permission initialize(Object service) {
                return (Permission)Permission.class.cast(service);
            }

            @Override
            protected void onEnable() {
                PermissionHandlerSelector.this.detectPermOption();
            }

            @Override
            protected void onDisable() {
                PermissionHandlerSelector.this.detectPermOption();
            }
        };
        Option[] optionArray = new Option[3];
        optionArray[0] = Option.of("LuckPerms5", () -> this.luckoPerms5.get() != null, PermissionHandlerLuckPerms5::new);
        optionArray[1] = Option.of("Vault", this.vaultPermissions::isEnabled, () -> new PermissionHandlerWildcardVault(this.vaultPermissions.get()));
        optionArray[2] = Option.of("Default", () -> true, PermissionHandlerWildcard::new);
        this.options = Arrays.asList(optionArray);
    }

    public PermissionHandler current() {
        return this.handler;
    }

    public void detectPermOption() {
        for (Option opt : this.options) {
            if (!opt.active.getAsBoolean()) continue;
            if (opt == this.currOption) break;
            this.currOption = opt;
            this.handler = opt.ctor.get();
            break;
        }
    }

    private static class Option {
        public final String name;
        public final BooleanSupplier active;
        public final Supplier<PermissionHandler> ctor;

        public Option(String name, BooleanSupplier active, Supplier<PermissionHandler> ctor) {
            this.name = name;
            this.active = active;
            this.ctor = ctor;
        }

        public static Option of(String name, BooleanSupplier active, Supplier<PermissionHandler> ctor) {
            return new Option(name, active, ctor);
        }
    }
}

