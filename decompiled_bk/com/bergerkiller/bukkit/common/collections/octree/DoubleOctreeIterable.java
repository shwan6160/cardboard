/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.collections.octree.DoubleOctreeIterator;

public interface DoubleOctreeIterable<T>
extends Iterable<T> {
    @Override
    public DoubleOctreeIterator<T> iterator();
}

