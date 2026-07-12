/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedArrayTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedParameterizedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedTypeVariableImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedWildcardTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.ParameterizedTypeImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeVariableImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.UnresolvedTypeVariableException;
import com.bergerkiller.bukkit.common.dep.typetoken.WildcardTypeImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class VarMap {
    private final Map<TypeVariable, AnnotatedType> map = new HashMap<TypeVariable, AnnotatedType>();

    VarMap() {
    }

    VarMap(AnnotatedParameterizedType type) {
        Type owner;
        do {
            TypeVariable<Class<T>>[] typeParameters;
            Class clazz = (Class)((ParameterizedType)type.getType()).getRawType();
            AnnotatedType[] arguments = type.getAnnotatedActualTypeArguments();
            if (arguments.length != (typeParameters = clazz.getTypeParameters()).length) {
                throw new IllegalStateException("The given type [" + type + "] is inconsistent: it has " + arguments.length + " arguments instead of " + typeParameters.length);
            }
            for (int i = 0; i < arguments.length; ++i) {
                this.add(typeParameters[i], arguments[i]);
            }
        } while ((type = (owner = ((ParameterizedType)type.getType()).getOwnerType()) instanceof ParameterizedType ? (AnnotatedParameterizedType)GenericTypeReflector.annotate(owner) : null) != null);
    }

    VarMap(ParameterizedType type) {
        this((AnnotatedParameterizedType)GenericTypeReflector.annotate(type));
    }

    VarMap(TypeVariable[] variables, AnnotatedType[] values) {
        this.addAll(variables, values);
    }

    void add(TypeVariable variable, AnnotatedType value) {
        this.map.put(variable, value);
    }

    void addAll(TypeVariable[] variables, AnnotatedType[] values) {
        assert (variables.length == values.length);
        for (int i = 0; i < variables.length; ++i) {
            this.map.put(variables[i], values[i]);
        }
    }

    AnnotatedType map(AnnotatedType type) {
        return this.map(type, MappingMode.EXACT);
    }

    AnnotatedType map(AnnotatedType type, MappingMode mappingMode) {
        if (type.getType() instanceof Class) {
            return GenericTypeReflector.updateAnnotations(type, ((Class)type.getType()).getAnnotations());
        }
        if (type instanceof AnnotatedTypeVariable) {
            TypeVariable tv = (TypeVariable)type.getType();
            if (!this.map.containsKey(tv)) {
                if (mappingMode.equals((Object)MappingMode.ALLOW_INCOMPLETE)) {
                    AnnotatedTypeVariable variable = (AnnotatedTypeVariable)type;
                    AnnotatedType[] bounds = this.map(variable.getAnnotatedBounds(), mappingMode);
                    Annotation[] merged = GenericTypeReflector.merge(variable.getAnnotations(), tv.getAnnotations());
                    TypeVariableImpl v = new TypeVariableImpl(tv, merged, bounds);
                    return new AnnotatedTypeVariableImpl(v, merged);
                }
                throw new UnresolvedTypeVariableException(tv);
            }
            TypeVariable varFromClass = this.map.keySet().stream().filter(key -> key.equals(tv)).findFirst().get();
            Annotation[] merged = GenericTypeReflector.merge(type.getAnnotations(), tv.getAnnotations(), this.map.get(tv).getAnnotations(), varFromClass.getAnnotations());
            return GenericTypeReflector.updateAnnotations(this.map.get(tv), merged);
        }
        if (type instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType pType = (AnnotatedParameterizedType)type;
            ParameterizedType inner = (ParameterizedType)pType.getType();
            Class raw = (Class)inner.getRawType();
            AnnotatedType[] typeParameters = new AnnotatedType[raw.getTypeParameters().length];
            for (int i = 0; i < typeParameters.length; ++i) {
                AnnotatedType typeParameter = this.map(pType.getAnnotatedActualTypeArguments()[i], mappingMode);
                typeParameters[i] = GenericTypeReflector.updateAnnotations(typeParameter, raw.getTypeParameters()[i].getAnnotations());
            }
            Type[] rawArgs = (Type[])Arrays.stream(typeParameters).map(AnnotatedType::getType).toArray(Type[]::new);
            Type innerOwnerType = inner.getOwnerType() == null ? null : this.map(GenericTypeReflector.annotate(inner.getOwnerType()), mappingMode).getType();
            ParameterizedTypeImpl newInner = new ParameterizedTypeImpl((Class)inner.getRawType(), rawArgs, innerOwnerType);
            return new AnnotatedParameterizedTypeImpl(newInner, GenericTypeReflector.merge(pType.getAnnotations(), raw.getAnnotations()), typeParameters);
        }
        if (type instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType wType = (AnnotatedWildcardType)type;
            AnnotatedType[] up = this.map(wType.getAnnotatedUpperBounds(), mappingMode);
            AnnotatedType[] lw = this.map(wType.getAnnotatedLowerBounds(), mappingMode);
            Type[] upperBounds = up == null || up.length == 0 ? ((WildcardType)wType.getType()).getUpperBounds() : (Type[])Arrays.stream(up).map(AnnotatedType::getType).toArray(Type[]::new);
            WildcardTypeImpl w = new WildcardTypeImpl(upperBounds, (Type[])Arrays.stream(lw).map(AnnotatedType::getType).toArray(Type[]::new));
            return new AnnotatedWildcardTypeImpl(w, wType.getAnnotations(), lw, up);
        }
        if (type instanceof AnnotatedArrayType) {
            return AnnotatedArrayTypeImpl.createArrayType(this.map(((AnnotatedArrayType)type).getAnnotatedGenericComponentType(), mappingMode), type.getAnnotations());
        }
        throw new RuntimeException("Not implemented: mapping " + type.getClass() + " (" + type + ")");
    }

    AnnotatedType[] map(AnnotatedType[] types) {
        return this.map(types, MappingMode.EXACT);
    }

    AnnotatedType[] map(AnnotatedType[] types, MappingMode mappingMode) {
        AnnotatedType[] result = new AnnotatedType[types.length];
        for (int i = 0; i < types.length; ++i) {
            result[i] = this.map(types[i], mappingMode);
        }
        return result;
    }

    Type[] map(Type[] types) {
        AnnotatedType[] result = this.map((AnnotatedType[])Arrays.stream(types).map(GenericTypeReflector::annotate).toArray(AnnotatedType[]::new));
        return (Type[])Arrays.stream(result).map(AnnotatedType::getType).toArray(Type[]::new);
    }

    Type map(Type type) {
        return this.map(GenericTypeReflector.annotate(type)).getType();
    }

    public static enum MappingMode {
        EXACT,
        ALLOW_INCOMPLETE;

    }
}

