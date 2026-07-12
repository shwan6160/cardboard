/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTagImpl;
import java.util.Collections;

final class ListBinaryTag0 {
    private static final String WRAPPER_KEY = "";

    private ListBinaryTag0() {
    }

    static BinaryTag unbox(CompoundBinaryTag compound) {
        BinaryTag potentialValue;
        if (compound.size() == 1 && (potentialValue = compound.get(WRAPPER_KEY)) != null) {
            return potentialValue;
        }
        return compound;
    }

    static CompoundBinaryTag box(BinaryTag tag) {
        if (ListBinaryTag0.needsBox(tag)) {
            return new CompoundBinaryTagImpl(Collections.singletonMap(WRAPPER_KEY, tag));
        }
        return (CompoundBinaryTag)tag;
    }

    private static boolean needsBox(BinaryTag tag) {
        if (!(tag instanceof CompoundBinaryTag)) {
            return true;
        }
        CompoundBinaryTag compound = (CompoundBinaryTag)tag;
        return compound.size() == 1 && compound.get(WRAPPER_KEY) != null;
    }
}

