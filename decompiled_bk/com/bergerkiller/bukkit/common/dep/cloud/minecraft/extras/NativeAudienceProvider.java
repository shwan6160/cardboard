/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.AudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.NonNull;

final class NativeAudienceProvider<C extends Audience>
implements AudienceProvider<C> {
    static final NativeAudienceProvider<?> INSTANCE = new NativeAudienceProvider();

    private NativeAudienceProvider() {
    }

    @Override
    public @NonNull Audience apply(@NonNull C sender) {
        return sender;
    }
}

