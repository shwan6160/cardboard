/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.service.AutoService
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.impl;

import com.bergerkiller.bukkit.common.dep.gson.JsonNull;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValueConverterRegistry;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import com.google.auto.service.AutoService;
import java.util.Collections;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@AutoService(value={DataComponentValueConverterRegistry.Provider.class})
@ApiStatus.Internal
public final class GsonDataComponentValueConverterProvider
implements DataComponentValueConverterRegistry.Provider {
    private static final Key ID = Key.key("adventure", "serializer/gson");

    @Override
    @NotNull
    public Key id() {
        return ID;
    }

    @Override
    @NotNull
    public Iterable<DataComponentValueConverterRegistry.Conversion<?, ?>> conversions() {
        return Collections.singletonList(DataComponentValueConverterRegistry.Conversion.convert(DataComponentValue.Removed.class, GsonDataComponentValue.class, (k, removed) -> GsonDataComponentValue.gsonDataComponentValue(JsonNull.INSTANCE)));
    }
}

