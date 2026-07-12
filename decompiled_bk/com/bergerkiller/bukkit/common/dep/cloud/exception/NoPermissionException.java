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
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResult;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class NoPermissionException
extends CommandParseException {
    private final PermissionResult result;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public NoPermissionException(@NonNull PermissionResult permissionResult, @NonNull Object commandSender, @NonNull List<@NonNull CommandComponent<?>> currentChain) {
        super(commandSender, currentChain);
        if (permissionResult.allowed()) {
            throw new IllegalArgumentException("Provided permission result was one that succeeded instead of failed");
        }
        this.result = permissionResult;
    }

    @Override
    public final String getMessage() {
        return String.format("Missing permission '%s'", this.missingPermission());
    }

    @API(status=API.Status.STABLE)
    public @NonNull Permission missingPermission() {
        return this.result.permission();
    }

    @API(status=API.Status.STABLE)
    public @NonNull PermissionResult permissionResult() {
        return this.result;
    }

    @Override
    public final synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public final synchronized Throwable initCause(Throwable cause) {
        return this;
    }
}

