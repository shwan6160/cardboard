/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.List;

public interface ConverterProvider {
    public void getConverters(TypeDeclaration var1, List<Converter<?, ?>> var2);
}

