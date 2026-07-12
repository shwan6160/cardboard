/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQueryImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface HelpQuery<C> {
    public static <C> @NonNull HelpQuery<C> of(@NonNull C sender, @NonNull String query) {
        return HelpQueryImpl.of(sender, query);
    }

    public @NonNull C sender();

    public @NonNull String query();
}

