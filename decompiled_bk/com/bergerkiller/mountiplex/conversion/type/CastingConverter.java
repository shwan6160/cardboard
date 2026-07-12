/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public final class CastingConverter<T>
extends Converter<Object, T> {
    public CastingConverter(Class<?> input, Class<?> output) {
        super(input, output);
    }

    public CastingConverter(TypeDeclaration input, TypeDeclaration output) {
        super(input, output);
    }

    @Override
    public T convertInput(Object value) {
        if (this.output.isAssignableFrom(value)) {
            return (T)value;
        }
        return null;
    }
}

