/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.permissions.PermissionDefault
 */
package com.bergerkiller.bukkit.common.permissions;

import com.bergerkiller.bukkit.common.permissions.IPermissionEnum;
import java.util.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public abstract class PermissionEnum
implements IPermissionEnum {
    private final String node;
    private final PermissionDefault def;
    private final String desc;
    private final int argCount;

    protected PermissionEnum(String node, PermissionDefault def, String description) {
        this(node, def, description, 0);
    }

    protected PermissionEnum(String node, PermissionDefault def, String description, int argCount) {
        this.node = node.toLowerCase(Locale.ENGLISH);
        this.def = def;
        this.desc = description;
        this.argCount = argCount;
    }

    @Override
    public String getRootName() {
        return this.node;
    }

    @Override
    public int getArgumentCount() {
        return this.argCount;
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public PermissionDefault getDefault() {
        return this.def;
    }

    @Override
    public String getDescription() {
        return this.desc;
    }

    @Override
    public String getName() {
        return IPermissionEnum.super.getName();
    }

    @Override
    public boolean handleMsg(CommandSender sender, String message) {
        return IPermissionEnum.super.handleMsg(sender, message);
    }

    @Override
    public boolean handleMsg(CommandSender sender, String message, String ... args) {
        return IPermissionEnum.super.handleMsg(sender, message, args);
    }

    @Override
    public void handle(CommandSender sender) {
        IPermissionEnum.super.handle(sender);
    }

    @Override
    public void handle(CommandSender sender, String ... args) {
        IPermissionEnum.super.handle(sender, args);
    }

    @Override
    public boolean has(CommandSender sender) {
        return IPermissionEnum.super.has(sender);
    }

    @Override
    public boolean has(CommandSender sender, String ... args) {
        return IPermissionEnum.super.has(sender, args);
    }
}

