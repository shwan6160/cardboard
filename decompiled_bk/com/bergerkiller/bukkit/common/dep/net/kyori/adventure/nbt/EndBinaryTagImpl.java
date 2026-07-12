/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.AbstractBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.EndBinaryTag;

final class EndBinaryTagImpl
extends AbstractBinaryTag
implements EndBinaryTag {
    static final EndBinaryTagImpl INSTANCE = new EndBinaryTagImpl();

    EndBinaryTagImpl() {
    }

    public boolean equals(Object that) {
        return this == that;
    }

    public int hashCode() {
        return 0;
    }
}

