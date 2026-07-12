/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.ComponentPreprocessor;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface PreprocessorHolder<C> {
    public @NonNull Collection<ComponentPreprocessor<C>> preprocessors();
}

