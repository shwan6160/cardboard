/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.injection;

import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionRequest;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import org.apiguardian.api.API;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface InjectionService<C>
extends Service<InjectionRequest<C>, Object> {
}

