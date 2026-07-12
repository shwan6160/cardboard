/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public abstract class InputOutputConverter<I, O>
extends InputConverter<O> {
    public InputOutputConverter(TypeDeclaration input, TypeDeclaration output) {
        super(input, output);
    }

    public abstract O convert(I var1, TypeDeclaration var2, TypeDeclaration var3);

    @Override
    public Converter<?, O> getConverter(TypeDeclaration input) {
        if (input.isInstanceOf(this.output)) {
            return new NullConverter(input, this.output);
        }
        return new ElementConverter(input, this.output);
    }

    private final class ElementConverter
    extends Converter<I, O> {
        public ElementConverter(TypeDeclaration input, TypeDeclaration output) {
            super(input, output);
        }

        @Override
        public O convertInput(I value) {
            return InputOutputConverter.this.convert(value, this.input, this.output);
        }
    }
}

