/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils.modlist;

import java.util.List;

public interface ModificationTrackedList<E>
extends List<E> {
    public int getModCount();
}

