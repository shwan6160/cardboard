/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class ParserParameters {
    private final Map<ParserParameter<?>, Object> internalMap = new HashMap();

    public static @NonNull ParserParameters empty() {
        return new ParserParameters();
    }

    public static <T> @NonNull ParserParameters single(@NonNull ParserParameter<T> parameter, @NonNull T value) {
        ParserParameters parameters = new ParserParameters();
        parameters.store(parameter, value);
        return parameters;
    }

    public <T> boolean has(@NonNull ParserParameter<T> parameter) {
        return this.internalMap.containsKey(parameter);
    }

    public <T> void store(@NonNull ParserParameter<T> parameter, @NonNull T value) {
        this.internalMap.put(parameter, value);
    }

    public <T> @NonNull T get(@NonNull ParserParameter<T> parameter, @NonNull T defaultValue) {
        return (T)this.internalMap.getOrDefault(parameter, defaultValue);
    }

    public void merge(@NonNull ParserParameters other) {
        this.internalMap.putAll(other.internalMap);
    }

    public @NonNull Map<@NonNull ParserParameter<?>, @NonNull Object> parameters() {
        return Collections.unmodifiableMap(this.internalMap);
    }
}

