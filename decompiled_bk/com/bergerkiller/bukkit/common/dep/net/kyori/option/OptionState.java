/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jspecify.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.option;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.Option;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionSchema;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.value.ValueSource;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface OptionState {
    @Deprecated
    public static OptionState emptyOptionState() {
        return OptionSchema.globalSchema().emptyState();
    }

    @Deprecated
    public static Builder optionState() {
        return OptionSchema.globalSchema().stateBuilder();
    }

    @Deprecated
    public static VersionedBuilder versionedOptionState() {
        return OptionSchema.globalSchema().versionedStateBuilder();
    }

    public OptionSchema schema();

    public boolean has(Option<?> var1);

    public <V> @Nullable V value(Option<V> var1);

    @ApiStatus.NonExtendable
    public static interface Builder {
        public <V> Builder value(Option<V> var1, @Nullable V var2);

        public Builder values(OptionState var1);

        public Builder values(ValueSource var1);

        public OptionState build();
    }

    @ApiStatus.NonExtendable
    public static interface VersionedBuilder {
        public VersionedBuilder version(int var1, Consumer<Builder> var2);

        public Versioned build();
    }

    @ApiStatus.NonExtendable
    public static interface Versioned
    extends OptionState {
        public Map<Integer, OptionState> childStates();

        public Versioned at(int var1);
    }
}

