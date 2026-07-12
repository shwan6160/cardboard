/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;

public final class MethodBodyBuilder {
    public final StringBuilder builder = new StringBuilder();

    public MethodBodyBuilder appendEnd() {
        this.builder.append(";\n");
        return this;
    }

    public MethodBodyBuilder append(String text) {
        this.builder.append(text);
        return this;
    }

    public MethodBodyBuilder appendFieldName(String text, String postfix) {
        this.builder.append(text).append(postfix);
        return this;
    }

    public MethodBodyBuilder append(char character) {
        this.builder.append(character);
        return this;
    }

    public MethodBodyBuilder append(int value) {
        this.builder.append(value);
        return this;
    }

    public MethodBodyBuilder appendBoxPrimitive(Class<?> primitiveType, String input, String input_postfix) {
        this.builder.append(BoxedType.getBoxedType(primitiveType).getSimpleName());
        this.builder.append(".valueOf(").append(input).append(input_postfix).append(')');
        return this;
    }

    public MethodBodyBuilder appendUnboxPrimitive(Class<?> boxedType) {
        this.builder.append('.').append(boxedType.getSimpleName());
        this.builder.append("Value()");
        return this;
    }

    public MethodBodyBuilder appendTypeName(Class<?> type) {
        return this.append(ReflectionUtil.getTypeName(type));
    }

    public MethodBodyBuilder appendTypeCast(Class<?> castType) {
        this.builder.append('(').append(ReflectionUtil.getTypeName(castType)).append(')');
        return this;
    }

    public MethodBodyBuilder appendBoxPrimitive(Class<?> primitiveType, String input) {
        this.builder.append(BoxedType.getBoxedType(primitiveType).getSimpleName());
        this.builder.append(".valueOf(").append(input).append(')');
        return this;
    }

    public MethodBodyBuilder appendAccessibleTypeName(Class<?> type) {
        return this.append(ReflectionUtil.getAccessibleTypeName(type));
    }

    public MethodBodyBuilder appendAccessibleTypeCast(Class<?> castType) {
        if (castType != Object.class && Resolver.isPublic(castType)) {
            String name = ReflectionUtil.getTypeName(castType);
            if (Resolver.resolveClassPath(name).equals(name)) {
                this.builder.append('(').append(name).append(')');
            } else {
                this.builder.append('(');
                this.builder.append("MPL_NOREMAP$").append(name);
                this.builder.append(')');
            }
        }
        return this;
    }

    public String toString() {
        return this.builder.toString();
    }

    public int hashCode() {
        return this.builder.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MethodBodyBuilder) {
            return this.builder.equals(((MethodBodyBuilder)o).builder);
        }
        return false;
    }
}

