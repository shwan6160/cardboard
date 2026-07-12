/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.setting;

import com.bergerkiller.bukkit.common.dep.cloud.setting.Configurable;
import java.util.EnumSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

final class EnumConfigurable<S extends Enum<S>>
implements Configurable<S> {
    private final EnumSet<S> settings;

    EnumConfigurable(@NonNull Class<S> settingClass) {
        this.settings = EnumSet.noneOf(settingClass);
    }

    EnumConfigurable(@NonNull S defaultSetting) {
        this.settings = EnumSet.of(defaultSetting);
    }

    @Override
    public @This @NonNull EnumConfigurable<S> set(@NonNull S setting, boolean value) {
        if (value) {
            this.settings.add(setting);
        } else {
            this.settings.remove(setting);
        }
        return this;
    }

    @Override
    public boolean get(@NonNull S setting) {
        return this.settings.contains(setting);
    }
}

