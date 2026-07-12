/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.key.MutableCloudKeyContainer;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParsingContextImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface AggregateParsingContext<C>
extends MutableCloudKeyContainer {
    public static <C> @NonNull AggregateParsingContext<C> argumentContext(@NonNull AggregateParser<C, ?> parser) {
        return new AggregateParsingContextImpl<C>(parser);
    }
}

