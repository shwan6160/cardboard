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
public class InvalidSyntaxException
extends CommandParseException {
    private final String correctSyntax;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public InvalidSyntaxException(@NonNull String correctSyntax, @NonNull Object commandSender, @NonNull List<@NonNull CommandComponent<?>> currentChain) {
        super(commandSender, currentChain);
        this.correctSyntax = correctSyntax;
    }

    public @NonNull String correctSyntax() {
        return this.correctSyntax;
    }

    @Override
    public final String getMessage() {
        return String.format("Invalid command syntax. Correct syntax is: %s", this.correctSyntax);
    }
}

