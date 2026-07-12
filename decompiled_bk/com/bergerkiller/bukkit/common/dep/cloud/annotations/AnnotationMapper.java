/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import java.lang.annotation.Annotation;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
@FunctionalInterface
public interface AnnotationMapper<A extends Annotation> {
    public @NonNull ParserParameters mapAnnotation(@NonNull A var1);
}

