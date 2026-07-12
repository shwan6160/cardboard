/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;

public class VoidTypeConverter {
    public static void register() {
        Conversion.registerConverter(new Converter<Object, Object>(Object.class, Void.TYPE){

            @Override
            public Object convertInput(Object value) {
                return null;
            }
        });
    }
}

