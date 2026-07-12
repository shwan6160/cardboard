/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE, since="2.0.0")
public final class MinecraftExtrasParserParameters {
    @API(status=API.Status.STABLE, since="2.0.0")
    public static final ParserParameter<Function<String, ? extends Component>> COMPONENT_DECODER = MinecraftExtrasParserParameters.create("component_decoder", new TypeToken<Function<String, ? extends Component>>(){});

    private MinecraftExtrasParserParameters() {
    }

    private static <T> @NonNull ParserParameter<T> create(@NonNull String key, @NonNull TypeToken<T> expectedType) {
        return new ParserParameter<T>(key, expectedType);
    }
}

