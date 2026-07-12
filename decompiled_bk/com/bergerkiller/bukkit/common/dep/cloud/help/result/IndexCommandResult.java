/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 *  org.immutables.value.Value$Parameter
 */
package com.bergerkiller.bukkit.common.dep.cloud.help.result;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntry;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.HelpQueryResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.IndexCommandResultImpl;
import java.util.Iterator;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface IndexCommandResult<C>
extends HelpQueryResult<C>,
Iterable<CommandEntry<C>> {
    public static <C> @NonNull IndexCommandResult<C> of(@NonNull HelpQuery<C> query, @NonNull List<@NonNull CommandEntry<C>> entries) {
        return IndexCommandResultImpl.of(query, entries);
    }

    @Override
    public @NonNull HelpQuery<C> query();

    public @NonNull List<@NonNull CommandEntry<C>> entries();

    @Value.Parameter(value=false)
    default public boolean isEmpty() {
        return this.entries().isEmpty();
    }

    @Override
    @Value.Parameter(value=false)
    default public @NonNull Iterator<@NonNull CommandEntry<C>> iterator() {
        return this.entries().iterator();
    }
}

