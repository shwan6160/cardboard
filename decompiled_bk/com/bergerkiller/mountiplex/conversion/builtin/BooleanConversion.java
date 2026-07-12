/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;

public class BooleanConversion {
    public static void register() {
        Conversion.registerConverter(new Converter<Boolean, Byte>(Boolean.class, Byte.class){

            @Override
            public Byte convertInput(Boolean value) {
                return value != false ? (byte)1 : 0;
            }

            @Override
            public int getCost() {
                return 2;
            }
        });
        Conversion.registerConverter(new Converter<Boolean, Short>(Boolean.class, Short.class){

            @Override
            public Short convertInput(Boolean value) {
                return value != false ? (short)1 : 0;
            }

            @Override
            public int getCost() {
                return 2;
            }
        });
        Conversion.registerConverter(new Converter<Boolean, Integer>(Boolean.class, Integer.class){

            @Override
            public Integer convertInput(Boolean value) {
                return value != false ? 1 : 0;
            }

            @Override
            public int getCost() {
                return 2;
            }
        });
        Conversion.registerConverter(new Converter<Boolean, Long>(Boolean.class, Long.class){

            @Override
            public Long convertInput(Boolean value) {
                return value != false ? 1L : 0L;
            }

            @Override
            public int getCost() {
                return 2;
            }
        });
        Conversion.registerConverter(new Converter<Number, Boolean>(Number.class, Boolean.class){

            @Override
            public Boolean convertInput(Number value) {
                return value.intValue() != 0;
            }

            @Override
            public int getCost() {
                return 2;
            }
        });
    }
}

