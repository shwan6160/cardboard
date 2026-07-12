/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.setting;

import com.bergerkiller.bukkit.common.dep.cloud.setting.EnumConfigurable;
import com.bergerkiller.bukkit.common.dep.cloud.setting.Setting;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public interface Configurable<S extends Setting> {
    public static <E extends Enum<E>> @NonNull Configurable<E> enumConfigurable(@NonNull Class<E> enumClass) {
        return new EnumConfigurable<Class<E>>(enumClass);
    }

    public @This @NonNull Configurable<S> set(@NonNull S var1, boolean var2);

    public boolean get(@NonNull S var1);
}

