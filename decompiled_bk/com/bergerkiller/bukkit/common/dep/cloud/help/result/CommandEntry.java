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

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntryImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CommandEntry<C>
extends Comparable<CommandEntry<C>> {
    public static <C> @NonNull CommandEntry<C> of(@NonNull Command<C> command, @NonNull String syntax) {
        return CommandEntryImpl.of(command, syntax);
    }

    public @NonNull Command<C> command();

    public @NonNull String syntax();

    @Override
    default public int compareTo(@NonNull CommandEntry<C> other) {
        return this.syntax().compareTo(other.syntax());
    }
}

