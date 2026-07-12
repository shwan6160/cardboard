/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.filtering;

import com.bergerkiller.bukkit.common.filtering.Filter;

public class FilterType<E>
implements Filter<E> {
    private final Class<?> type;

    public FilterType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getFilteredType() {
        return this.type;
    }

    @Override
    public boolean isFiltered(E element) {
        return !this.getFilteredType().isInstance(element);
    }
}

