/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.flag;

import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public final class FlagContext {
    public static final Object FLAG_PRESENCE_VALUE = new Object();
    private final Map<String, List> flagValues = new HashMap<String, List>();

    private FlagContext() {
    }

    public static @NonNull FlagContext create() {
        return new FlagContext();
    }

    public void addPresenceFlag(@NonNull CommandFlag<?> flag) {
        this.flagValues.computeIfAbsent(flag.name(), $ -> new ArrayList()).add(FLAG_PRESENCE_VALUE);
    }

    public <T> void addValueFlag(@NonNull CommandFlag<T> flag, @NonNull T value) {
        this.flagValues.computeIfAbsent(flag.name(), $ -> new ArrayList()).add(value);
    }

    @API(status=API.Status.STABLE)
    public <T> int count(@NonNull CommandFlag<T> flag) {
        return this.getAll(flag).size();
    }

    @API(status=API.Status.STABLE)
    public int count(@NonNull String flag) {
        return this.getAll(flag).size();
    }

    public boolean isPresent(@NonNull String flag) {
        List value = this.flagValues.get(flag);
        return value != null && !value.isEmpty();
    }

    @API(status=API.Status.STABLE)
    public boolean isPresent(@NonNull CommandFlag<Void> flag) {
        return this.isPresent(flag.name());
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Optional<T> getValue(@NonNull String name) {
        List value = this.flagValues.get(name);
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value.get(0));
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Optional<T> getValue(@NonNull CommandFlag<T> flag) {
        return this.getValue(flag.name());
    }

    public <T> @Nullable T getValue(@NonNull String name, @Nullable T defaultValue) {
        return this.getValue(name).orElse(defaultValue);
    }

    @API(status=API.Status.STABLE)
    public <T> @Nullable T getValue(@NonNull CommandFlag<T> name, @Nullable T defaultValue) {
        return this.getValue(name).orElse(defaultValue);
    }

    @API(status=API.Status.STABLE)
    public boolean hasFlag(@NonNull String name) {
        return this.getValue(name).isPresent();
    }

    @API(status=API.Status.STABLE)
    public boolean hasFlag(@NonNull CommandFlag<?> flag) {
        return this.getValue(flag).isPresent();
    }

    @API(status=API.Status.STABLE)
    public boolean contains(@NonNull String name) {
        return this.hasFlag(name);
    }

    @API(status=API.Status.STABLE)
    public boolean contains(@NonNull CommandFlag<?> flag) {
        return this.hasFlag(flag);
    }

    @API(status=API.Status.STABLE)
    public <T> @Nullable T get(@NonNull String name) {
        return this.getValue(name).orElse(null);
    }

    @API(status=API.Status.STABLE)
    public <T> @Nullable T get(@NonNull CommandFlag<T> flag) {
        return this.getValue(flag).orElse(null);
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Collection<T> getAll(@NonNull CommandFlag<T> flag) {
        List values = this.flagValues.get(flag.name());
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        return Collections.emptyList();
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Collection<T> getAll(@NonNull String flag) {
        List values = this.flagValues.get(flag);
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        return Collections.emptyList();
    }
}

