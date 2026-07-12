/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.api.BinaryTagHolder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.RemovedDataComponentValueImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;

public interface DataComponentValue
extends Examinable {
    public static @NotNull Removed removed() {
        return RemovedDataComponentValueImpl.REMOVED;
    }

    public static interface Removed
    extends DataComponentValue {
    }

    public static interface TagSerializable
    extends DataComponentValue {
        @NotNull
        public BinaryTagHolder asBinaryTag();
    }
}

