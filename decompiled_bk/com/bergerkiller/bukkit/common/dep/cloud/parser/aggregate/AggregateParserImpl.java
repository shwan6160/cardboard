/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateResultMapper;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

final class AggregateParserImpl<C, O>
implements AggregateParser<C, O> {
    private final List<CommandComponent<C>> components;
    private final TypeToken<O> valueType;
    private final AggregateResultMapper<C, O> mapper;

    AggregateParserImpl(@NonNull List<CommandComponent<C>> components, @NonNull TypeToken<O> valueType, @NonNull AggregateResultMapper<C, O> mapper) {
        this.components = components;
        this.valueType = valueType;
        this.mapper = mapper;
    }

    @Override
    public @NonNull List<@NonNull CommandComponent<C>> components() {
        return Collections.unmodifiableList(this.components);
    }

    @Override
    public @NonNull AggregateResultMapper<C, O> mapper() {
        return this.mapper;
    }

    @Override
    public @NonNull TypeToken<O> valueType() {
        return this.valueType;
    }
}

