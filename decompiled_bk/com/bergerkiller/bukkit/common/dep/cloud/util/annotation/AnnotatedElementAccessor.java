/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.util.annotation;

import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
final class AnnotatedElementAccessor
implements AnnotationAccessor {
    private final AnnotatedElement element;

    AnnotatedElementAccessor(@NonNull AnnotatedElement element) {
        this.element = Objects.requireNonNull(element, "Method may not be null");
    }

    @Override
    public <A extends Annotation> @Nullable A annotation(@NonNull Class<A> clazz) {
        return this.element.getAnnotation(clazz);
    }

    @Override
    public @NonNull Collection<@NonNull Annotation> annotations() {
        return Collections.unmodifiableCollection(Arrays.asList(this.element.getAnnotations()));
    }
}

