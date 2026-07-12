/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.option.value;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.Option;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.value.ValueSources;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ValueSource {
    public static ValueSource environmentVariable() {
        return ValueSource.environmentVariable("");
    }

    public static ValueSource environmentVariable(String prefix) {
        return new ValueSources.EnvironmentVariable(prefix);
    }

    public static ValueSource systemProperty() {
        return ValueSource.systemProperty("");
    }

    public static ValueSource systemProperty(String prefix) {
        return new ValueSources.SystemProperty(prefix);
    }

    public <T> @Nullable T value(Option<T> var1);
}

