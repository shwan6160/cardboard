/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public interface CaptureType
extends Type {
    public Type[] getUpperBounds();

    public void setUpperBounds(Type[] var1);

    public Type[] getLowerBounds();

    public TypeVariable<?> getTypeVariable();

    public WildcardType getWildcardType();
}

