/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.ComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONComponentSerializerAccessor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.PlatformAPI;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JSONComponentSerializer
extends ComponentSerializer<Component, Component, String> {
    @NotNull
    public static JSONComponentSerializer json() {
        return JSONComponentSerializerAccessor.Instances.INSTANCE;
    }

    public static @NotNull Builder builder() {
        return JSONComponentSerializerAccessor.Instances.BUILDER_SUPPLIER.get();
    }

    public static interface Builder {
        @NotNull
        public Builder options(@NotNull OptionState var1);

        @NotNull
        public Builder editOptions(@NotNull Consumer<OptionState.Builder> var1);

        @Deprecated
        @NotNull
        public Builder downsampleColors();

        @NotNull
        public Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer var1);

        @Deprecated
        @NotNull
        public Builder emitLegacyHoverEvent();

        @NotNull
        public JSONComponentSerializer build();
    }

    @PlatformAPI
    @ApiStatus.Internal
    public static interface Provider {
        @PlatformAPI
        @ApiStatus.Internal
        @NotNull
        public JSONComponentSerializer instance();

        @PlatformAPI
        @ApiStatus.Internal
        @NotNull
        public @NotNull Supplier<@NotNull Builder> builder();
    }
}

