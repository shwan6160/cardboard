/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.common.permissions;

import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import org.bukkit.command.CommandSender;

public interface IPermissionEnum
extends IPermissionDefault {
    public String getRootName();

    default public int getArgumentCount() {
        return 0;
    }

    @Override
    default public String getName() {
        String rootName = this.getRootName();
        int count = this.getArgumentCount();
        if (count == 0) {
            return rootName;
        }
        StringBuffer outputBuffer = new StringBuffer(rootName.length() + count * 2);
        outputBuffer.append(rootName);
        for (int n = 0; n < count; ++n) {
            outputBuffer.append(".*");
        }
        return outputBuffer.toString();
    }

    default public boolean has(CommandSender sender) {
        return this.has(sender, StringUtil.EMPTY_ARRAY);
    }

    default public boolean has(CommandSender sender, String ... args) {
        if (this.getArgumentCount() > args.length) {
            throw new IllegalArgumentException("This permission requires " + this.getArgumentCount() + " arguments, but " + args.length + " were provided");
        }
        if (args.length == 0) {
            return CommonUtil.hasPermission(sender, this.getName());
        }
        String[] fragments = this.getRootName().split("\\.");
        return CommonUtil.hasPermission(sender, LogicUtil.appendArray(fragments, args));
    }

    default public boolean handleMsg(CommandSender sender, String message) {
        return this.handleMsg(sender, message, StringUtil.EMPTY_ARRAY);
    }

    default public boolean handleMsg(CommandSender sender, String message, String ... args) {
        if (this.has(sender, args)) {
            return true;
        }
        sender.sendMessage(message);
        return false;
    }

    default public void handle(CommandSender sender) {
        this.handle(sender, StringUtil.EMPTY_ARRAY);
    }

    default public void handle(CommandSender sender, String ... args) {
        if (this.getArgumentCount() > args.length) {
            throw new IllegalArgumentException("This permission requires " + this.getArgumentCount() + " arguments, but " + args.length + " were provided");
        }
        if (args.length == 0) {
            String rootName = this.getRootName();
            if (!CommonUtil.hasPermission(sender, rootName)) {
                throw new NoPermissionException(rootName);
            }
        } else {
            String[] fragments = this.getRootName().split("\\.");
            CharSequence[] permPath = LogicUtil.appendArray(fragments, args);
            if (!CommonUtil.hasPermission(sender, (String[])permPath)) {
                throw new NoPermissionException(String.join((CharSequence)".", permPath));
            }
        }
    }
}

