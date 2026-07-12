/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips;

import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.CompletionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.NativeCompletionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.ReflectiveCompletionMapper;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import org.apiguardian.api.API;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, since="2.0.0")
public interface CompletionMapperFactory {
    public static @NonNull CompletionMapperFactory detectingRelocation() {
        return new CompletionMapperFactoryImpl();
    }

    public @NonNull CompletionMapper createMapper();

    public static final class CompletionMapperFactoryImpl
    implements CompletionMapperFactory {
        private CompletionMapperFactoryImpl() {
        }

        @Override
        public @NonNull CompletionMapper createMapper() {
            if (Audience.class.isAssignableFrom(Player.class)) {
                return new NativeCompletionMapper();
            }
            return new ReflectiveCompletionMapper();
        }
    }
}

