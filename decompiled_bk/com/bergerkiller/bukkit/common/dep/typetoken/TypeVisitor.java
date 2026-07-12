/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedCaptureType;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedCaptureTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedTypeVariableImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedWildcardTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.CaptureType;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.WildcardTypeImpl;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class TypeVisitor {
    private final Map<TypeVariable, AnnotatedTypeVariable> varCache = new IdentityHashMap<TypeVariable, AnnotatedTypeVariable>();
    private final Map<AnnotatedCaptureCacheKey, AnnotatedType> captureCache = new HashMap<AnnotatedCaptureCacheKey, AnnotatedType>();

    protected AnnotatedType visitParameterizedType(AnnotatedParameterizedType type) {
        AnnotatedType[] params = (AnnotatedType[])Arrays.stream(type.getAnnotatedActualTypeArguments()).map(param -> GenericTypeReflector.transform(param, this)).toArray(AnnotatedType[]::new);
        return GenericTypeReflector.replaceParameters(type, params);
    }

    protected AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
        Type[] typeArray;
        AnnotatedType[] lowerBounds = (AnnotatedType[])Arrays.stream(type.getAnnotatedLowerBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(AnnotatedType[]::new);
        AnnotatedType[] upperBounds = (AnnotatedType[])Arrays.stream(type.getAnnotatedUpperBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(AnnotatedType[]::new);
        if (upperBounds.length > 0) {
            typeArray = (Type[])Arrays.stream(upperBounds).map(AnnotatedType::getType).toArray(Type[]::new);
        } else {
            Type[] typeArray2 = new Type[1];
            typeArray = typeArray2;
            typeArray2[0] = Object.class;
        }
        WildcardTypeImpl inner = new WildcardTypeImpl(typeArray, (Type[])Arrays.stream(lowerBounds).map(AnnotatedType::getType).toArray(Type[]::new));
        return new AnnotatedWildcardTypeImpl(inner, type.getAnnotations(), lowerBounds, upperBounds);
    }

    protected AnnotatedType visitVariable(AnnotatedTypeVariable type) {
        TypeVariable var = (TypeVariable)type.getType();
        if (this.varCache.containsKey(var)) {
            return this.varCache.get(var);
        }
        AnnotatedTypeVariableImpl variable = new AnnotatedTypeVariableImpl(var, type.getAnnotations());
        this.varCache.put(var, variable);
        AnnotatedType[] bounds = (AnnotatedType[])Arrays.stream(type.getAnnotatedBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(AnnotatedType[]::new);
        variable.init(bounds);
        return variable;
    }

    protected AnnotatedType visitArray(AnnotatedArrayType type) {
        AnnotatedType componentType = GenericTypeReflector.transform(type.getAnnotatedGenericComponentType(), this);
        return new AnnotatedArrayTypeImpl(GenericArrayTypeImpl.createArrayType(componentType.getType()), type.getAnnotations(), componentType);
    }

    protected AnnotatedType visitCaptureType(AnnotatedCaptureType type) {
        AnnotatedCaptureCacheKey key = new AnnotatedCaptureCacheKey(type);
        if (this.captureCache.containsKey(key)) {
            return this.captureCache.get(key);
        }
        AnnotatedType[] lowerBounds = type.getAnnotatedLowerBounds();
        if (lowerBounds != null) {
            lowerBounds = (AnnotatedType[])Arrays.stream(lowerBounds).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(AnnotatedType[]::new);
        }
        AnnotatedCaptureTypeImpl annotatedCapture = new AnnotatedCaptureTypeImpl((CaptureType)type.getType(), type.getAnnotatedWildcardType(), type.getAnnotatedTypeVariable(), lowerBounds, null, type.getAnnotations());
        this.captureCache.put(key, annotatedCapture);
        AnnotatedType[] upperBounds = (AnnotatedType[])Arrays.stream(type.getAnnotatedUpperBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(AnnotatedType[]::new);
        annotatedCapture.setAnnotatedUpperBounds(upperBounds);
        return annotatedCapture;
    }

    protected AnnotatedType visitClass(AnnotatedType type) {
        return type;
    }

    protected AnnotatedType visitUnmatched(AnnotatedType type) {
        return type;
    }

    private static class AnnotatedCaptureCacheKey {
        AnnotatedCaptureType capture;
        CaptureType raw;

        AnnotatedCaptureCacheKey(AnnotatedCaptureType capture) {
            this.capture = capture;
            this.raw = (CaptureType)capture.getType();
        }

        public int hashCode() {
            return 127 * this.raw.getWildcardType().hashCode() ^ this.raw.getTypeVariable().hashCode() ^ GenericTypeReflector.hashCode(Arrays.stream(this.capture.getAnnotations()));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AnnotatedCaptureCacheKey)) {
                return false;
            }
            AnnotatedCaptureCacheKey that = (AnnotatedCaptureCacheKey)obj;
            return this.capture == that.capture || new GenericTypeReflector.CaptureCacheKey(this.raw).equals(new GenericTypeReflector.CaptureCacheKey(that.raw)) && Arrays.equals(this.capture.getAnnotations(), that.capture.getAnnotations());
        }
    }
}

