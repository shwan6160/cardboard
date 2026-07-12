/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeVariableImpl<D extends GenericDeclaration>
implements TypeVariable<D> {
    private final Map<Class<? extends Annotation>, Annotation> annotations;
    private final D genericDeclaration;
    private final String name;
    private final AnnotatedType[] bounds;

    TypeVariableImpl(TypeVariable<D> variable, AnnotatedType[] bounds) {
        this(variable, variable.getAnnotations(), bounds);
    }

    TypeVariableImpl(TypeVariable<D> variable, Annotation[] annotations, AnnotatedType[] bounds) {
        Objects.requireNonNull(variable);
        this.genericDeclaration = variable.getGenericDeclaration();
        this.name = variable.getName();
        this.annotations = new HashMap<Class<? extends Annotation>, Annotation>();
        for (Annotation annotation : annotations) {
            this.annotations.put(annotation.annotationType(), annotation);
        }
        if (bounds == null || bounds.length == 0) {
            throw new IllegalArgumentException("There must be at least one bound. For an unbound variable, the bound must be Object");
        }
        this.bounds = bounds;
    }

    @Override
    public Type[] getBounds() {
        return (Type[])Arrays.stream(this.bounds).map(AnnotatedType::getType).toArray(Type[]::new);
    }

    @Override
    public D getGenericDeclaration() {
        return this.genericDeclaration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        return this.bounds;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return (T)this.annotations.get(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations.values().toArray(new Annotation[0]);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof TypeVariable) {
            TypeVariable that = (TypeVariable)other;
            return Objects.equals(this.genericDeclaration, that.getGenericDeclaration()) && Objects.equals(this.name, that.getName());
        }
        return false;
    }

    public int hashCode() {
        return this.genericDeclaration.hashCode() ^ this.name.hashCode();
    }

    public String toString() {
        return this.annotationsString() + this.getName();
    }

    private String annotationsString() {
        return this.annotations.isEmpty() ? "" : this.annotations.values().stream().map(Annotation::toString).collect(Collectors.joining(", ")) + " ";
    }
}

