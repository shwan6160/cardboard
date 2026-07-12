/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.common.internal.permissions;

import java.util.Locale;
import org.bukkit.command.CommandSender;

interface SinglePermissionChecker {
    public boolean hasPermissionNoRecurse(CommandSender var1, String var2);

    default public boolean permCheckWildcard(CommandSender sender, String node) {
        return this.permCheckWildcard(sender, node.split("\\."));
    }

    default public boolean permCheckWildcard(CommandSender sender, String[] args) {
        int expectedLength = args.length;
        for (String node : args) {
            expectedLength += node.length();
        }
        return this.permCheckWildcard(sender, new StringBuilder(expectedLength), args, 0);
    }

    default public boolean permCheckWildcard(CommandSender sender, StringBuilder root, String[] args, int argIndex) {
        String rootText = root.toString();
        if (!rootText.isEmpty() && this.hasPermissionNoRecurse(sender, rootText)) {
            return true;
        }
        if (argIndex >= args.length) {
            return false;
        }
        int rootLength = root.length();
        if (rootLength != 0) {
            root.append('.');
            ++rootLength;
        }
        int newArgIndex = argIndex + 1;
        root.append(args[argIndex].toLowerCase(Locale.ENGLISH));
        if (this.permCheckWildcard(sender, root, args, newArgIndex)) {
            return true;
        }
        root.setLength(rootLength);
        root.append('*');
        return this.permCheckWildcard(sender, root, args, newArgIndex);
    }
}

