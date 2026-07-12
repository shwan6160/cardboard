/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.PipelineException;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServiceRepository;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

enum ServiceFilterHandler {
    INSTANCE;


    <Context> boolean passes(@NonNull ServiceRepository.ServiceWrapper<? extends Service<Context, ?>> service, @NonNull Context context) {
        if (!service.isDefaultImplementation()) {
            for (Predicate predicate : service.filters()) {
                try {
                    if (predicate.test(context)) continue;
                    return false;
                }
                catch (Exception e) {
                    throw new PipelineException(String.format("Failed to evaluate filter '%s' for '%s'", TypeToken.get(predicate.getClass()).getType().getTypeName(), service), e);
                }
            }
        }
        return true;
    }
}

