/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.type.RawConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public final class NullConverter
extends RawConverter {
    public NullConverter(Class<?> input, Class<?> output) {
        super(input, output);
    }

    public NullConverter(TypeDeclaration input, TypeDeclaration output) {
        super(input, output);
    }

    @Override
    public final Object convertInput(Object value) {
        return value;
    }

    @Override
    public int getCost() {
        return 0;
    }
}

