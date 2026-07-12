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

import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotatedElementAccessor;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.MultiDelegateAnnotationAccessor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public interface AnnotationAccessor {
    @API(status=API.Status.STABLE)
    public static @NonNull AnnotationAccessor empty() {
        return new NullAnnotationAccessor();
    }

    public static @NonNull AnnotationAccessor of(@NonNull AnnotatedElement element) {
        return new AnnotatedElementAccessor(element);
    }

    @API(status=API.Status.STABLE)
    public static @NonNull AnnotationAccessor of(AnnotationAccessor ... accessors) {
        return new MultiDelegateAnnotationAccessor(accessors);
    }

    public <A extends Annotation> @Nullable A annotation(@NonNull Class<A> var1);

    public @NonNull Collection<@NonNull Annotation> annotations();

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public static final class NullAnnotationAccessor
    implements AnnotationAccessor {
        @Override
        public <A extends Annotation> @Nullable A annotation(@NonNull Class<A> clazz) {
            return null;
        }

        @Override
        public @NonNull Collection<@NonNull Annotation> annotations() {
            return Collections.emptyList();
        }
    }
}

