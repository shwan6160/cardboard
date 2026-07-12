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
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class StandardParameters {
    public static final ParserParameter<Number> RANGE_MIN = StandardParameters.create("min", TypeToken.get(Number.class));
    public static final ParserParameter<Number> RANGE_MAX = StandardParameters.create("max", TypeToken.get(Number.class));
    public static final ParserParameter<Boolean> GREEDY = StandardParameters.create("greedy", TypeToken.get(Boolean.class));
    @API(status=API.Status.STABLE)
    public static final ParserParameter<Boolean> FLAG_YIELDING = StandardParameters.create("flag_yielding", TypeToken.get(Boolean.class));
    @API(status=API.Status.STABLE)
    public static final ParserParameter<Boolean> QUOTED = StandardParameters.create("quoted", TypeToken.get(Boolean.class));
    @API(status=API.Status.STABLE)
    public static final ParserParameter<Boolean> LIBERAL = StandardParameters.create("liberal", TypeToken.get(Boolean.class));

    private StandardParameters() {
    }

    private static <T> @NonNull ParserParameter<T> create(@NonNull String key, @NonNull TypeToken<T> expectedType) {
        return new ParserParameter<T>(key, expectedType);
    }
}

