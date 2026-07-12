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
import java.util.Collections;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class CommandParseException
extends IllegalArgumentException {
    private final Object commandSender;
    private final List<CommandComponent<?>> currentChain;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    protected CommandParseException(@NonNull Object commandSender, @NonNull List<CommandComponent<?>> currentChain) {
        this.commandSender = commandSender;
        this.currentChain = currentChain;
    }

    public @NonNull Object commandSender() {
        return this.commandSender;
    }

    public @NonNull List<@NonNull CommandComponent<?>> currentChain() {
        return Collections.unmodifiableList(this.currentChain);
    }
}

