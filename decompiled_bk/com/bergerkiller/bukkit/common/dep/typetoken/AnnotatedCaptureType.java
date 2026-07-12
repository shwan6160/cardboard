/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;

public interface AnnotatedCaptureType
extends AnnotatedType {
    public AnnotatedType[] getAnnotatedUpperBounds();

    public AnnotatedType[] getAnnotatedLowerBounds();

    public AnnotatedTypeVariable getAnnotatedTypeVariable();

    public AnnotatedWildcardType getAnnotatedWildcardType();

    public void setAnnotatedUpperBounds(AnnotatedType[] var1);
}

