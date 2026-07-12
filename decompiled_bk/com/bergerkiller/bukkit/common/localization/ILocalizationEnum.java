/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.common.localization;

import com.bergerkiller.bukkit.common.localization.ILocalizationDefault;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import org.bukkit.command.CommandSender;

public interface ILocalizationEnum
extends ILocalizationDefault {
    default public void message(CommandSender sender, String ... arguments) {
        String text = this.get(arguments);
        if (!LogicUtil.nullOrEmpty(text)) {
            sender.sendMessage(text);
        }
    }

    public String get(String ... var1);
}

