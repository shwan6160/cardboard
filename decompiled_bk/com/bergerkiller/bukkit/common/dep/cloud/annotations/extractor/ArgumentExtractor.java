/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface ArgumentExtractor {
    public @NonNull Collection<@NonNull ArgumentDescriptor> extractArguments(@NonNull List<@NonNull SyntaxFragment> var1, @NonNull Method var2);
}

