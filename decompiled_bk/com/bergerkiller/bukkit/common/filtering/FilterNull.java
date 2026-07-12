/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.filtering;

import com.bergerkiller.bukkit.common.filtering.Filter;

public class FilterNull<E>
implements Filter<E> {
    public static final FilterNull INSTANCE = new FilterNull();

    @Override
    public boolean isFiltered(E element) {
        return element == null;
    }

    public static <T> FilterNull<T> getInstance() {
        return INSTANCE;
    }
}

