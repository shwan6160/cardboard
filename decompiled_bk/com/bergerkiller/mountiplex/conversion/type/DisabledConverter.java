/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public class DisabledConverter<I, O>
extends Converter<I, O> {
    private final String _message;

    public DisabledConverter(Class<?> input, Class<?> output, String message) {
        super(input, output);
        this._message = message;
    }

    public DisabledConverter(TypeDeclaration input, TypeDeclaration output, String message) {
        super(input, output);
        this._message = message;
    }

    @Override
    public final O convertInput(I value) {
        throw new UnsupportedOperationException(this._message);
    }

    @Override
    public final boolean acceptsNullInput() {
        return true;
    }
}

