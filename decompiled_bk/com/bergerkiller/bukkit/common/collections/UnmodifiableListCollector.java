/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class UnmodifiableListCollector
implements Collector {
    public static final UnmodifiableListCollector INSTANCE = new UnmodifiableListCollector();
    private final BiConsumer<Object, Object> _accumulator = (list, value) -> ((List)list).add(value);
    private final BinaryOperator<Object> _combiner = (left, right) -> {
        ((List)left).addAll((List)right);
        return left;
    };
    private final Function<Object, Object> _finisher = list -> Collections.unmodifiableList((List)list);
    private final Set<Collector.Characteristics> _characteristics = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    private UnmodifiableListCollector() {
    }

    public Supplier<Object> supplier() {
        return ArrayList::new;
    }

    public BiConsumer<Object, Object> accumulator() {
        return this._accumulator;
    }

    public BinaryOperator<Object> combiner() {
        return this._combiner;
    }

    public Function<Object, Object> finisher() {
        return this._finisher;
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return this._characteristics;
    }
}

