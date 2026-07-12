/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.HelpQueryResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.IndexCommandResult;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface HelpHandler<C> {
    public @NonNull HelpQueryResult<C> query(@NonNull HelpQuery<C> var1);

    default public @NonNull IndexCommandResult<C> queryRootIndex(@NonNull C sender) {
        return (IndexCommandResult)this.query(HelpQuery.of(sender, ""));
    }
}

