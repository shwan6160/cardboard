/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.concurrent.Executor;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;

public final class BukkitCommandContextKeys {
    public static final CloudKey<CommandSender> BUKKIT_COMMAND_SENDER = CloudKey.of("BukkitCommandSender", TypeToken.get(CommandSender.class));
    @API(status=API.Status.STABLE, since="2.0.0")
    public static final CloudKey<Executor> SENDER_SCHEDULER_EXECUTOR = CloudKey.of("SenderSchedulerExecutor", Executor.class);

    private BukkitCommandContextKeys() {
    }
}

