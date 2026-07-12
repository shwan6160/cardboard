/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;

public class NumberConversion {
    public static void register() {
        Conversion.registerConverter(new IntegerNumberParser<Byte>(Byte.class){

            @Override
            public Byte parse(String value) {
                return Byte.parseByte(value);
            }
        });
        Conversion.registerConverter(new IntegerNumberParser<Short>(Short.class){

            @Override
            public Short parse(String value) {
                return Short.parseShort(value);
            }
        });
        Conversion.registerConverter(new IntegerNumberParser<Integer>(Integer.class){

            @Override
            public Integer parse(String value) {
                return Integer.parseInt(value);
            }
        });
        Conversion.registerConverter(new IntegerNumberParser<Long>(Long.class){

            @Override
            public Long parse(String value) {
                return Long.parseLong(value);
            }
        });
        Conversion.registerConverter(new FloatingNumberParser<Float>(Float.class){

            @Override
            public Float parse(String value) {
                return Float.valueOf(Float.parseFloat(value));
            }
        });
        Conversion.registerConverter(new FloatingNumberParser<Double>(Double.class){

            @Override
            public Double parse(String value) {
                return Double.parseDouble(value);
            }
        });
        Conversion.registerConverter(new NumberParser<Number>(Number.class){

            @Override
            public Number parse(String value, boolean isFloatingPoint) throws NumberFormatException {
                if (isFloatingPoint) {
                    float resultF;
                    double result = Double.parseDouble(value);
                    if (result == (double)(resultF = (float)result)) {
                        return Float.valueOf(resultF);
                    }
                    return result;
                }
                long result = Long.parseLong(value);
                if (result >= -128L && result <= 127L) {
                    return (byte)result;
                }
                if (result >= -32768L && result <= 32767L) {
                    return (short)result;
                }
                if (result >= Integer.MIN_VALUE && result < Integer.MAX_VALUE) {
                    return (int)result;
                }
                return result;
            }
        });
        Conversion.registerConverter(new NumberCaster<Byte>(Byte.class){

            @Override
            public Byte convertInput(Number value) {
                return value.byteValue();
            }
        });
        Conversion.registerConverter(new NumberCaster<Short>(Short.class){

            @Override
            public Short convertInput(Number value) {
                return value.shortValue();
            }
        });
        Conversion.registerConverter(new NumberCaster<Integer>(Integer.class){

            @Override
            public Integer convertInput(Number value) {
                return value.intValue();
            }
        });
        Conversion.registerConverter(new NumberCaster<Long>(Long.class){

            @Override
            public Long convertInput(Number value) {
                return value.longValue();
            }
        });
        Conversion.registerConverter(new NumberCaster<Float>(Float.class){

            @Override
            public Float convertInput(Number value) {
                return Float.valueOf(value.floatValue());
            }
        });
        Conversion.registerConverter(new NumberCaster<Double>(Double.class){

            @Override
            public Double convertInput(Number value) {
                return value.doubleValue();
            }
        });
    }

    private static abstract class NumberCaster<T>
    extends Converter<Number, T> {
        public NumberCaster(Class<T> output) {
            super(Number.class, output);
        }
    }

    private static abstract class IntegerNumberParser<T>
    extends NumberParser<T> {
        public IntegerNumberParser(Class<T> output) {
            super(output);
        }

        @Override
        public final T parse(String value, boolean isFloatingPoint) throws NumberFormatException {
            if (isFloatingPoint) {
                return this.parse(value.substring(0, value.indexOf(46)));
            }
            return this.parse(value);
        }

        public abstract T parse(String var1) throws NumberFormatException;
    }

    private static abstract class FloatingNumberParser<T>
    extends NumberParser<T> {
        public FloatingNumberParser(Class<T> output) {
            super(output);
        }

        @Override
        public final T parse(String value, boolean isFloatingPoint) throws NumberFormatException {
            if (isFloatingPoint) {
                return this.parse(value);
            }
            return this.parse(value + ".0");
        }

        public abstract T parse(String var1) throws NumberFormatException;
    }

    private static abstract class NumberParser<T>
    extends Converter<String, T> {
        public NumberParser(Class<T> output) {
            super(String.class, output);
        }

        public abstract T parse(String var1, boolean var2) throws NumberFormatException;

        @Override
        public T convertInput(String value) {
            try {
                StringBuilder rval = new StringBuilder(value.length());
                boolean hasComma = false;
                boolean hasDigit = false;
                for (int i = 0; i < value.length(); ++i) {
                    char c = value.charAt(i);
                    if (Character.isDigit(c)) {
                        rval.append(c);
                        hasDigit = true;
                        continue;
                    }
                    if (c == ' ') {
                        if (!hasDigit) continue;
                        break;
                    }
                    if (!(c != ',' && c != '.' || hasComma)) {
                        rval.append('.');
                        hasComma = true;
                        continue;
                    }
                    if (c != '-' || rval.length() != 0) continue;
                    rval.append(c);
                }
                if (hasDigit) {
                    return this.parse(rval.toString(), hasComma);
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            return null;
        }
    }
}

