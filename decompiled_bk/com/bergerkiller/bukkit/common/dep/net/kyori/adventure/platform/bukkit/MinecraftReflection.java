/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MinecraftReflection {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final String PREFIX_NMS = "net.minecraft.server";
    private static final String PREFIX_MC = "net.minecraft.";
    private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";
    private static final String CRAFT_SERVER = "CraftServer";
    @Nullable
    private static final String VERSION;

    private MinecraftReflection() {
    }

    @Nullable
    public static Class<?> findClass(String ... classNames) {
        for (String clazz : classNames) {
            if (clazz == null) continue;
            try {
                Class<?> classObj = Class.forName(clazz);
                return classObj;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return null;
    }

    @NotNull
    public static Class<?> needClass(String ... className) {
        return Objects.requireNonNull(MinecraftReflection.findClass(className), "Could not find class from candidates" + Arrays.toString(className));
    }

    public static boolean hasClass(String ... classNames) {
        return MinecraftReflection.findClass(classNames) != null;
    }

    @Nullable
    public static MethodHandle findMethod(@Nullable Class<?> holderClass, String methodName, @Nullable Class<?> returnClass, Class<?> ... parameterClasses) {
        return MinecraftReflection.findMethod(holderClass, new String[]{methodName}, returnClass, parameterClasses);
    }

    @Nullable
    public static MethodHandle findMethod(@Nullable Class<?> holderClass, @Nullable String @NotNull [] methodNames, @Nullable Class<?> returnClass, Class<?> ... parameterClasses) {
        if (holderClass == null || returnClass == null) {
            return null;
        }
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass != null) continue;
            return null;
        }
        for (String methodName : methodNames) {
            if (methodName == null) continue;
            try {
                return LOOKUP.findVirtual(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
            }
            catch (IllegalAccessException | NoSuchMethodException reflectiveOperationException) {
                // empty catch block
            }
        }
        return null;
    }

    public static MethodHandle searchMethod(@Nullable Class<?> holderClass, @Nullable Integer modifier, String methodName, @Nullable Class<?> returnClass, Class<?> ... parameterClasses) {
        return MinecraftReflection.searchMethod(holderClass, modifier, new String[]{methodName}, returnClass, parameterClasses);
    }

    public static MethodHandle searchMethod(@Nullable Class<?> holderClass, @Nullable Integer modifier, @Nullable String @NotNull [] methodNames, @Nullable Class<?> returnClass, Class<?> ... parameterClasses) {
        if (holderClass == null || returnClass == null) {
            return null;
        }
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass != null) continue;
            return null;
        }
        for (String methodName : methodNames) {
            if (methodName == null) continue;
            try {
                if (modifier != null && Modifier.isStatic(modifier)) {
                    return LOOKUP.findStatic(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
                }
                return LOOKUP.findVirtual(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
            }
            catch (IllegalAccessException | NoSuchMethodException reflectiveOperationException) {
                // empty catch block
            }
        }
        for (Method method : holderClass.getDeclaredMethods()) {
            if (modifier == null || (method.getModifiers() & modifier) == 0 || !Arrays.equals(method.getParameterTypes(), parameterClasses)) continue;
            try {
                if (Modifier.isStatic(modifier)) {
                    return LOOKUP.findStatic(holderClass, method.getName(), MethodType.methodType(returnClass, parameterClasses));
                }
                return LOOKUP.findVirtual(holderClass, method.getName(), MethodType.methodType(returnClass, parameterClasses));
            }
            catch (IllegalAccessException | NoSuchMethodException reflectiveOperationException) {
                // empty catch block
            }
        }
        return null;
    }

    @Nullable
    public static MethodHandle findStaticMethod(@Nullable Class<?> holderClass, String methodNames, @Nullable Class<?> returnClass, Class<?> ... parameterClasses) {
        return MinecraftReflection.findStaticMethod(holderClass, new String[]{methodNames}, returnClass, parameterClasses);
    }

    @Nullable
    public static MethodHandle findStaticMethod(@Nullable Class<?> holderClass, String[] methodNames, @Nullable Class<?> returnClass, Class<?> ... parameterClasses) {
        if (holderClass == null || returnClass == null) {
            return null;
        }
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass != null) continue;
            return null;
        }
        for (String methodName : methodNames) {
            try {
                return LOOKUP.findStatic(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
            }
            catch (IllegalAccessException | NoSuchMethodException reflectiveOperationException) {
            }
        }
        return null;
    }

    public static boolean hasField(@Nullable Class<?> holderClass, Class<?> type, String ... names) {
        if (holderClass == null) {
            return false;
        }
        for (String name : names) {
            try {
                Field field = holderClass.getDeclaredField(name);
                if (field.getType() != type) continue;
                return true;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        return false;
    }

    public static boolean hasMethod(@Nullable Class<?> holderClass, String methodName, Class<?> ... parameterClasses) {
        return MinecraftReflection.hasMethod(holderClass, new String[]{methodName}, parameterClasses);
    }

    public static boolean hasMethod(@Nullable Class<?> holderClass, String[] methodNames, Class<?> ... parameterClasses) {
        if (holderClass == null) {
            return false;
        }
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass != null) continue;
            return false;
        }
        for (String methodName : methodNames) {
            try {
                holderClass.getMethod(methodName, parameterClasses);
                return true;
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        return false;
    }

    @Nullable
    public static MethodHandle findConstructor(@Nullable Class<?> holderClass, Class<?> ... parameterClasses) {
        if (holderClass == null) {
            return null;
        }
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass != null) continue;
            return null;
        }
        try {
            return LOOKUP.findConstructor(holderClass, MethodType.methodType(Void.TYPE, parameterClasses));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            return null;
        }
    }

    @NotNull
    public static Field needField(@NotNull Class<?> holderClass, @NotNull String fieldName) throws NoSuchFieldException {
        Field field = holderClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @Nullable
    public static Field findField(@Nullable Class<?> holderClass, String ... fieldName) {
        return MinecraftReflection.findField(holderClass, null, fieldName);
    }

    @Nullable
    public static Field findField(@Nullable Class<?> holderClass, @Nullable Class<?> expectedType, String ... fieldNames) {
        if (holderClass == null) {
            return null;
        }
        for (String fieldName : fieldNames) {
            Field field;
            try {
                field = holderClass.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException ex) {
                continue;
            }
            field.setAccessible(true);
            if (expectedType != null && !expectedType.isAssignableFrom(field.getType())) continue;
            return field;
        }
        return null;
    }

    @Nullable
    public static MethodHandle findSetterOf(@Nullable Field field) {
        if (field == null) {
            return null;
        }
        try {
            return LOOKUP.unreflectSetter(field);
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    public static MethodHandle findGetterOf(@Nullable Field field) {
        if (field == null) {
            return null;
        }
        try {
            return LOOKUP.unreflectGetter(field);
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    public static Object findEnum(@Nullable Class<?> enumClass, @NotNull String enumName) {
        return MinecraftReflection.findEnum(enumClass, enumName, Integer.MAX_VALUE);
    }

    @Nullable
    public static Object findEnum(@Nullable Class<?> enumClass, @NotNull String enumName, int enumFallbackOrdinal) {
        if (enumClass == null || !Enum.class.isAssignableFrom(enumClass)) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
        }
        catch (IllegalArgumentException e) {
            ?[] constants = enumClass.getEnumConstants();
            if (constants.length > enumFallbackOrdinal) {
                return constants[enumFallbackOrdinal];
            }
            return null;
        }
    }

    public static boolean isCraftBukkit() {
        return VERSION != null;
    }

    @Nullable
    public static String findCraftClassName(@NotNull String className) {
        return MinecraftReflection.isCraftBukkit() ? PREFIX_CRAFTBUKKIT + VERSION + className : null;
    }

    @Nullable
    public static Class<?> findCraftClass(@NotNull String className) {
        String craftClassName = MinecraftReflection.findCraftClassName(className);
        if (craftClassName == null) {
            return null;
        }
        return MinecraftReflection.findClass(craftClassName);
    }

    @Nullable
    public static <T> Class<? extends T> findCraftClass(@NotNull String className, @NotNull Class<T> superClass) {
        Class<?> craftClass = MinecraftReflection.findCraftClass(className);
        if (craftClass == null || !Objects.requireNonNull(superClass, "superClass").isAssignableFrom(craftClass)) {
            return null;
        }
        return craftClass.asSubclass(superClass);
    }

    @NotNull
    public static Class<?> needCraftClass(@NotNull String className) {
        return Objects.requireNonNull(MinecraftReflection.findCraftClass(className), "Could not find org.bukkit.craftbukkit class " + className);
    }

    @Nullable
    public static String findNmsClassName(@NotNull String className) {
        return MinecraftReflection.isCraftBukkit() ? PREFIX_NMS + VERSION + className : null;
    }

    @Nullable
    public static Class<?> findNmsClass(@NotNull String className) {
        String nmsClassName = MinecraftReflection.findNmsClassName(className);
        if (nmsClassName == null) {
            return null;
        }
        return MinecraftReflection.findClass(nmsClassName);
    }

    @NotNull
    public static Class<?> needNmsClass(@NotNull String className) {
        return Objects.requireNonNull(MinecraftReflection.findNmsClass(className), "Could not find net.minecraft.server class " + className);
    }

    @Nullable
    public static String findMcClassName(@NotNull String className) {
        return MinecraftReflection.isCraftBukkit() ? PREFIX_MC + className : null;
    }

    @Nullable
    public static Class<?> findMcClass(String ... classNames) {
        for (String clazz : classNames) {
            Class<?> candidate;
            String nmsClassName = MinecraftReflection.findMcClassName(clazz);
            if (nmsClassName == null || (candidate = MinecraftReflection.findClass(nmsClassName)) == null) continue;
            return candidate;
        }
        return null;
    }

    @NotNull
    public static Class<?> needMcClass(String ... className) {
        return Objects.requireNonNull(MinecraftReflection.findMcClass(className), "Could not find net.minecraft class from candidates" + Arrays.toString(className));
    }

    public static @NotNull MethodHandles.Lookup lookup() {
        return LOOKUP;
    }

    static {
        Class serverClass = Bukkit.getServer().getClass();
        if (!serverClass.getSimpleName().equals(CRAFT_SERVER)) {
            VERSION = null;
        } else if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            VERSION = ".";
        } else {
            String name = serverClass.getName();
            name = name.substring(PREFIX_CRAFTBUKKIT.length());
            VERSION = name = name.substring(0, name.length() - CRAFT_SERVER.length());
        }
    }
}

