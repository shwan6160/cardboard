/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.filtering;

import com.bergerkiller.bukkit.common.filtering.Filter;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FilterBundle<E>
implements Filter<E> {
    private final List<Filter<E>> filters = new ArrayList<Filter<E>>();

    public FilterBundle() {
    }

    public FilterBundle(Filter<?> ... filters) {
        LogicUtil.addArray(this.filters, filters);
    }

    public FilterBundle(Collection<Filter<?>> filters) {
        this.filters.addAll(filters);
    }

    public void addFilter(Filter<?> filter) {
        this.filters.add(filter);
    }

    public void removeFilter(Filter<?> filter) {
        this.filters.remove(filter);
    }

    public Collection<Filter<E>> getFilters() {
        return Collections.unmodifiableCollection(this.filters);
    }

    @Override
    public boolean isFiltered(E element) {
        for (Filter<E> filter : this.filters) {
            if (!filter.isFiltered(element)) continue;
            return true;
        }
        return false;
    }
}

