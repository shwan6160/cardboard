/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandContextKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitHelper;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class BukkitCommandPreprocessor<C>
implements CommandPreprocessor<C> {
    private final BukkitCommandManager<C> commandManager;
    private final @Nullable BukkitBackwardsBrigadierSenderMapper<C, ?> mapper;

    BukkitCommandPreprocessor(@NonNull BukkitCommandManager<C> commandManager) {
        this.commandManager = commandManager;
        this.mapper = this.commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER) ? new BukkitBackwardsBrigadierSenderMapper(this.commandManager.senderMapper()) : null;
    }

    @Override
    public void accept(@NonNull CommandPreprocessingContext<C> context) {
        if (this.mapper != null && !context.commandContext().contains("_cloud_brigadier_native_sender")) {
            context.commandContext().store("_cloud_brigadier_native_sender", this.mapper.apply(context.commandContext().sender()));
        }
        context.commandContext().store(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER, this.commandManager.senderMapper().reverse(context.commandContext().sender()));
        context.commandContext().computeIfAbsent(BukkitCommandContextKeys.SENDER_SCHEDULER_EXECUTOR, $ -> BukkitHelper.mainThreadExecutor(this.commandManager));
    }
}

