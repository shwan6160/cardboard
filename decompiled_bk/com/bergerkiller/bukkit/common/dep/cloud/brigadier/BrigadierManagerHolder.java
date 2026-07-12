/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE, since="2.0.0")
public interface BrigadierManagerHolder<C, S> {
    @API(status=API.Status.STABLE, since="2.0.0")
    public boolean hasBrigadierManager();

    @API(status=API.Status.STABLE, since="2.0.0")
    public @NonNull CloudBrigadierManager<C, ? extends S> brigadierManager();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static final class BrigadierManagerNotPresent
    extends RuntimeException {
        public BrigadierManagerNotPresent(String message) {
            super(message);
        }
    }
}

