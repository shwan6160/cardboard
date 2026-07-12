/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.help.result;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntry;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.HelpQueryResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.VerboseCommandResultImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface VerboseCommandResult<C>
extends HelpQueryResult<C> {
    public static <C> @NonNull VerboseCommandResult<C> of(@NonNull HelpQuery<C> query, @NonNull CommandEntry<C> entry) {
        return VerboseCommandResultImpl.of(query, entry);
    }

    @Override
    public @NonNull HelpQuery<C> query();

    public @NonNull CommandEntry<C> entry();
}

