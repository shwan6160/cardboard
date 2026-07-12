/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.server.AsyncTabCompleteEvent$Completion
 *  io.papermc.paper.brigadier.PaperBrigadier
 *  io.papermc.paper.command.brigadier.MessageComponentSerializer
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.CompletionMapper;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.Message;
import io.papermc.paper.brigadier.PaperBrigadier;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;

final class ReflectiveCompletionMapper
implements CompletionMapper {
    private final CompletionMapper wrapped = CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.MessageComponentSerializer") ? new Modern() : new Legacy();

    ReflectiveCompletionMapper() {
    }

    @Override
    public // Could not load outer class - annotation placement on inner may be incorrect
     @NonNull AsyncTabCompleteEvent.Completion map(@NonNull TooltipSuggestion suggestion) {
        return this.wrapped.map(suggestion);
    }

    private static final class Modern
    implements CompletionMapper {
        private final Object serializer;
        private final Method deserializeOrNull;
        private final Method completionWithTooltipMethod;

        Modern() {
            Method instance = CraftBukkitReflection.needMethod(MessageComponentSerializer.class, "message", new Class[0]);
            try {
                this.serializer = instance.invoke(null, new Object[0]);
                this.deserializeOrNull = CraftBukkitReflection.needMethod(MessageComponentSerializer.class, "deserializeOrNull", Object.class);
                this.completionWithTooltipMethod = CraftBukkitReflection.needMethod(AsyncTabCompleteEvent.Completion.class, "completion", String.class, this.deserializeOrNull.getReturnType());
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public // Could not load outer class - annotation placement on inner may be incorrect
         @NonNull AsyncTabCompleteEvent.Completion map(@NonNull TooltipSuggestion suggestion) {
            try {
                return (AsyncTabCompleteEvent.Completion)this.completionWithTooltipMethod.invoke(null, suggestion.suggestion(), this.deserializeOrNull.invoke(this.serializer, suggestion.tooltip()));
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class Legacy
    implements CompletionMapper {
        private final MethodHandle completionWithTooltip;
        private final MethodHandle componentFromMessage;

        Legacy() {
            Method componentFromMessageMethod = CraftBukkitReflection.needMethod(PaperBrigadier.class, "componentFromMessage", Message.class);
            Method completionWithTooltipMethod = CraftBukkitReflection.needMethod(AsyncTabCompleteEvent.Completion.class, "completion", String.class, componentFromMessageMethod.getReturnType());
            try {
                this.componentFromMessage = MethodHandles.publicLookup().unreflect(componentFromMessageMethod);
                this.completionWithTooltip = MethodHandles.publicLookup().unreflect(completionWithTooltipMethod);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public // Could not load outer class - annotation placement on inner may be incorrect
         @NonNull AsyncTabCompleteEvent.Completion map(@NonNull TooltipSuggestion suggestion) {
            Message tooltip = suggestion.tooltip();
            if (tooltip == null) {
                return AsyncTabCompleteEvent.Completion.completion((String)suggestion.suggestion());
            }
            try {
                Object component = this.componentFromMessage.invoke(tooltip);
                return this.completionWithTooltip.invoke(suggestion.suggestion(), component);
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}

