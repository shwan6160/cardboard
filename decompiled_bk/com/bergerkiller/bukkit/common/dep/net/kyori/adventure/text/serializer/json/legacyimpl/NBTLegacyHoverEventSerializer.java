/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.legacyimpl;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializerImpl;
import org.jetbrains.annotations.NotNull;

public interface NBTLegacyHoverEventSerializer
extends LegacyHoverEventSerializer {
    @NotNull
    public static LegacyHoverEventSerializer get() {
        return NBTLegacyHoverEventSerializerImpl.INSTANCE;
    }
}

