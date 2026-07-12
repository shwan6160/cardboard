/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.legacyimpl;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
public interface NBTLegacyHoverEventSerializer
extends LegacyHoverEventSerializer,
com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer {
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static LegacyHoverEventSerializer get() {
        return NBTLegacyHoverEventSerializerImpl.INSTANCE;
    }
}

