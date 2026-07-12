/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.NativeAudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface AudienceProvider<C>
extends Function<C, Audience> {
    @Override
    public @NonNull Audience apply(@NonNull C var1);

    public static <C extends Audience> AudienceProvider<C> nativeAudience() {
        return NativeAudienceProvider.INSTANCE;
    }
}

