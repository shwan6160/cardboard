/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePipeline;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServiceSpigot;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ServicePump<Context> {
    private final ServicePipeline servicePipeline;
    private final Context context;

    ServicePump(@NonNull ServicePipeline servicePipeline, @NonNull Context context) {
        this.servicePipeline = servicePipeline;
        this.context = context;
    }

    public <Result> @NonNull ServiceSpigot<@NonNull Context, @NonNull Result> through(@NonNull TypeToken<? extends Service<@NonNull Context, @NonNull Result>> type) {
        return new ServiceSpigot(this.servicePipeline, this.context, type);
    }

    public <Result> @NonNull ServiceSpigot<@NonNull Context, @NonNull Result> through(@NonNull Class<? extends Service<@NonNull Context, @NonNull Result>> clazz) {
        return this.through(TypeToken.get(clazz));
    }
}

