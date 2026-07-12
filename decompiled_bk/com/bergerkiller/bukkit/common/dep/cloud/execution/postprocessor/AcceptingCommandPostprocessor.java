/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessor;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class AcceptingCommandPostprocessor<C>
implements CommandPostprocessor<C> {
    public static final String PROCESSED_INDICATOR_KEY = "__COMMAND_POST_PROCESSED__";

    @Override
    public void accept(@NonNull CommandPostprocessingContext<C> context) {
        context.commandContext().store(PROCESSED_INDICATOR_KEY, "true");
    }
}

