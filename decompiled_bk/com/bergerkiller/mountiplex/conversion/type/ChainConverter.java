/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import java.util.List;

public final class ChainConverter<I, O>
extends Converter<I, O> {
    private final Converter<Object, Object>[] converters;
    private final boolean acceptsNull;

    public ChainConverter(List<Converter<?, ?>> chain) {
        super(chain.get((int)0).input, chain.get((int)(chain.size() - 1)).output);
        this.converters = chain.toArray(new Converter[chain.size()]);
        this.acceptsNull = this.converters[0].acceptsNullInput();
    }

    @Override
    public O convertInput(I value) {
        Converter<Object, Object> converter;
        Object v = value;
        Converter<Object, Object>[] converterArray = this.converters;
        int n = converterArray.length;
        for (int i = 0; i < n && (v = (converter = converterArray[i]).convertInput(v)) != null; ++i) {
        }
        return (O)v;
    }

    @Override
    public boolean acceptsNullInput() {
        return this.acceptsNull;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        result.append(" {\n");
        for (Converter<Object, Object> converter : this.converters) {
            result.append("  -> ").append(converter.toString()).append("\n");
        }
        result.append("}");
        return result.toString();
    }

    @Override
    public int getCost() {
        int cost = 0;
        for (Converter<Object, Object> converter : this.converters) {
            cost += converter.getCost();
        }
        return cost;
    }
}

