/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

class AnnotatedWildcardTypeImpl
extends AnnotatedTypeImpl
implements AnnotatedWildcardType {
    private final AnnotatedType[] lowerBounds;
    private final AnnotatedType[] upperBounds;

    AnnotatedWildcardTypeImpl(WildcardType type, Annotation[] annotations, AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds) {
        super(type, annotations);
        if (lowerBounds == null || lowerBounds.length == 0) {
            lowerBounds = new AnnotatedType[]{};
        }
        if (upperBounds == null || upperBounds.length == 0) {
            upperBounds = new AnnotatedType[]{GenericTypeReflector.annotate(Object.class)};
        }
        AnnotatedWildcardTypeImpl.validateBounds(type, lowerBounds, upperBounds);
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    @Override
    public AnnotatedType[] getAnnotatedLowerBounds() {
        return this.lowerBounds;
    }

    @Override
    public AnnotatedType[] getAnnotatedUpperBounds() {
        return this.upperBounds;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AnnotatedWildcardType) || !super.equals(other)) {
            return false;
        }
        return GenericTypeReflector.typeArraysEqual(this.lowerBounds, ((AnnotatedWildcardType)other).getAnnotatedLowerBounds()) && GenericTypeReflector.typeArraysEqual(this.upperBounds, ((AnnotatedWildcardType)other).getAnnotatedUpperBounds());
    }

    @Override
    public int hashCode() {
        return 127 * super.hashCode() ^ GenericTypeReflector.hashCode(this.lowerBounds) + GenericTypeReflector.hashCode(this.upperBounds);
    }

    @Override
    public String toString() {
        if (this.lowerBounds.length > 0) {
            return this.annotationsString() + "? super " + this.typesString(this.lowerBounds);
        }
        if (this.upperBounds.length == 0 || this.upperBounds[0].getType() == Object.class) {
            return this.annotationsString() + "?";
        }
        return this.annotationsString() + "? extends " + this.typesString(this.upperBounds);
    }

    private static void validateBounds(WildcardType type, AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds) {
        int i;
        if (type.getLowerBounds().length != lowerBounds.length) {
            throw new IllegalArgumentException("Incompatible lower bounds " + Arrays.toString(lowerBounds) + " for type " + type);
        }
        if (type.getUpperBounds().length != upperBounds.length) {
            throw new IllegalArgumentException("Incompatible upper bounds " + Arrays.toString(upperBounds) + " for type " + type);
        }
        for (i = 0; i < type.getLowerBounds().length; ++i) {
            if (GenericTypeReflector.erase(type.getLowerBounds()[i]) == GenericTypeReflector.erase(lowerBounds[i].getType())) continue;
            throw new IllegalArgumentException("Bound " + lowerBounds[i].getType() + " incompatible with " + type.getLowerBounds()[i] + " in type " + type);
        }
        for (i = 0; i < type.getUpperBounds().length; ++i) {
            if (GenericTypeReflector.erase(type.getUpperBounds()[i]) == GenericTypeReflector.erase(upperBounds[i].getType())) continue;
            throw new IllegalArgumentException("Bound " + upperBounds[i].getType() + " incompatible with " + type.getUpperBounds()[i] + " in type " + type);
        }
    }
}

