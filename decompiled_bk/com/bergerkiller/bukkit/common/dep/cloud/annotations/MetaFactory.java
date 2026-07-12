/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import java.lang.reflect.Method;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

class MetaFactory
implements Function<Method, CommandMeta> {
    private final AnnotationParser<?> annotationParser;
    private final Function<ParserParameters, CommandMeta> metaMapper;

    MetaFactory(@NonNull AnnotationParser<?> annotationParser, @NonNull Function<@NonNull ParserParameters, @NonNull CommandMeta> metaMapper) {
        this.annotationParser = annotationParser;
        this.metaMapper = metaMapper;
    }

    @Override
    public @NonNull CommandMeta apply(@NonNull Method method) {
        ParserParameters parameters = ParserParameters.empty();
        this.annotationParser.annotationMappers().forEach((annotationClass, mapper) -> {
            Object annotation = AnnotationParser.getMethodOrClassAnnotation(method, annotationClass);
            if (annotation != null) {
                parameters.merge(mapper.mapAnnotation(annotation));
            }
        });
        return this.metaMapper.apply(parameters);
    }
}

