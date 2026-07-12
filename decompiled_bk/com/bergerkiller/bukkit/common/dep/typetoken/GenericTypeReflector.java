/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedCaptureType;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedCaptureTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedParameterizedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedTypeVariableImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedWildcardTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.CaptureType;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.ParameterizedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeFactory;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeVisitor;
import com.bergerkiller.bukkit.common.dep.typetoken.UnresolvedTypeVariableException;
import com.bergerkiller.bukkit.common.dep.typetoken.VarMap;
import com.bergerkiller.bukkit.common.dep.typetoken.WildcardTypeImpl;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class GenericTypeReflector {
    private static final WildcardType UNBOUND_WILDCARD = new WildcardTypeImpl(new Type[]{Object.class}, new Type[0]);
    private static final Map<Class<?>, Class<?>> BOX_TYPES;

    public static Class<?> erase(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
        }
        if (type instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable)type;
            if (tv.getBounds().length == 0) {
                return Object.class;
            }
            return GenericTypeReflector.erase(tv.getBounds()[0]);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType aType = (GenericArrayType)type;
            return GenericArrayTypeImpl.createArrayType(GenericTypeReflector.erase(aType.getGenericComponentType()));
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            Type[] lowerBounds = wildcardType.getLowerBounds();
            return GenericTypeReflector.erase(lowerBounds.length > 0 ? lowerBounds[0] : wildcardType.getUpperBounds()[0]);
        }
        if (type instanceof CaptureType) {
            CaptureType captureType = (CaptureType)type;
            Type[] lowerBounds = captureType.getLowerBounds();
            return GenericTypeReflector.erase(lowerBounds.length > 0 ? lowerBounds[0] : captureType.getUpperBounds()[0]);
        }
        throw new RuntimeException("not supported: " + type.getClass());
    }

    public static Type box(Type type) {
        Class<?> boxed = BOX_TYPES.get(type);
        return boxed != null ? boxed : type;
    }

    public static boolean isBoxType(Type type) {
        return BOX_TYPES.containsValue(type);
    }

    public static boolean isFullyBound(Type type) {
        if (type instanceof Class) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return Arrays.stream(((ParameterizedType)type).getActualTypeArguments()).allMatch(GenericTypeReflector::isFullyBound);
        }
        if (type instanceof GenericArrayType) {
            return GenericTypeReflector.isFullyBound(((GenericArrayType)type).getGenericComponentType());
        }
        return false;
    }

    private static AnnotatedType mapTypeParameters(AnnotatedType toMapType, AnnotatedType typeAndParams) {
        return GenericTypeReflector.mapTypeParameters(toMapType, typeAndParams, VarMap.MappingMode.EXACT);
    }

    private static AnnotatedType mapTypeParameters(AnnotatedType toMapType, AnnotatedType typeAndParams, VarMap.MappingMode mappingMode) {
        if (GenericTypeReflector.isMissingTypeParameters(typeAndParams.getType())) {
            return new AnnotatedTypeImpl(GenericTypeReflector.erase(toMapType.getType()), toMapType.getAnnotations());
        }
        VarMap varMap = new VarMap();
        AnnotatedType handlingTypeAndParams = typeAndParams;
        while (handlingTypeAndParams instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType pType = (AnnotatedParameterizedType)handlingTypeAndParams;
            Class clazz = (Class)((ParameterizedType)pType.getType()).getRawType();
            TypeVariable[] vars = clazz.getTypeParameters();
            varMap.addAll(vars, pType.getAnnotatedActualTypeArguments());
            Type owner = ((ParameterizedType)pType.getType()).getOwnerType();
            handlingTypeAndParams = owner == null ? null : GenericTypeReflector.annotate(owner);
        }
        return varMap.map(toMapType, mappingMode);
    }

    public static AnnotatedType resolveExactType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
        return GenericTypeReflector.resolveType(unresolved, GenericTypeReflector.expandGenerics(typeAndParams), VarMap.MappingMode.EXACT);
    }

    public static Type resolveExactType(Type unresolved, Type typeAndParams) {
        return GenericTypeReflector.resolveType(GenericTypeReflector.annotate(unresolved), GenericTypeReflector.annotate(typeAndParams, true), VarMap.MappingMode.EXACT).getType();
    }

    public static AnnotatedType resolveType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
        return GenericTypeReflector.resolveType(unresolved, GenericTypeReflector.expandGenerics(typeAndParams), VarMap.MappingMode.ALLOW_INCOMPLETE);
    }

    public static Type resolveType(Type unresolved, Type typeAndParams) {
        return GenericTypeReflector.resolveType(GenericTypeReflector.annotate(unresolved), GenericTypeReflector.annotate(typeAndParams, true), VarMap.MappingMode.ALLOW_INCOMPLETE).getType();
    }

    private static AnnotatedType resolveType(AnnotatedType unresolved, AnnotatedType typeAndParams, VarMap.MappingMode mappingMode) {
        if (unresolved instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType)unresolved;
            AnnotatedType[] params = GenericTypeReflector.mapArray(parameterizedType.getAnnotatedActualTypeArguments(), AnnotatedType[]::new, p -> GenericTypeReflector.resolveType(p, typeAndParams, mappingMode));
            return GenericTypeReflector.replaceParameters(parameterizedType, params);
        }
        if (unresolved instanceof AnnotatedWildcardType) {
            AnnotatedType[] lower = GenericTypeReflector.mapArray(((AnnotatedWildcardType)unresolved).getAnnotatedLowerBounds(), AnnotatedType[]::new, b -> GenericTypeReflector.resolveType(b, typeAndParams, mappingMode));
            AnnotatedType[] upper = GenericTypeReflector.mapArray(((AnnotatedWildcardType)unresolved).getAnnotatedUpperBounds(), AnnotatedType[]::new, b -> GenericTypeReflector.resolveType(b, typeAndParams, mappingMode));
            return new AnnotatedWildcardTypeImpl((WildcardType)unresolved.getType(), unresolved.getAnnotations(), lower, upper);
        }
        if (unresolved instanceof AnnotatedTypeVariable) {
            AnnotatedType resolved;
            TypeVariable var = (TypeVariable)unresolved.getType();
            if (var.getGenericDeclaration() instanceof Class && (resolved = GenericTypeReflector.getTypeParameter(typeAndParams, var)) != null) {
                return GenericTypeReflector.updateAnnotations(resolved, unresolved.getAnnotations());
            }
            if (mappingMode.equals((Object)VarMap.MappingMode.ALLOW_INCOMPLETE)) {
                return unresolved;
            }
            throw new IllegalArgumentException("Variable " + var.getName() + " is not declared by the given type " + typeAndParams.getType().getTypeName() + " or its super types");
        }
        if (unresolved instanceof AnnotatedArrayType) {
            AnnotatedType componentType = GenericTypeReflector.resolveType(((AnnotatedArrayType)unresolved).getAnnotatedGenericComponentType(), typeAndParams, mappingMode);
            return new AnnotatedArrayTypeImpl(TypeFactory.arrayOf(componentType.getType()), unresolved.getAnnotations(), componentType);
        }
        return unresolved;
    }

    public static boolean isMissingTypeParameters(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>)type;
            if (Modifier.isStatic(clazz.getModifiers())) {
                return clazz.getTypeParameters().length != 0;
            }
            for (Class<?> enclosing = clazz; enclosing != null; enclosing = enclosing.getEnclosingClass()) {
                if (enclosing.getTypeParameters().length == 0) continue;
                return true;
            }
            return false;
        }
        if (type instanceof ParameterizedType) {
            return false;
        }
        throw new AssertionError((Object)("Unexpected type " + type.getClass()));
    }

    public static Type addWildcardParameters(Class<?> clazz) {
        if (clazz.isArray()) {
            return GenericArrayTypeImpl.createArrayType(GenericTypeReflector.addWildcardParameters(clazz.getComponentType()));
        }
        if (GenericTypeReflector.isMissingTypeParameters(clazz)) {
            TypeVariable<Class<?>>[] vars = clazz.getTypeParameters();
            Object[] arguments = new Type[vars.length];
            Arrays.fill(arguments, UNBOUND_WILDCARD);
            Type owner = clazz.getDeclaringClass() == null ? null : GenericTypeReflector.addWildcardParameters(clazz.getDeclaringClass());
            return new ParameterizedTypeImpl(clazz, (Type[])arguments, owner);
        }
        return clazz;
    }

    public static AnnotatedType getExactSuperType(AnnotatedType subType, Class<?> searchSuperClass) {
        if (subType instanceof AnnotatedParameterizedType || subType.getType() instanceof Class || subType instanceof AnnotatedArrayType) {
            Class<?> superClass = GenericTypeReflector.erase(subType.getType());
            if (searchSuperClass == superClass) {
                return subType;
            }
            if (!searchSuperClass.isAssignableFrom(superClass)) {
                return null;
            }
        }
        for (AnnotatedType superType : GenericTypeReflector.getExactDirectSuperTypes(subType)) {
            AnnotatedType result = GenericTypeReflector.getExactSuperType(superType, searchSuperClass);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public static Type getExactSuperType(Type subType, Class<?> searchSuperClass) {
        AnnotatedType superType = GenericTypeReflector.getExactSuperType(GenericTypeReflector.annotate(subType), searchSuperClass);
        return superType == null ? null : superType.getType();
    }

    public static AnnotatedType getExactSubType(AnnotatedType superType, Class<?> searchSubClass) {
        Type subType = searchSubClass;
        if (searchSubClass.getTypeParameters().length > 0) {
            subType = TypeFactory.parameterizedClass(searchSubClass, searchSubClass.getTypeParameters());
        }
        AnnotatedType annotatedSubType = GenericTypeReflector.annotate(subType);
        Class<?> rawSuperType = GenericTypeReflector.erase(superType.getType());
        if (searchSubClass.isArray() && superType instanceof AnnotatedArrayType) {
            if (rawSuperType.isAssignableFrom(searchSubClass)) {
                return AnnotatedArrayTypeImpl.createArrayType(GenericTypeReflector.getExactSubType(((AnnotatedArrayType)superType).getAnnotatedGenericComponentType(), searchSubClass.getComponentType()), new Annotation[0]);
            }
            return null;
        }
        if (searchSubClass.getTypeParameters().length == 0) {
            return annotatedSubType;
        }
        if (!(superType instanceof AnnotatedParameterizedType)) {
            return GenericTypeReflector.annotate(searchSubClass);
        }
        AnnotatedParameterizedType parameterizedSuperType = (AnnotatedParameterizedType)superType;
        AnnotatedParameterizedType matched = (AnnotatedParameterizedType)GenericTypeReflector.getExactSuperType(annotatedSubType, rawSuperType);
        if (matched == null) {
            return null;
        }
        VarMap varMap = new VarMap();
        try {
            GenericTypeReflector.extractVariables(parameterizedSuperType, matched, searchSubClass, varMap);
            return varMap.map(annotatedSubType);
        }
        catch (UnresolvedTypeVariableException e) {
            return GenericTypeReflector.annotate(searchSubClass);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Type getExactSubType(Type superType, Class<?> searchSubClass) {
        AnnotatedType resolvedSubtype = GenericTypeReflector.getExactSubType(GenericTypeReflector.annotate(superType), searchSubClass);
        return resolvedSubtype == null ? null : resolvedSubtype.getType();
    }

    public static AnnotatedType getTypeParameter(AnnotatedType type, TypeVariable<? extends Class<?>> variable) {
        Class<?> clazz = variable.getGenericDeclaration();
        AnnotatedType superType = GenericTypeReflector.getExactSuperType(type, clazz);
        if (superType instanceof AnnotatedParameterizedType) {
            int index = Arrays.asList(clazz.getTypeParameters()).indexOf(variable);
            AnnotatedType resolvedVarType = ((AnnotatedParameterizedType)superType).getAnnotatedActualTypeArguments()[index];
            return GenericTypeReflector.updateAnnotations(resolvedVarType, variable.getAnnotations());
        }
        return null;
    }

    public static Type getTypeParameter(Type type, TypeVariable<? extends Class<?>> variable) {
        AnnotatedType typeParameter = GenericTypeReflector.getTypeParameter(GenericTypeReflector.annotate(type), variable);
        return typeParameter == null ? null : typeParameter.getType();
    }

    public static boolean isSuperType(Type superType, Type subType) {
        if (superType instanceof ParameterizedType || superType instanceof Class || superType instanceof GenericArrayType) {
            Type mappedSubType;
            Class<?> superClass = GenericTypeReflector.erase(superType);
            AnnotatedType annotatedMappedSubType = GenericTypeReflector.getExactSuperType(GenericTypeReflector.capture(GenericTypeReflector.annotate(subType)), superClass);
            Type type = mappedSubType = annotatedMappedSubType == null ? null : annotatedMappedSubType.getType();
            if (mappedSubType == null) {
                return false;
            }
            if (superType instanceof Class) {
                return true;
            }
            if (mappedSubType instanceof Class) {
                return true;
            }
            if (mappedSubType instanceof GenericArrayType) {
                Type superComponentType = GenericTypeReflector.getArrayComponentType(superType);
                assert (superComponentType != null);
                Type mappedSubComponentType = GenericTypeReflector.getArrayComponentType(mappedSubType);
                assert (mappedSubComponentType != null);
                return GenericTypeReflector.isSuperType(superComponentType, mappedSubComponentType);
            }
            assert (mappedSubType instanceof ParameterizedType);
            assert (superType instanceof ParameterizedType);
            ParameterizedType pMappedSubType = (ParameterizedType)mappedSubType;
            assert (pMappedSubType.getRawType() == superClass);
            ParameterizedType pSuperType = (ParameterizedType)superType;
            Type[] superTypeArgs = pSuperType.getActualTypeArguments();
            Type[] subTypeArgs = pMappedSubType.getActualTypeArguments();
            assert (superTypeArgs.length == subTypeArgs.length);
            for (int i = 0; i < superTypeArgs.length; ++i) {
                if (GenericTypeReflector.contains(superTypeArgs[i], subTypeArgs[i])) continue;
                return false;
            }
            return pSuperType.getOwnerType() == null || GenericTypeReflector.isSuperType(pSuperType.getOwnerType(), pMappedSubType.getOwnerType());
        }
        if (superType instanceof CaptureType) {
            if (superType.equals(subType)) {
                return true;
            }
            for (Type lowerBound : ((CaptureType)superType).getLowerBounds()) {
                if (!GenericTypeReflector.isSuperType(lowerBound, subType)) continue;
                return true;
            }
            return false;
        }
        throw new RuntimeException("Type not supported: " + superType.getClass());
    }

    private static boolean isArraySupertype(Type arraySuperType, Type subType) {
        Type superTypeComponent = GenericTypeReflector.getArrayComponentType(arraySuperType);
        assert (superTypeComponent != null);
        Type subTypeComponent = GenericTypeReflector.getArrayComponentType(subType);
        if (subTypeComponent == null) {
            return false;
        }
        return GenericTypeReflector.isSuperType(superTypeComponent, subTypeComponent);
    }

    public static AnnotatedType getArrayComponentType(AnnotatedType type) {
        if (type.getType() instanceof Class) {
            Class clazz = (Class)type.getType();
            return new AnnotatedTypeImpl(clazz.getComponentType(), clazz.getAnnotations());
        }
        if (type instanceof AnnotatedArrayType) {
            AnnotatedArrayType aType = (AnnotatedArrayType)type;
            return aType.getAnnotatedGenericComponentType();
        }
        return null;
    }

    public static Type getArrayComponentType(Type type) {
        AnnotatedType componentType = GenericTypeReflector.getArrayComponentType(GenericTypeReflector.annotate(type));
        return componentType == null ? null : componentType.getType();
    }

    private static boolean contains(Type containingType, Type containedType) {
        if (containingType instanceof WildcardType) {
            WildcardType wContainingType = (WildcardType)containingType;
            for (Type upperBound : wContainingType.getUpperBounds()) {
                if (GenericTypeReflector.isSuperType(upperBound, containedType)) continue;
                return false;
            }
            for (Type lowerBound : wContainingType.getLowerBounds()) {
                if (GenericTypeReflector.isSuperType(containedType, lowerBound)) continue;
                return false;
            }
            return true;
        }
        return containingType.equals(containedType);
    }

    private static void extractVariables(AnnotatedParameterizedType resolvedTyped, AnnotatedParameterizedType unresolvedType, Class<?> declaringClass, VarMap variables) {
        for (int i = 0; i < resolvedTyped.getAnnotatedActualTypeArguments().length; ++i) {
            AnnotatedType unresolvedParam = unresolvedType.getAnnotatedActualTypeArguments()[i];
            AnnotatedType resolvedParam = resolvedTyped.getAnnotatedActualTypeArguments()[i];
            Type var = unresolvedParam.getType();
            if (var instanceof TypeVariable && ((TypeVariable)var).getGenericDeclaration() == declaringClass) {
                variables.add((TypeVariable)var, resolvedParam);
                continue;
            }
            if (!(unresolvedParam instanceof AnnotatedParameterizedType)) continue;
            if (!(resolvedParam instanceof AnnotatedParameterizedType) || !GenericTypeReflector.erase(unresolvedParam.getType()).equals(GenericTypeReflector.erase(resolvedParam.getType()))) {
                throw new IllegalArgumentException("The provided types do not match in shape");
            }
            GenericTypeReflector.extractVariables((AnnotatedParameterizedType)resolvedParam, (AnnotatedParameterizedType)unresolvedParam, declaringClass, variables);
        }
    }

    private static AnnotatedType[] getExactDirectSuperTypes(AnnotatedType type) {
        if (type instanceof AnnotatedParameterizedType || type != null && type.getType() instanceof Class) {
            int resultIndex;
            AnnotatedType[] result;
            Class clazz;
            if (type instanceof AnnotatedParameterizedType) {
                clazz = (Class)((ParameterizedType)type.getType()).getRawType();
            } else {
                clazz = (Class)type.getType();
                if (clazz.isArray()) {
                    return GenericTypeReflector.getArrayExactDirectSuperTypes(GenericTypeReflector.annotate(clazz));
                }
            }
            AnnotatedType[] superInterfaces = clazz.getAnnotatedInterfaces();
            AnnotatedType superClass = clazz.getAnnotatedSuperclass();
            if (superClass == null && superInterfaces.length == 0 && clazz.isInterface()) {
                return new AnnotatedType[]{new AnnotatedTypeImpl((Type)((Object)Object.class))};
            }
            if (superClass == null) {
                result = new AnnotatedType[superInterfaces.length];
                resultIndex = 0;
            } else {
                result = new AnnotatedType[superInterfaces.length + 1];
                resultIndex = 1;
                result[0] = GenericTypeReflector.mapTypeParameters(superClass, type);
            }
            for (AnnotatedType superInterface : superInterfaces) {
                result[resultIndex++] = GenericTypeReflector.mapTypeParameters(superInterface, type);
            }
            return result;
        }
        if (type instanceof AnnotatedTypeVariable) {
            AnnotatedTypeVariable tv = (AnnotatedTypeVariable)type;
            return tv.getAnnotatedBounds();
        }
        if (type instanceof AnnotatedWildcardType) {
            return ((AnnotatedWildcardType)type).getAnnotatedUpperBounds();
        }
        if (type instanceof AnnotatedCaptureTypeImpl) {
            return ((AnnotatedCaptureTypeImpl)type).getAnnotatedUpperBounds();
        }
        if (type instanceof AnnotatedArrayType) {
            return GenericTypeReflector.getArrayExactDirectSuperTypes(type);
        }
        if (type == null) {
            throw new NullPointerException();
        }
        throw new RuntimeException("not implemented type: " + type);
    }

    private static AnnotatedType[] getArrayExactDirectSuperTypes(AnnotatedType arrayType) {
        AnnotatedType[] result;
        int resultIndex;
        AnnotatedType typeComponent = GenericTypeReflector.getArrayComponentType(arrayType);
        if (typeComponent != null && typeComponent.getType() instanceof Class && ((Class)typeComponent.getType()).isPrimitive()) {
            resultIndex = 0;
            result = new AnnotatedType[3];
        } else {
            AnnotatedType[] componentSupertypes = GenericTypeReflector.getExactDirectSuperTypes(typeComponent);
            result = new AnnotatedType[componentSupertypes.length + 3];
            for (resultIndex = 0; resultIndex < componentSupertypes.length; ++resultIndex) {
                result[resultIndex] = AnnotatedArrayTypeImpl.createArrayType(componentSupertypes[resultIndex], new Annotation[0]);
            }
        }
        result[resultIndex++] = new AnnotatedTypeImpl((Type)((Object)Object.class));
        result[resultIndex++] = new AnnotatedTypeImpl((Type)((Object)Cloneable.class));
        result[resultIndex++] = new AnnotatedTypeImpl((Type)((Object)Serializable.class));
        return result;
    }

    public static AnnotatedType getExactReturnType(Method m, AnnotatedType declaringType) {
        return GenericTypeReflector.getReturnType(m, declaringType, VarMap.MappingMode.EXACT);
    }

    public static Type getExactReturnType(Method m, Type declaringType) {
        return GenericTypeReflector.getExactReturnType(m, GenericTypeReflector.annotate(declaringType)).getType();
    }

    public static AnnotatedType getReturnType(Method m, AnnotatedType declaringType) {
        return GenericTypeReflector.getReturnType(m, declaringType, VarMap.MappingMode.ALLOW_INCOMPLETE);
    }

    public static Type getReturnType(Method m, Type declaringType) {
        return GenericTypeReflector.getReturnType(m, GenericTypeReflector.annotate(declaringType)).getType();
    }

    private static AnnotatedType getReturnType(Method m, AnnotatedType declaringType, VarMap.MappingMode mappingMode) {
        AnnotatedType returnType = m.getAnnotatedReturnType();
        AnnotatedType exactDeclaringType = GenericTypeReflector.getExactSuperType(GenericTypeReflector.capture(declaringType), m.getDeclaringClass());
        if (exactDeclaringType == null) {
            throw new IllegalArgumentException("The method " + m + " is not a member of type " + declaringType);
        }
        return GenericTypeReflector.mapTypeParameters(returnType, exactDeclaringType, mappingMode);
    }

    public static AnnotatedType getExactFieldType(Field f, AnnotatedType declaringType) {
        return GenericTypeReflector.getFieldType(f, declaringType, VarMap.MappingMode.EXACT);
    }

    public static Type getExactFieldType(Field f, Type type) {
        return GenericTypeReflector.getExactFieldType(f, GenericTypeReflector.annotate(type)).getType();
    }

    public static AnnotatedType getFieldType(Field f, AnnotatedType declaringType) {
        return GenericTypeReflector.getFieldType(f, declaringType, VarMap.MappingMode.ALLOW_INCOMPLETE);
    }

    public static Type getFieldType(Field f, Type type) {
        return GenericTypeReflector.getFieldType(f, GenericTypeReflector.annotate(type)).getType();
    }

    private static AnnotatedType getFieldType(Field f, AnnotatedType declaringType, VarMap.MappingMode mappingMode) {
        AnnotatedType returnType = f.getAnnotatedType();
        AnnotatedType exactDeclaringType = GenericTypeReflector.getExactSuperType(GenericTypeReflector.capture(declaringType), f.getDeclaringClass());
        if (exactDeclaringType == null) {
            throw new IllegalArgumentException("The field " + f + " is not a member of type " + declaringType);
        }
        return GenericTypeReflector.mapTypeParameters(returnType, exactDeclaringType, mappingMode);
    }

    public static AnnotatedType[] getExactParameterTypes(Executable exe, AnnotatedType declaringType) {
        return GenericTypeReflector.getParameterTypes(exe, declaringType, VarMap.MappingMode.EXACT);
    }

    public static Type[] getExactParameterTypes(Executable exe, Type declaringType) {
        return GenericTypeReflector.mapArray(GenericTypeReflector.getExactParameterTypes(exe, GenericTypeReflector.annotate(declaringType)), Type[]::new, AnnotatedType::getType);
    }

    public static AnnotatedType[] getParameterTypes(Executable exe, AnnotatedType declaringType) {
        return GenericTypeReflector.getParameterTypes(exe, declaringType, VarMap.MappingMode.ALLOW_INCOMPLETE);
    }

    public static Type[] getParameterTypes(Executable exe, Type declaringType) {
        return GenericTypeReflector.mapArray(GenericTypeReflector.getParameterTypes(exe, GenericTypeReflector.annotate(declaringType)), Type[]::new, AnnotatedType::getType);
    }

    private static AnnotatedType[] getParameterTypes(Executable exe, AnnotatedType declaringType, VarMap.MappingMode mappingMode) {
        AnnotatedType[] parameterTypes = exe.getAnnotatedParameterTypes();
        AnnotatedType exactDeclaringType = GenericTypeReflector.getExactSuperType(GenericTypeReflector.capture(declaringType), exe.getDeclaringClass());
        if (exactDeclaringType == null) {
            throw new IllegalArgumentException("The method/constructor " + exe + " is not a member of type " + declaringType);
        }
        AnnotatedType[] result = new AnnotatedType[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            result[i] = GenericTypeReflector.mapTypeParameters(parameterTypes[i], exactDeclaringType, mappingMode);
        }
        return result;
    }

    public static AnnotatedType capture(AnnotatedType type) {
        if (type instanceof AnnotatedParameterizedType) {
            return GenericTypeReflector.capture((AnnotatedParameterizedType)type);
        }
        return type;
    }

    public static AnnotatedParameterizedType capture(AnnotatedParameterizedType type) {
        VarMap varMap = new VarMap();
        ArrayList<AnnotatedCaptureTypeImpl> toInit = new ArrayList<AnnotatedCaptureTypeImpl>();
        Class clazz = (Class)((ParameterizedType)type.getType()).getRawType();
        AnnotatedType[] arguments = type.getAnnotatedActualTypeArguments();
        TypeVariable<Class<T>>[] vars = clazz.getTypeParameters();
        AnnotatedType[] capturedArguments = new AnnotatedType[arguments.length];
        assert (arguments.length == vars.length);
        for (int i = 0; i < arguments.length; ++i) {
            AnnotatedType argument = arguments[i];
            if (argument instanceof AnnotatedWildcardType) {
                AnnotatedCaptureTypeImpl captured = new AnnotatedCaptureTypeImpl((AnnotatedWildcardType)argument, new AnnotatedTypeVariableImpl(vars[i]));
                argument = captured;
                toInit.add(captured);
            }
            capturedArguments[i] = argument;
            varMap.add(vars[i], argument);
        }
        for (AnnotatedCaptureTypeImpl captured : toInit) {
            captured.init(varMap);
        }
        ParameterizedType inner = (ParameterizedType)type.getType();
        AnnotatedType ownerType = inner.getOwnerType() == null ? null : GenericTypeReflector.capture(GenericTypeReflector.annotate(inner.getOwnerType()));
        Type[] rawArgs = GenericTypeReflector.mapArray(capturedArguments, Type[]::new, AnnotatedType::getType);
        ParameterizedTypeImpl nn = new ParameterizedTypeImpl(clazz, rawArgs, ownerType == null ? null : ownerType.getType());
        return new AnnotatedParameterizedTypeImpl(nn, type.getAnnotations(), capturedArguments);
    }

    public static String getTypeName(Type type) {
        if (type instanceof Class) {
            Class clazz = (Class)type;
            return clazz.isArray() ? GenericTypeReflector.getTypeName(clazz.getComponentType()) + "[]" : clazz.getName();
        }
        return type.toString();
    }

    public static List<Class<?>> getUpperBoundClassAndInterfaces(Type type) {
        LinkedHashSet result = new LinkedHashSet();
        GenericTypeReflector.buildUpperBoundClassAndInterfaces(type, result);
        return new ArrayList(result);
    }

    private static AnnotatedType annotate(Type type, boolean expandGenerics) {
        return GenericTypeReflector.annotate(type, expandGenerics, new HashMap<CaptureCacheKey, AnnotatedType>());
    }

    public static AnnotatedType annotate(Type type) {
        return GenericTypeReflector.annotate(type, false);
    }

    public static AnnotatedType annotate(Type type, Annotation[] annotations) {
        return GenericTypeReflector.updateAnnotations(GenericTypeReflector.annotate(type), annotations);
    }

    private static AnnotatedType annotate(Type type, boolean expandGenerics, Map<CaptureCacheKey, AnnotatedType> cache) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType)type;
            AnnotatedType[] params = new AnnotatedType[parameterized.getActualTypeArguments().length];
            for (int i = 0; i < params.length; ++i) {
                AnnotatedType param = GenericTypeReflector.annotate(parameterized.getActualTypeArguments()[i], expandGenerics, cache);
                params[i] = GenericTypeReflector.updateAnnotations(param, GenericTypeReflector.erase(type).getTypeParameters()[i].getAnnotations());
            }
            return new AnnotatedParameterizedTypeImpl(parameterized, GenericTypeReflector.erase(type).getAnnotations(), params);
        }
        if (type instanceof CaptureType) {
            CaptureCacheKey key = new CaptureCacheKey((CaptureType)type);
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            CaptureType capture = (CaptureType)type;
            AnnotatedCaptureTypeImpl annotatedCapture = new AnnotatedCaptureTypeImpl(capture, (AnnotatedWildcardType)GenericTypeReflector.annotate(capture.getWildcardType(), expandGenerics, cache), (AnnotatedTypeVariable)GenericTypeReflector.annotate(capture.getTypeVariable(), expandGenerics, cache));
            cache.put(new CaptureCacheKey(capture), annotatedCapture);
            AnnotatedType[] upperBounds = GenericTypeReflector.mapArray(capture.getUpperBounds(), AnnotatedType[]::new, bound -> GenericTypeReflector.annotate(bound, expandGenerics, cache));
            annotatedCapture.setAnnotatedUpperBounds(upperBounds);
            return annotatedCapture;
        }
        if (type instanceof WildcardType) {
            WildcardType wildcard = (WildcardType)type;
            AnnotatedType[] lowerBounds = GenericTypeReflector.mapArray(wildcard.getLowerBounds(), AnnotatedType[]::new, bound -> GenericTypeReflector.annotate(bound, expandGenerics, cache));
            AnnotatedType[] upperBounds = GenericTypeReflector.mapArray(wildcard.getUpperBounds(), AnnotatedType[]::new, bound -> GenericTypeReflector.annotate(bound, expandGenerics, cache));
            return new AnnotatedWildcardTypeImpl(wildcard, GenericTypeReflector.erase(type).getAnnotations(), lowerBounds, upperBounds);
        }
        if (type instanceof TypeVariable) {
            return new AnnotatedTypeVariableImpl((TypeVariable)type);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genArray = (GenericArrayType)type;
            return new AnnotatedArrayTypeImpl(genArray, new Annotation[0], GenericTypeReflector.annotate(genArray.getGenericComponentType(), expandGenerics, cache));
        }
        if (type instanceof Class) {
            Class clazz = (Class)type;
            if (clazz.isArray()) {
                Class<?> componentClass = clazz.getComponentType();
                return AnnotatedArrayTypeImpl.createArrayType(new AnnotatedTypeImpl(componentClass, componentClass.getAnnotations()), new Annotation[0]);
            }
            if (clazz.getTypeParameters().length > 0 && expandGenerics) {
                return GenericTypeReflector.expandClassGenerics(clazz);
            }
            return new AnnotatedTypeImpl(clazz, clazz.getAnnotations());
        }
        throw new IllegalArgumentException("Unrecognized type: " + type.getTypeName());
    }

    public static <T extends AnnotatedType> T replaceAnnotations(T original, Annotation[] annotations) {
        if (original instanceof AnnotatedParameterizedType) {
            return (T)new AnnotatedParameterizedTypeImpl((ParameterizedType)original.getType(), annotations, ((AnnotatedParameterizedType)original).getAnnotatedActualTypeArguments());
        }
        if (original instanceof AnnotatedCaptureType) {
            AnnotatedCaptureTypeImpl capture = (AnnotatedCaptureTypeImpl)original;
            return (T)capture.setAnnotations(annotations);
        }
        if (original instanceof AnnotatedWildcardType) {
            return (T)new AnnotatedWildcardTypeImpl((WildcardType)original.getType(), annotations, ((AnnotatedWildcardType)original).getAnnotatedLowerBounds(), ((AnnotatedWildcardType)original).getAnnotatedUpperBounds());
        }
        if (original instanceof AnnotatedTypeVariable) {
            return (T)new AnnotatedTypeVariableImpl((TypeVariable)original.getType(), annotations);
        }
        if (original instanceof AnnotatedArrayType) {
            return (T)new AnnotatedArrayTypeImpl(original.getType(), annotations, ((AnnotatedArrayType)original).getAnnotatedGenericComponentType());
        }
        return (T)new AnnotatedTypeImpl(original.getType(), annotations);
    }

    public static <T extends AnnotatedType> T updateAnnotations(T original, Annotation[] annotations) {
        if (annotations == null || annotations.length == 0 || Arrays.equals(original.getAnnotations(), annotations)) {
            return original;
        }
        return GenericTypeReflector.replaceAnnotations(original, GenericTypeReflector.merge(original.getAnnotations(), annotations));
    }

    public static <T extends AnnotatedType> T mergeAnnotations(T t1, T t2) {
        Annotation[] merged = GenericTypeReflector.merge(t1.getAnnotations(), t2.getAnnotations());
        if (t1 instanceof AnnotatedParameterizedType) {
            AnnotatedType[] p1 = ((AnnotatedParameterizedType)t1).getAnnotatedActualTypeArguments();
            AnnotatedType[] p2 = ((AnnotatedParameterizedType)t2).getAnnotatedActualTypeArguments();
            AnnotatedType[] params = new AnnotatedType[p1.length];
            for (int i = 0; i < p1.length; ++i) {
                params[i] = GenericTypeReflector.mergeAnnotations(p1[i], p2[i]);
            }
            return (T)new AnnotatedParameterizedTypeImpl((ParameterizedType)t1.getType(), merged, params);
        }
        if (t1 instanceof AnnotatedWildcardType) {
            AnnotatedType[] l1 = ((AnnotatedWildcardType)t1).getAnnotatedLowerBounds();
            AnnotatedType[] l2 = ((AnnotatedWildcardType)t2).getAnnotatedLowerBounds();
            AnnotatedType[] lowerBounds = new AnnotatedType[l1.length];
            for (int i = 0; i < l1.length; ++i) {
                lowerBounds[i] = GenericTypeReflector.mergeAnnotations(l1[i], l2[i]);
            }
            AnnotatedType[] u1 = ((AnnotatedWildcardType)t1).getAnnotatedUpperBounds();
            AnnotatedType[] u2 = ((AnnotatedWildcardType)t2).getAnnotatedUpperBounds();
            AnnotatedType[] upperBounds = new AnnotatedType[u1.length];
            for (int i = 0; i < u1.length; ++i) {
                upperBounds[i] = GenericTypeReflector.mergeAnnotations(u1[i], u2[i]);
            }
            return (T)new AnnotatedWildcardTypeImpl((WildcardType)t1.getType(), merged, lowerBounds, upperBounds);
        }
        if (t1 instanceof AnnotatedTypeVariable) {
            return (T)new AnnotatedTypeVariableImpl((TypeVariable)t1.getType(), merged);
        }
        if (t1 instanceof AnnotatedArrayType) {
            AnnotatedType componentType = GenericTypeReflector.mergeAnnotations(((AnnotatedArrayType)t1).getAnnotatedGenericComponentType(), ((AnnotatedArrayType)t2).getAnnotatedGenericComponentType());
            return (T)new AnnotatedArrayTypeImpl(t1.getType(), merged, componentType);
        }
        return (T)new AnnotatedTypeImpl(t1.getType(), merged);
    }

    public static AnnotatedParameterizedType replaceParameters(AnnotatedParameterizedType type, AnnotatedType[] typeParameters) {
        return GenericTypeReflector.replaceParameters(type, new Annotation[0], typeParameters);
    }

    private static AnnotatedParameterizedType replaceParameters(AnnotatedParameterizedType type, Annotation[] annotations, AnnotatedType[] typeParameters) {
        Type[] rawArguments = GenericTypeReflector.mapArray(typeParameters, Type[]::new, AnnotatedType::getType);
        ParameterizedType inner = (ParameterizedType)type.getType();
        ParameterizedType rawType = (ParameterizedType)TypeFactory.parameterizedInnerClass(inner.getOwnerType(), GenericTypeReflector.erase(inner), rawArguments);
        return new AnnotatedParameterizedTypeImpl(rawType, GenericTypeReflector.merge(type.getAnnotations(), annotations), typeParameters);
    }

    public static <T extends AnnotatedType> T toCanonical(T type) {
        return GenericTypeReflector.toCanonical(type, Function.identity());
    }

    public static <T extends AnnotatedType> T toCanonicalBoxed(T type) {
        return GenericTypeReflector.toCanonical(type, GenericTypeReflector::box);
    }

    private static <T extends AnnotatedType> T toCanonical(T type, final Function<Type, Type> leafTransformer) {
        return (T)GenericTypeReflector.transform(type, new TypeVisitor(){

            @Override
            protected AnnotatedType visitClass(AnnotatedType type) {
                Annotation[] annotations = type.getAnnotations();
                Class raw = (Class)type.getType();
                annotations = GenericTypeReflector.merge(annotations, raw.getAnnotations());
                return new AnnotatedTypeImpl((Type)leafTransformer.apply(type.getType()), annotations);
            }

            @Override
            protected AnnotatedType visitArray(AnnotatedArrayType type) {
                return new AnnotatedArrayTypeImpl((Type)leafTransformer.apply(type.getType()), type.getAnnotations(), GenericTypeReflector.transform(type.getAnnotatedGenericComponentType(), this));
            }

            @Override
            protected AnnotatedType visitParameterizedType(AnnotatedParameterizedType type) {
                AnnotatedType[] params = (AnnotatedType[])Arrays.stream(type.getAnnotatedActualTypeArguments()).map(param -> GenericTypeReflector.transform(param, this)).toArray(AnnotatedType[]::new);
                Class raw = (Class)((ParameterizedType)type.getType()).getRawType();
                return GenericTypeReflector.replaceParameters(type, raw.getAnnotations(), params);
            }
        });
    }

    private static AnnotatedType expandGenerics(AnnotatedType type) {
        return GenericTypeReflector.transform(type, new TypeVisitor(){

            @Override
            public AnnotatedType visitClass(AnnotatedType type) {
                Class clazz = (Class)type.getType();
                if (clazz.getTypeParameters().length > 0) {
                    return GenericTypeReflector.expandClassGenerics(clazz);
                }
                return type;
            }
        });
    }

    public static AnnotatedType transform(AnnotatedType type, TypeVisitor visitor) {
        if (type instanceof AnnotatedParameterizedType) {
            return visitor.visitParameterizedType((AnnotatedParameterizedType)type);
        }
        if (type instanceof AnnotatedWildcardType) {
            return visitor.visitWildcardType((AnnotatedWildcardType)type);
        }
        if (type instanceof AnnotatedTypeVariable) {
            return visitor.visitVariable((AnnotatedTypeVariable)type);
        }
        if (type instanceof AnnotatedArrayType) {
            return visitor.visitArray((AnnotatedArrayType)type);
        }
        if (type instanceof AnnotatedCaptureType) {
            return visitor.visitCaptureType((AnnotatedCaptureType)type);
        }
        if (type.getType() instanceof Class) {
            return visitor.visitClass(type);
        }
        return visitor.visitUnmatched(type);
    }

    public static AnnotatedType reduceBounded(AnnotatedType type) {
        AnnotatedType capture = GenericTypeReflector.capture(type);
        return GenericTypeReflector.transform(capture, new TypeVisitor(){

            @Override
            protected AnnotatedType visitVariable(AnnotatedTypeVariable type) {
                return GenericTypeReflector.updateAnnotations(GenericTypeReflector.transform(type.getAnnotatedBounds()[0], this), type.getAnnotations());
            }

            @Override
            protected AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
                return type.getAnnotatedLowerBounds().length > 0 ? GenericTypeReflector.updateAnnotations(GenericTypeReflector.transform(type.getAnnotatedLowerBounds()[0], this), type.getAnnotations()) : GenericTypeReflector.updateAnnotations(GenericTypeReflector.transform(type.getAnnotatedUpperBounds()[0], this), type.getAnnotations());
            }

            @Override
            protected AnnotatedType visitCaptureType(AnnotatedCaptureType type) {
                AnnotatedType bound;
                AnnotatedType annotatedType = bound = type.getAnnotatedLowerBounds().length > 0 ? type.getAnnotatedLowerBounds()[0] : type.getAnnotatedUpperBounds()[0];
                if (bound instanceof AnnotatedParameterizedType) {
                    AnnotatedType[] typeArguments;
                    for (AnnotatedType typeArgument : typeArguments = ((AnnotatedParameterizedType)bound).getAnnotatedActualTypeArguments()) {
                        if (!type.equals(typeArgument)) continue;
                        ParameterizedType parameterizedType = (ParameterizedType)bound.getType();
                        return GenericTypeReflector.annotate(parameterizedType.getRawType(), GenericTypeReflector.merge(type.getAnnotations(), bound.getAnnotations()));
                    }
                }
                return GenericTypeReflector.updateAnnotations(GenericTypeReflector.transform(bound, this), type.getAnnotations());
            }
        });
    }

    private static AnnotatedParameterizedType expandClassGenerics(Class<?> type) {
        ParameterizedTypeImpl inner = new ParameterizedTypeImpl(type, type.getTypeParameters(), type.getDeclaringClass());
        AnnotatedType[] params = GenericTypeReflector.mapArray(type.getTypeParameters(), AnnotatedType[]::new, GenericTypeReflector::annotate);
        return new AnnotatedParameterizedTypeImpl(inner, type.getAnnotations(), params);
    }

    public static Annotation[] merge(Annotation[] ... annotations) {
        LinkedHashSet<Annotation> result = new LinkedHashSet<Annotation>();
        Annotation[][] annotationArray = annotations;
        int n = annotationArray.length;
        for (int i = 0; i < n; ++i) {
            Annotation[] annos;
            for (Annotation anno : annos = annotationArray[i]) {
                result.add(anno);
            }
        }
        return result.toArray(new Annotation[0]);
    }

    static boolean typeArraysEqual(AnnotatedType[] t1, AnnotatedType[] t2) {
        if (t1 == t2) {
            return true;
        }
        if (t1 == null) {
            return false;
        }
        if (t2 == null) {
            return false;
        }
        if (t1.length != t2.length) {
            return false;
        }
        for (int i = 0; i < t1.length; ++i) {
            if (t1[i].getType().equals(t2[i].getType()) && Arrays.equals(t1[i].getAnnotations(), t2[i].getAnnotations())) continue;
            return false;
        }
        return true;
    }

    public static int hashCode(AnnotatedType ... types) {
        int typeHash = Arrays.stream(types).mapToInt(t -> t.getType().hashCode()).reduce(0, (x, y) -> 127 * x ^ y);
        int annotationHash = GenericTypeReflector.hashCode(Arrays.stream(types).flatMap(t -> Arrays.stream(t.getAnnotations())));
        return 31 * typeHash ^ annotationHash;
    }

    static int hashCode(Stream<Annotation> annotations) {
        return annotations.mapToInt(a -> 31 * a.annotationType().hashCode() ^ a.hashCode()).reduce(0, (x, y) -> 127 * x ^ y);
    }

    public static boolean equals(AnnotatedType t1, AnnotatedType t2) {
        Objects.requireNonNull(t1);
        Objects.requireNonNull(t2);
        t1 = GenericTypeReflector.toCanonical(t1);
        t2 = GenericTypeReflector.toCanonical(t2);
        return t1.equals(t2);
    }

    private static void buildUpperBoundClassAndInterfaces(Type type, Set<Class<?>> result) {
        if (type instanceof ParameterizedType || type instanceof Class) {
            result.add(GenericTypeReflector.erase(type));
            return;
        }
        for (AnnotatedType superType : GenericTypeReflector.getExactDirectSuperTypes(GenericTypeReflector.annotate(type))) {
            GenericTypeReflector.buildUpperBoundClassAndInterfaces(superType.getType(), result);
        }
    }

    private static <I, O> O[] mapArray(I[] array, IntFunction<O[]> resultCtor, Function<I, O> mapper) {
        O[] result = resultCtor.apply(array.length);
        for (int i = 0; i < array.length; ++i) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    static {
        HashMap<Class<Object>, Class<Void>> boxTypes = new HashMap<Class<Object>, Class<Void>>();
        boxTypes.put(Boolean.TYPE, Boolean.class);
        boxTypes.put(Byte.TYPE, Byte.class);
        boxTypes.put(Character.TYPE, Character.class);
        boxTypes.put(Double.TYPE, Double.class);
        boxTypes.put(Float.TYPE, Float.class);
        boxTypes.put(Integer.TYPE, Integer.class);
        boxTypes.put(Long.TYPE, Long.class);
        boxTypes.put(Short.TYPE, Short.class);
        boxTypes.put(Void.TYPE, Void.class);
        BOX_TYPES = Collections.unmodifiableMap(boxTypes);
    }

    static class CaptureCacheKey {
        CaptureType capture;

        CaptureCacheKey(CaptureType capture) {
            this.capture = capture;
        }

        public int hashCode() {
            return 127 * this.capture.getWildcardType().hashCode() ^ this.capture.getTypeVariable().hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CaptureCacheKey)) {
                return false;
            }
            CaptureType that = ((CaptureCacheKey)obj).capture;
            return this.capture == that || this.capture.getWildcardType().equals(that.getWildcardType()) && this.capture.getTypeVariable().equals(that.getTypeVariable()) && Arrays.equals(this.capture.getUpperBounds(), that.getUpperBounds());
        }
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
            return this.capture == that.capture || new CaptureCacheKey(this.raw).equals(new CaptureCacheKey(that.raw)) && Arrays.equals(this.capture.getAnnotations(), that.capture.getAnnotations());
        }
    }
}

