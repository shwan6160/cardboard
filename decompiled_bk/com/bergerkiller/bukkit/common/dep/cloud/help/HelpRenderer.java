/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.help.result.HelpQueryResult;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface HelpRenderer<C> {
    public void render(@NonNull HelpQueryResult<C> var1);
}

