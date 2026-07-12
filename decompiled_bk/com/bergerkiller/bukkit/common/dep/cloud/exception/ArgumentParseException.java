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
public class ArgumentParseException
extends CommandParseException {
    private final Throwable cause;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public ArgumentParseException(@NonNull Throwable throwable, @NonNull Object commandSender, @NonNull List<@NonNull CommandComponent<?>> currentChain) {
        super(commandSender, currentChain);
        this.cause = throwable;
    }

    @Override
    public synchronized @NonNull Throwable getCause() {
        return this.cause;
    }
}

