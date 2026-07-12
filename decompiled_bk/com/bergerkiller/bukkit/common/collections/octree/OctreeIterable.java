/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;

public interface OctreeIterable<T>
extends Iterable<T> {
    @Override
    public OctreeIterator<T> iterator();
}

