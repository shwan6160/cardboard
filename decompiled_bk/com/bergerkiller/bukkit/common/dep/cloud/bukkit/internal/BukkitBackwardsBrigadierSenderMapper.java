/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal;

import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import java.lang.reflect.Method;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class BukkitBackwardsBrigadierSenderMapper<C, S>
implements Function<C, S> {
    private static final Class<?> VANILLA_COMMAND_WRAPPER_CLASS = CraftBukkitReflection.needOBCClass("command.VanillaCommandWrapper");
    private static final Method GET_LISTENER_METHOD = CraftBukkitReflection.needMethod(VANILLA_COMMAND_WRAPPER_CLASS, "getListener", CommandSender.class);
    private final SenderMapper<?, C> senderMapper;

    public BukkitBackwardsBrigadierSenderMapper(@NonNull SenderMapper<?, C> senderMapper) {
        this.senderMapper = senderMapper;
    }

    @Override
    public S apply(@NonNull C cloud) {
        try {
            return (S)GET_LISTENER_METHOD.invoke(null, this.senderMapper.reverse(cloud));
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}

