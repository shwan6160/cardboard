/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.option.value;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.value.ValueType;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.Nullable;

abstract class ValueTypeImpl<T>
implements ValueType<T> {
    private final Class<T> type;

    ValueTypeImpl(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    static IllegalArgumentException doNotKnowHowToTurn(String input, Class<?> expected, @Nullable String message) {
        throw new IllegalArgumentException("Do not know how to turn value '" + input + "' into a " + expected.getName() + (message == null ? "" : ": " + message));
    }

    static final class EnumType<E extends Enum<E>>
    extends ValueTypeImpl<E> {
        private final Map<String, E> values = new HashMap<String, E>();

        EnumType(Class<E> type) {
            super(type);
            for (Enum entry : (Enum[])type.getEnumConstants()) {
                this.values.put(entry.name().toLowerCase(Locale.ROOT), entry);
            }
        }

        @Override
        public E parse(String plainValue) throws IllegalArgumentException {
            Enum result = (Enum)this.values.get(plainValue.toLowerCase(Locale.ROOT));
            if (result == null) {
                throw EnumType.doNotKnowHowToTurn(plainValue, this.type(), null);
            }
            return (E)result;
        }
    }

    static final class Types {
        static ValueType<String> STRING = new ValueTypeImpl<String>(String.class){

            @Override
            public String parse(String plainValue) throws IllegalArgumentException {
                return plainValue;
            }
        };
        static ValueType<Boolean> BOOLEAN = new ValueTypeImpl<Boolean>(Boolean.class){

            @Override
            public Boolean parse(String plainValue) throws IllegalArgumentException {
                if (plainValue.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                }
                if (plainValue.equalsIgnoreCase("false")) {
                    return Boolean.FALSE;
                }
                throw 2.doNotKnowHowToTurn(plainValue, Boolean.class, null);
            }
        };
        static ValueType<Integer> INT = new ValueTypeImpl<Integer>(Integer.class){

            @Override
            public Integer parse(String plainValue) throws IllegalArgumentException {
                try {
                    return Integer.decode(plainValue);
                }
                catch (NumberFormatException ex) {
                    throw 3.doNotKnowHowToTurn(plainValue, Integer.class, ex.getMessage());
                }
            }
        };
        static ValueType<Double> DOUBLE = new ValueTypeImpl<Double>(Double.class){

            @Override
            public Double parse(String plainValue) throws IllegalArgumentException {
                try {
                    return Double.parseDouble(plainValue);
                }
                catch (NumberFormatException ex) {
                    throw 4.doNotKnowHowToTurn(plainValue, Double.class, ex.getMessage());
                }
            }
        };

        private Types() {
        }
    }
}

