/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public abstract class InputConverter<T>
extends Converter<Object, T> {
    public InputConverter(TypeDeclaration input, TypeDeclaration output) {
        super(input, output);
    }

    public InputConverter(TypeDeclaration output) {
        super(TypeDeclaration.OBJECT, output);
    }

    public Converter<?, T> getNullConverter() {
        return null;
    }

    @Override
    public boolean acceptsNullInput() {
        return this.getNullConverter() != null;
    }

    public abstract Converter<?, T> getConverter(TypeDeclaration var1);

    public final <I> Converter<I, T> getConverter(Class<I> inputType) {
        return this.getConverter(TypeDeclaration.fromClass(inputType));
    }

    public boolean canConvert(TypeDeclaration input) {
        return this.getConverter(input) != null;
    }

    public boolean canConvert(Class<?> inputType) {
        return this.getConverter(inputType) != null;
    }

    @Override
    public final T convertInput(Object value) {
        Converter<?, T> converter;
        if (value == null) {
            converter = this.getNullConverter();
        } else {
            if (this.output.isAssignableFrom(value)) {
                return (T)value;
            }
            converter = this.getConverter(TypeDeclaration.fromClass(value.getClass()));
        }
        if (converter != null) {
            return converter.convertInput(value);
        }
        return null;
    }
}

