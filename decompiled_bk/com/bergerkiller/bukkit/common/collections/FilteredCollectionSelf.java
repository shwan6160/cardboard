/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.FilteredCollection;
import com.bergerkiller.bukkit.common.filtering.Filter;
import java.util.Collection;

public abstract class FilteredCollectionSelf<E>
extends FilteredCollection<E>
implements Filter<E> {
    protected FilteredCollectionSelf(Collection<E> base) {
        super(base, null);
        this.filter = this;
    }
}

