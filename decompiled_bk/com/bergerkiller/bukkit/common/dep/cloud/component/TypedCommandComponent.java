/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.component;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.ComponentPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyHolder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public final class TypedCommandComponent<C, T>
extends CommandComponent<C>
implements CloudKeyHolder<T> {
    TypedCommandComponent(@NonNull String name, @NonNull ArgumentParser<C, ?> parser, @NonNull TypeToken<?> valueType, @NonNull Description description, @NonNull CommandComponent.ComponentType componentType, @Nullable DefaultValue<C, ?> defaultValue, @NonNull SuggestionProvider<C> suggestionProvider, @NonNull Collection<@NonNull ComponentPreprocessor<C>> componentPreprocessors) {
        super(name, parser, valueType, description, componentType, defaultValue, suggestionProvider, componentPreprocessors);
    }

    @Override
    public @NonNull TypeToken<T> valueType() {
        return super.valueType();
    }

    @Override
    public @NonNull ArgumentParser<C, T> parser() {
        return super.parser();
    }

    @Override
    public @Nullable DefaultValue<C, T> defaultValue() {
        return super.defaultValue();
    }

    @Override
    public @NonNull CloudKey<T> key() {
        return CloudKey.of(this.name(), this.valueType());
    }
}

