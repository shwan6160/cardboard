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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
final class MultiDelegateAnnotationAccessor
implements AnnotationAccessor {
    private final AnnotationAccessor[] accessors;

    MultiDelegateAnnotationAccessor(AnnotationAccessor ... accessors) {
        this.accessors = accessors;
    }

    @Override
    public <A extends Annotation> @Nullable A annotation(@NonNull Class<A> clazz) {
        AnnotationAccessor annotationAccessor;
        A instance = null;
        AnnotationAccessor[] annotationAccessorArray = this.accessors;
        int n = annotationAccessorArray.length;
        for (int i = 0; i < n && (instance = (A)(annotationAccessor = annotationAccessorArray[i]).annotation(clazz)) == null; ++i) {
        }
        return instance;
    }

    @Override
    public @NonNull Collection<@NonNull Annotation> annotations() {
        LinkedList<Annotation> annotationList = new LinkedList<Annotation>();
        for (AnnotationAccessor annotationAccessor : this.accessors) {
            annotationList.addAll(annotationAccessor.annotations());
        }
        return Collections.unmodifiableCollection(annotationList);
    }
}

