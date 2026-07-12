/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, since="2.0.0")
public interface CompletionMapper {
    public // Could not load outer class - annotation placement on inner may be incorrect
     @NonNull AsyncTabCompleteEvent.Completion map(@NonNull TooltipSuggestion var1);
}

