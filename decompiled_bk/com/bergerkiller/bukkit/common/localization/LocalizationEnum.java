/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.common.localization;

import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import org.bukkit.command.CommandSender;

public abstract class LocalizationEnum
implements ILocalizationEnum {
    private final String name;
    private final String defValue;

    public LocalizationEnum(String name, String defValue) {
        this.name = name;
        this.defValue = defValue;
    }

    @Override
    public String getDefault() {
        return this.defValue;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void message(CommandSender sender, String ... arguments) {
        ILocalizationEnum.super.message(sender, arguments);
    }

    @Override
    public abstract String get(String ... var1);
}

