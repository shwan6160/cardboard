/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.help.result;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpRenderer;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface HelpQueryResult<C> {
    public @NonNull HelpQuery<C> query();

    default public void render(@NonNull HelpRenderer<C> renderer) {
        renderer.render(this);
    }
}

