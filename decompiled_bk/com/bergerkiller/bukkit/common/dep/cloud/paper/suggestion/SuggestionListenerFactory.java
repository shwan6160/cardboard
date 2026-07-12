/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.paper.LegacyPaperCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.AsyncCommandSuggestionListener;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.BrigadierAsyncCommandSuggestionListener;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.SuggestionListener;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, since="2.0.0")
public interface SuggestionListenerFactory<C> {
    public static <C> @NonNull SuggestionListenerFactory<C> create(@NonNull LegacyPaperCommandManager<C> commandManager) {
        return new SuggestionListenerFactoryImpl(commandManager);
    }

    public @NonNull SuggestionListener<C> createListener();

    public static final class SuggestionListenerFactoryImpl<C>
    implements SuggestionListenerFactory<C> {
        private final LegacyPaperCommandManager<C> commandManager;

        private SuggestionListenerFactoryImpl(@NonNull LegacyPaperCommandManager<C> commandManager) {
            this.commandManager = commandManager;
        }

        @Override
        public @NonNull SuggestionListener<C> createListener() {
            @Nullable Class<?> completionCls = CraftBukkitReflection.findClass("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent$Completion");
            if (completionCls != null) {
                return new BrigadierAsyncCommandSuggestionListener<C>(this.commandManager);
            }
            return new AsyncCommandSuggestionListener<C>(this.commandManager);
        }
    }
}

