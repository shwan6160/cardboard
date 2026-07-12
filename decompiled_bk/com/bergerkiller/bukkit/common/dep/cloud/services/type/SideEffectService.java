/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services.type;

import com.bergerkiller.bukkit.common.dep.cloud.services.State;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface SideEffectService<Context>
extends Service<Context, State> {
    @Override
    public @NonNull State handle(@NonNull Context var1) throws Exception;
}

