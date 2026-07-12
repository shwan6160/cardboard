/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface FlagExtractor {
    public @NonNull Collection<@NonNull FlagDescriptor> extractFlags(@NonNull Method var1);
}

