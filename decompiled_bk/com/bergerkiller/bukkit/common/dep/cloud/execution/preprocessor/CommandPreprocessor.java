/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.ConsumerService;
import org.apiguardian.api.API;

@API(status=API.Status.STABLE)
public interface CommandPreprocessor<C>
extends ConsumerService<CommandPreprocessingContext<C>> {
}

