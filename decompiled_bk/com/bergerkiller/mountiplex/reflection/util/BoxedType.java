/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BoxedType {
    private static final Map<String, Class<?>> unboxedByName = new HashMap();
    private static final Map<Class<?>, Class<?>> unboxedToBoxed = new HashMap();
    private static final Map<Class<?>, Class<?>> boxedToUnboxed = new HashMap();
    private static final Map<Class<?>, Object> unboxedDefaults = new HashMap();

    private static final <T> void register(Class<?> unboxed, Class<T> boxed, T defaultValue) {
        unboxedToBoxed.put(unboxed, boxed);
        boxedToUnboxed.put(boxed, unboxed);
        unboxedByName.put(unboxed.getSimpleName(), unboxed);
        unboxedDefaults.put(unboxed, defaultValue);
    }

    public static <T> T getDefaultValue(Class<T> type) {
        return (T)unboxedDefaults.get(type);
    }

    public static Set<Class<?>> getUnboxedTypes() {
        return unboxedToBoxed.keySet();
    }

    public static Set<Class<?>> getBoxedTypes() {
        return boxedToUnboxed.keySet();
    }

    public static Class<?> getUnboxedType(String name) {
        return unboxedByName.get(name);
    }

    public static Class<?> getUnboxedType(Class<?> boxedType) {
        return boxedToUnboxed.get(boxedType);
    }

    public static Class<?> getBoxedType(Class<?> unboxedType) {
        return unboxedToBoxed.get(unboxedType);
    }

    public static Class<?> tryBoxType(Class<?> type) {
        Class<?> boxed = unboxedToBoxed.get(type);
        return boxed == null ? type : boxed;
    }

    static {
        BoxedType.register(Boolean.TYPE, Boolean.class, false);
        BoxedType.register(Character.TYPE, Character.class, Character.valueOf('\u0000'));
        BoxedType.register(Byte.TYPE, Byte.class, (byte)0);
        BoxedType.register(Short.TYPE, Short.class, (short)0);
        BoxedType.register(Integer.TYPE, Integer.class, 0);
        BoxedType.register(Long.TYPE, Long.class, 0L);
        BoxedType.register(Float.TYPE, Float.class, Float.valueOf(0.0f));
        BoxedType.register(Double.TYPE, Double.class, 0.0);
        BoxedType.register(Void.TYPE, Void.class, null);
    }
}

