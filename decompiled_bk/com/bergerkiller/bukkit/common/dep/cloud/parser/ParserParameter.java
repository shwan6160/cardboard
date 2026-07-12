/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class ParserParameter<T> {
    private final String key;
    private final TypeToken<T> expectedType;

    public ParserParameter(@NonNull String key, @NonNull TypeToken<T> expectedType) {
        this.key = key;
        this.expectedType = expectedType;
    }

    public @NonNull String key() {
        return this.key;
    }

    public @NonNull TypeToken<T> expectedType() {
        return this.expectedType;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ParserParameter that = (ParserParameter)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.expectedType, that.expectedType);
    }

    public final int hashCode() {
        return Objects.hash(this.key, this.expectedType);
    }
}

