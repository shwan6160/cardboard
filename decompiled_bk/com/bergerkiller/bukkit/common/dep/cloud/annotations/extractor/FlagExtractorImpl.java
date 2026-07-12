/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DescriptionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ImmutableFlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.FlagExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.annotations.*"})
public final class FlagExtractorImpl
implements FlagExtractor {
    private final AnnotationParser<?> annotationParser;
    private final DescriptionMapper descriptionMapper;

    private static @Nullable String nullIfEmpty(@NonNull String string) {
        if (string.isEmpty()) {
            return null;
        }
        return string;
    }

    public FlagExtractorImpl(@NonNull AnnotationParser<?> annotationParser) {
        this.annotationParser = annotationParser;
        this.descriptionMapper = this.annotationParser::mapDescription;
    }

    @Override
    public @NonNull Collection<@NonNull FlagDescriptor> extractFlags(@NonNull Method method) {
        LinkedList<FlagDescriptor> flags = new LinkedList<FlagDescriptor>();
        for (Parameter parameter : method.getParameters()) {
            if (!parameter.isAnnotationPresent(Flag.class)) continue;
            Flag flag = parameter.getAnnotation(Flag.class);
            String flagName = this.annotationParser.processString(flag.value());
            String name = flagName.equals("__INFERRED_ARGUMENT_NAME__") ? parameter.getName() : flagName;
            ImmutableFlagDescriptor flagDescriptor = FlagDescriptor.builder().parameter(parameter).name(name).description(this.descriptionMapper.map(flag.description())).aliases(this.annotationParser.processStrings(Arrays.asList(flag.aliases()))).permission(Permission.of(this.annotationParser.processString(flag.permission()))).repeatable(flag.repeatable()).parserName(FlagExtractorImpl.nullIfEmpty(this.annotationParser.processString(flag.parserName()))).suggestions(FlagExtractorImpl.nullIfEmpty(this.annotationParser.processString(flag.suggestions()))).build();
            flags.add(flagDescriptor);
        }
        return flags;
    }
}

