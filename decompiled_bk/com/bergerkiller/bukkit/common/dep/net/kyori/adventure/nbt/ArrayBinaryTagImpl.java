/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.AbstractBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ArrayBinaryTag;

abstract class ArrayBinaryTagImpl
extends AbstractBinaryTag
implements ArrayBinaryTag {
    ArrayBinaryTagImpl() {
    }

    static void checkIndex(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }
}

