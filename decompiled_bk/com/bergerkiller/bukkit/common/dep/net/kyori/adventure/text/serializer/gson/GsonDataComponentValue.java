/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonNull;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonDataComponentValueImpl;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface GsonDataComponentValue
extends DataComponentValue {
    public static GsonDataComponentValue gsonDataComponentValue(@NotNull JsonElement data) {
        if (data instanceof JsonNull) {
            return GsonDataComponentValueImpl.RemovedGsonComponentValueImpl.INSTANCE;
        }
        return new GsonDataComponentValueImpl(Objects.requireNonNull(data, "data"));
    }

    @NotNull
    public JsonElement element();
}

