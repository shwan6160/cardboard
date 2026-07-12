/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedParameterizedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotationFormatException;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotationInvocationHandler;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.ParameterizedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeArgumentNotInBoundException;
import com.bergerkiller.bukkit.common.dep.typetoken.VarMap;
import com.bergerkiller.bukkit.common.dep.typetoken.WildcardTypeImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class TypeFactory {
    private static final WildcardType UNBOUND_WILDCARD = new WildcardTypeImpl(new Type[]{Object.class}, new Type[0]);

    public static Type parameterizedClass(Class<?> clazz, Type ... arguments) {
        return TypeFactory.parameterizedInnerClass(null, clazz, arguments);
    }

    public static AnnotatedType annotatedClass(Class<?> clazz, Annotation[] annotations) {
        return TypeFactory.parameterizedAnnotatedClass(clazz, annotations, new AnnotatedType[0]);
    }

    public static AnnotatedType parameterizedAnnotatedClass(Class<?> clazz, Annotation[] annotations, AnnotatedType ... arguments) {
        return TypeFactory.parameterizedAnnotatedInnerClass(null, clazz, annotations, arguments);
    }

    public static AnnotatedType annotatedInnerClass(Type owner, Class<?> clazz, Annotation[] annotations) {
        return TypeFactory.parameterizedAnnotatedInnerClass(owner, clazz, annotations, new AnnotatedType[0]);
    }

    public static AnnotatedType parameterizedAnnotatedInnerClass(Type owner, Class<?> clazz, Annotation[] annotations, AnnotatedType ... arguments) {
        if (arguments == null || arguments.length == 0) {
            return GenericTypeReflector.annotate(clazz, annotations);
        }
        Type[] typeArguments = (Type[])Arrays.stream(arguments).map(AnnotatedType::getType).toArray(Type[]::new);
        return new AnnotatedParameterizedTypeImpl((ParameterizedType)TypeFactory.parameterizedInnerClass(owner, clazz, typeArguments), annotations, arguments);
    }

    public static AnnotatedParameterizedType parameterizedAnnotatedType(ParameterizedType type, Annotation[] typeAnnotations, Annotation[] ... argumentAnnotations) {
        if (argumentAnnotations == null || argumentAnnotations.length == 0) {
            return (AnnotatedParameterizedType)GenericTypeReflector.annotate((Type)type, typeAnnotations);
        }
        AnnotatedType[] typeArguments = new AnnotatedType[type.getActualTypeArguments().length];
        for (int i = 0; i < typeArguments.length; ++i) {
            Annotation[] annotations = argumentAnnotations.length > i ? argumentAnnotations[i] : null;
            typeArguments[i] = GenericTypeReflector.annotate(type.getActualTypeArguments()[i], annotations);
        }
        return (AnnotatedParameterizedType)TypeFactory.parameterizedAnnotatedClass(GenericTypeReflector.erase(type), typeAnnotations, typeArguments);
    }

    public static Type innerClass(Type owner, Class<?> clazz) {
        return TypeFactory.parameterizedInnerClass(owner, clazz, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Type parameterizedInnerClass(Type owner, Class<?> clazz, Type ... arguments) {
        if (clazz.getDeclaringClass() == null && owner != null) {
            throw new IllegalArgumentException("Cannot specify an owner type for a top level class");
        }
        Type realOwner = TypeFactory.transformOwner(owner, clazz);
        if (arguments == null) {
            if (clazz.getTypeParameters().length != 0) return clazz;
            arguments = new Type[]{};
        } else if (arguments.length != clazz.getTypeParameters().length) {
            throw new IllegalArgumentException("Incorrect number of type arguments for [" + clazz + "]: expected " + clazz.getTypeParameters().length + ", but got " + arguments.length);
        }
        if (!GenericTypeReflector.isMissingTypeParameters(clazz)) {
            return clazz;
        }
        if (realOwner != null && !Modifier.isStatic(clazz.getModifiers()) && GenericTypeReflector.isMissingTypeParameters(realOwner)) {
            return clazz;
        }
        ParameterizedTypeImpl result = new ParameterizedTypeImpl(clazz, arguments, realOwner);
        TypeFactory.checkParametersWithinBound(result);
        return result;
    }

    private static void checkParametersWithinBound(ParameterizedType type) {
        Type[] arguments = type.getActualTypeArguments();
        TypeVariable<Class<T>>[] typeParameters = ((Class)type.getRawType()).getTypeParameters();
        VarMap varMap = new VarMap(type);
        for (int i = 0; i < arguments.length; ++i) {
            for (Type bound : typeParameters[i].getBounds()) {
                Type replacedBound = varMap.map(bound);
                if (arguments[i] instanceof WildcardType) {
                    WildcardType wildcardTypeParameter = (WildcardType)arguments[i];
                    for (Type wildcardUpperBound : wildcardTypeParameter.getUpperBounds()) {
                        if (TypeFactory.couldHaveCommonSubtype(replacedBound, wildcardUpperBound)) continue;
                        throw new TypeArgumentNotInBoundException(arguments[i], typeParameters[i], bound);
                    }
                    for (Type wildcardLowerBound : wildcardTypeParameter.getLowerBounds()) {
                        if (GenericTypeReflector.isSuperType(replacedBound, wildcardLowerBound)) continue;
                        throw new TypeArgumentNotInBoundException(arguments[i], typeParameters[i], bound);
                    }
                    continue;
                }
                if (GenericTypeReflector.isSuperType(replacedBound, arguments[i])) continue;
                throw new TypeArgumentNotInBoundException(arguments[i], typeParameters[i], bound);
            }
        }
    }

    private static boolean couldHaveCommonSubtype(Type type1, Type type2) {
        Class<?> erased1 = GenericTypeReflector.erase(type1);
        Class<?> erased2 = GenericTypeReflector.erase(type2);
        return erased1.isInterface() || erased2.isInterface() || erased1.isAssignableFrom(erased2) || erased2.isAssignableFrom(erased1);
    }

    private static Type transformOwner(Type givenOwner, Class<?> clazz) {
        if (givenOwner == null) {
            return clazz.getDeclaringClass();
        }
        Type transformedOwner = GenericTypeReflector.getExactSuperType(GenericTypeReflector.annotate(givenOwner).getType(), clazz.getDeclaringClass());
        if (transformedOwner == null) {
            throw new IllegalArgumentException("Given owner type [" + givenOwner + "] is not appropriate for [" + clazz + "]: it should be a subtype of " + clazz.getDeclaringClass());
        }
        if (Modifier.isStatic(clazz.getModifiers())) {
            return GenericTypeReflector.erase(transformedOwner);
        }
        return transformedOwner;
    }

    public static WildcardType unboundWildcard() {
        return UNBOUND_WILDCARD;
    }

    public static WildcardType wildcardExtends(Type upperBound) {
        if (upperBound == null) {
            throw new NullPointerException();
        }
        return new WildcardTypeImpl(new Type[]{upperBound}, new Type[0]);
    }

    public static WildcardType wildcardSuper(Type lowerBound) {
        if (lowerBound == null) {
            throw new NullPointerException();
        }
        return new WildcardTypeImpl(new Type[]{Object.class}, new Type[]{lowerBound});
    }

    public static Type arrayOf(Type componentType) {
        return GenericArrayTypeImpl.createArrayType(componentType);
    }

    public static AnnotatedArrayType arrayOf(AnnotatedType componentType, Annotation[] annotations) {
        return AnnotatedArrayTypeImpl.createArrayType(componentType, annotations);
    }

    public static <A extends Annotation> A annotation(Class<A> annotationType, Map<String, Object> values) throws AnnotationFormatException {
        return (A)((Annotation)Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, new AnnotationInvocationHandler(annotationType, values == null ? Collections.emptyMap() : values)));
    }
}

