/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandParseException;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class NoSuchCommandException
extends CommandParseException {
    private final String suppliedCommand;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public NoSuchCommandException(@NonNull Object commandSender, @NonNull List<CommandComponent<?>> currentChain, @NonNull String command) {
        super(commandSender, currentChain);
        this.suppliedCommand = command;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (CommandComponent<?> commandComponent : this.currentChain()) {
            if (commandComponent == null) continue;
            builder.append(" ").append(commandComponent.name());
        }
        return String.format("Unrecognized command input '%s' following chain%s", this.suppliedCommand, builder.toString());
    }

    public @NonNull String suppliedCommand() {
        return this.suppliedCommand;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return this;
    }
}

