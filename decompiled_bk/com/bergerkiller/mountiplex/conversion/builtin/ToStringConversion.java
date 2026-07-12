/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import java.lang.reflect.Array;
import java.util.Collection;

public class ToStringConversion {
    public static void register() {
        Conversion.registerConverter(new Converter<Object, String>(Object.class, String.class){

            @Override
            public String convertInput(Object value) {
                return value.toString();
            }

            @Override
            public int getCost() {
                return 100;
            }
        });
        Conversion.registerConverter(new Converter<CharSequence, String>(CharSequence.class, String.class){

            @Override
            public String convertInput(CharSequence value) {
                return value.toString();
            }
        });
        Conversion.registerConverter(new Converter<Double, String>(Double.class, String.class){

            @Override
            public String convertInput(Double value) {
                return Double.toString(value);
            }
        });
        Conversion.registerConverter(new Converter<Float, String>(Float.class, String.class){

            @Override
            public String convertInput(Float value) {
                return Float.toString(value.floatValue());
            }
        });
        Conversion.registerConverter(new Converter<Byte, String>(Byte.class, String.class){

            @Override
            public String convertInput(Byte value) {
                return Byte.toString(value);
            }
        });
        Conversion.registerConverter(new Converter<Short, String>(Short.class, String.class){

            @Override
            public String convertInput(Short value) {
                return Short.toString(value);
            }
        });
        Conversion.registerConverter(new Converter<Integer, String>(Integer.class, String.class){

            @Override
            public String convertInput(Integer value) {
                return Integer.toString(value);
            }
        });
        Conversion.registerConverter(new Converter<Long, String>(Long.class, String.class){

            @Override
            public String convertInput(Long value) {
                return Long.toString(value);
            }
        });
        Conversion.registerConverter(new Converter<Boolean, String>(Boolean.class, String.class){

            @Override
            public String convertInput(Boolean value) {
                return value.toString();
            }
        });
        Conversion.registerConverter(new Converter<char[], String>(char[].class, String.class){

            @Override
            public String convertInput(char[] value) {
                return String.copyValueOf(value);
            }
        });
        for (Class primitiveType : new Class[]{Double.TYPE, Float.TYPE, Long.TYPE, Integer.TYPE, Short.TYPE, Byte.TYPE, Boolean.TYPE}) {
            Class<?> arrType = MountiplexUtil.getArrayType(primitiveType);
            final Converter elToStrConv = Conversion.find(primitiveType, String.class);
            Conversion.registerConverter(new Converter<Object, String>(arrType, String.class){

                @Override
                public String convertInput(Object value) {
                    int len = Array.getLength(value);
                    StringBuilder builder = new StringBuilder(len * 5);
                    builder.append('[');
                    for (int i = 0; i < len; ++i) {
                        if (i > 0) {
                            builder.append(", ");
                        }
                        builder.append((String)elToStrConv.convertInput(Array.get(value, i)));
                    }
                    builder.append(']');
                    return builder.toString();
                }
            });
        }
        Conversion.registerConverter(new Converter<Object[], String>(Object[].class, String.class){

            @Override
            public String convertInput(Object[] value) {
                Converter<?, String> elConverter = Conversion.find(value.getClass().getComponentType(), String.class);
                StringBuilder builder = new StringBuilder(value.length * 5);
                builder.append('[');
                for (int i = 0; i < value.length; ++i) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    builder.append(elConverter.convert(value[i], ""));
                }
                builder.append(']');
                return builder.toString();
            }
        });
        Conversion.registerConverter(new Converter<Collection<?>, String>((Class)Collection.class, (Class)String.class){

            @Override
            public String convertInput(Collection<?> collection) {
                StringBuilder builder = new StringBuilder(collection.size() * 5);
                InputConverter<String> toStringConv = Conversion.find(String.class);
                boolean first = true;
                builder.append("[");
                for (Object element : collection) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(toStringConv.convert(element, "null"));
                }
                builder.append("]");
                return builder.toString();
            }
        });
    }
}

