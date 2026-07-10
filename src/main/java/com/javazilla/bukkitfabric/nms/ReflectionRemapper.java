package com.javazilla.bukkitfabric.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionRemapper {
    private ReflectionRemapper() {
    }

    public static String mapClassName(String className) {
        return org.cardboardpowered.util.nms.ReflectionRemapper.mapClassName(className);
    }

    public static Method getDeclaredMethodByName(Class<?> calling, String f, Class<?>[] parms) {
        String methodName = f;
        if (calling.getName().contains("AttributeInstance")) {
            if ("a".equals(f)) {
                methodName = "setBaseValue";
            } else if ("b".equals(f)) {
                methodName = "getBaseValue";
            } else if ("e".equals(f)) {
                methodName = "getValue";
            }
        } else if (calling.getName().contains("HangingEntity")) {
            if ("bt".equals(f)) {
                methodName = "getDirection";
            }
        } else if (calling.getName().contains("ServerPlayer")) {
            if ("a".equals(f)) {
                methodName = "sendSystemMessage";
            }
        }

        try {
            org.cardboardpowered.util.nms.RemapUtils ru = (org.cardboardpowered.util.nms.RemapUtils) org.cardboardpowered.mohistremap.RemapUtilProvider.get();
            String mapped = ru != null ? ru.mapMethodName(calling, methodName, parms) : methodName;
            Method method = calling.getDeclaredMethod(mapped, parms);
            method.setAccessible(true);
            return method;
        } catch (Throwable t) {
            try {
                Method method = calling.getDeclaredMethod(methodName, parms);
                method.setAccessible(true);
                return method;
            } catch (Throwable ignored) {}
            return null;
        }
    }

    public static Field getDeclaredFieldByName(Class<?> calling, String f) {
        try {
            org.cardboardpowered.util.nms.RemapUtils ru = (org.cardboardpowered.util.nms.RemapUtils) org.cardboardpowered.mohistremap.RemapUtilProvider.get();
            String mapped = ru != null ? ru.mapFieldName(calling, f) : f;
            Field field = calling.getDeclaredField(mapped);
            field.setAccessible(true);
            return field;
        } catch (Throwable t) {
            try {
                Field field = calling.getDeclaredField(f);
                field.setAccessible(true);
                return field;
            } catch (Throwable ignored) {}
            return null;
        }
    }
}
