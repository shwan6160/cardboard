/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameter;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE, since="1.7.0")
public final class BukkitParserParameters {
    @API(status=API.Status.STABLE, since="1.8.0")
    public static final ParserParameter<Boolean> ALLOW_EMPTY_SELECTOR_RESULT = BukkitParserParameters.create("allow_empty_selector_result", TypeToken.get(Boolean.class));
    public static final ParserParameter<Boolean> REQUIRE_EXPLICIT_NAMESPACE = BukkitParserParameters.create("require_explicit_namespace", TypeToken.get(Boolean.class));
    public static final ParserParameter<String> DEFAULT_NAMESPACE = BukkitParserParameters.create("default_namespace", TypeToken.get(String.class));

    private BukkitParserParameters() {
    }

    private static <T> @NonNull ParserParameter<T> create(@NonNull String key, @NonNull TypeToken<T> expectedType) {
        return new ParserParameter<T>(key, expectedType);
    }
}

