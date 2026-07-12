/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.option.value;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.value.ValueTypeImpl;
import java.util.Objects;

public interface ValueType<T> {
    public static ValueType<String> stringType() {
        return ValueTypeImpl.Types.STRING;
    }

    public static ValueType<Boolean> booleanType() {
        return ValueTypeImpl.Types.BOOLEAN;
    }

    public static ValueType<Integer> integerType() {
        return ValueTypeImpl.Types.INT;
    }

    public static ValueType<Double> doubleType() {
        return ValueTypeImpl.Types.DOUBLE;
    }

    public static <E extends Enum<E>> ValueType<E> enumType(Class<E> enumClazz) {
        return new ValueTypeImpl.EnumType<E>(Objects.requireNonNull(enumClazz, "enumClazz"));
    }

    public Class<T> type();

    public T parse(String var1) throws IllegalArgumentException;
}

