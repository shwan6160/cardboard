/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.immutables.annotate.InjectAnnotation
 *  org.immutables.annotate.InjectAnnotation$Where
 *  org.immutables.value.Value$Style
 *  org.immutables.value.Value$Style$BuilderVisibility
 */
package com.bergerkiller.bukkit.common.dep.cloud.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.immutables.annotate.InjectAnnotation;
import org.immutables.value.Value;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.SOURCE)
@API(status=API.Status.INTERNAL)
@Value.Style(typeImmutableEnclosing="*", typeAbstract={"*"}, deferCollectionAllocation=true, optionalAcceptNullable=true, jdkOnly=true, allParameters=true, headerComments=true, jacksonIntegration=false, builderVisibility=Value.Style.BuilderVisibility.SAME, defaultAsDefault=true, depluralize=true)
@InjectAnnotation(type=API.class, target={InjectAnnotation.Where.IMMUTABLE_TYPE}, code="(status = org.apiguardian.api.API.Status.STABLE, consumers = \"org.incendo.cloud.*\")")
public @interface ImmutableBuilder {
}

