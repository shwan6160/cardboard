/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.handling;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionController;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public final class ExceptionContextFactory<C> {
    private final ExceptionController<C> controller;

    public ExceptionContextFactory(@NonNull ExceptionController<C> controller) {
        this.controller = controller;
    }

    public <T extends Throwable> @NonNull ExceptionContext<C, T> createContext(@NonNull CommandContext<C> context, @NonNull T exception) {
        return new ExceptionContext.ExceptionContextImpl<C, T>(exception, context, this.controller);
    }
}

