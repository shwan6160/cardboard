/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessor;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class AcceptingCommandPreprocessor<C>
implements CommandPreprocessor<C> {
    public static final String PROCESSED_INDICATOR_KEY = "__COMMAND_PRE_PROCESSED__";

    @Override
    public void accept(@NonNull CommandPreprocessingContext<C> context) {
        context.commandContext().store(PROCESSED_INDICATOR_KEY, "true");
    }
}

