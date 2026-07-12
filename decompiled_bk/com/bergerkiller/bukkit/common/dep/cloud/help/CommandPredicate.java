/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface CommandPredicate<C>
extends Predicate<Command<C>> {
    public static <C> @NonNull CommandPredicate<C> acceptAll() {
        return cmd -> true;
    }

    @Override
    public boolean test(@NonNull Command<C> var1);
}

