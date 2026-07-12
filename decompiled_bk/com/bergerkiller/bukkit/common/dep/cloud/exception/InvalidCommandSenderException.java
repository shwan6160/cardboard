/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandParseException;
import com.bergerkiller.bukkit.common.dep.cloud.util.TypeUtils;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public final class InvalidCommandSenderException
extends CommandParseException {
    private final Set<Type> requiredSenderTypes;
    private final @Nullable Command<?> command;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public InvalidCommandSenderException(@NonNull Object commandSender, @NonNull Type requiredSenderTypes, @NonNull List<@NonNull CommandComponent<?>> currentChain, @Nullable Command<?> command) {
        this(commandSender, new HashSet<Type>(Collections.singletonList(requiredSenderTypes)), currentChain, command);
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public InvalidCommandSenderException(@NonNull Object commandSender, @NonNull Set<Type> requiredSenderTypes, @NonNull List<@NonNull CommandComponent<?>> currentChain, @Nullable Command<?> command) {
        super(commandSender, currentChain);
        this.requiredSenderTypes = Collections.unmodifiableSet(requiredSenderTypes);
        this.command = command;
    }

    public @NonNull Set<Type> requiredSenderTypes() {
        return this.requiredSenderTypes;
    }

    @Override
    public String getMessage() {
        if (this.requiredSenderTypes.size() == 1) {
            return String.format("%s is not allowed to execute that command. Must be of type %s", this.commandSender().getClass().getSimpleName(), TypeUtils.simpleName(this.requiredSenderTypes.iterator().next()));
        }
        return String.format("%s is not allowed to execute that command. Must be one of %s", this.commandSender().getClass().getSimpleName(), this.requiredSenderTypes.stream().map(TypeUtils::simpleName).collect(Collectors.joining(", ")));
    }

    @API(status=API.Status.STABLE)
    public @Nullable Command<?> command() {
        return this.command;
    }
}

