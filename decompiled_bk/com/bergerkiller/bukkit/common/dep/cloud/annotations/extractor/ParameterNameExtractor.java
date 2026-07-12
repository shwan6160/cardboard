/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import java.lang.reflect.Parameter;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface ParameterNameExtractor {
    public static @NonNull ParameterNameExtractor simple() {
        return Parameter::getName;
    }

    public static @NonNull ParameterNameExtractor withTransformation(@NonNull Function<String, String> transformation) {
        return parameter -> (String)transformation.apply(parameter.getName());
    }

    public @NonNull String extract(@NonNull Parameter var1);
}

