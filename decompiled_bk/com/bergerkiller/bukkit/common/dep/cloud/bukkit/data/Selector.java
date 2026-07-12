/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.data;

import java.util.Collection;
import java.util.Collections;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE, since="2.0.0")
public interface Selector<V> {
    public @NonNull String inputString();

    public @NonNull Collection<V> values();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static interface Single<V>
    extends Selector<V> {
        @Override
        default public @NonNull Collection<V> values() {
            return Collections.singletonList(this.single());
        }

        public @NonNull V single();
    }
}

