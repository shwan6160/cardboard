/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public final class FailingConverter<I, O>
extends Converter<I, O> {
    private final String message;
    private static final FailingConverter UNINITIALIZED = new FailingConverter(TypeDeclaration.OBJECT, TypeDeclaration.OBJECT, "Converter has not been initialized");

    private FailingConverter(TypeDeclaration input, TypeDeclaration output, String message) {
        super(input, output);
        this.message = message;
    }

    @Override
    public O convertInput(I value) {
        throw new UnsupportedOperationException(this.message);
    }

    public String getMessage() {
        return this.message;
    }

    public static <I, O> FailingConverter<I, O> create(TypeDeclaration input, TypeDeclaration output, String message) {
        return new FailingConverter<I, O>(input, output, message);
    }

    public static <I, O> FailingConverter<I, O> uninitialized() {
        return UNINITIALIZED;
    }
}

