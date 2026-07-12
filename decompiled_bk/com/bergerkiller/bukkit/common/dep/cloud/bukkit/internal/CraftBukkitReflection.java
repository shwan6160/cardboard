/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class CraftBukkitReflection {
    private static final String PREFIX_NMS = "net.minecraft.server";
    private static final String PREFIX_MC = "net.minecraft.";
    private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";
    private static final String CRAFT_SERVER = "CraftServer";
    private static final String CB_PKG_VERSION;
    public static final int MAJOR_REVISION;

    private static int parseMajorRevision(@NonNull String versionName) {
        String[] components = versionName.split("\\.");
        if (components.length == 0) {
            throw new IllegalArgumentException("Empty version name");
        }
        int major = Integer.parseInt(components[0]);
        return major == 1 && components.length > 1 ? Integer.parseInt(components[1]) : major;
    }

    @SafeVarargs
    public static <T> @Nullable T firstNonNullOrNull(T ... elements) {
        for (T element : elements) {
            if (element == null) continue;
            return element;
        }
        return null;
    }

    @SafeVarargs
    public static <T> @NonNull T firstNonNullOrThrow(@NonNull Supplier<@NonNull String> errorMessage, T ... elements) {
        @Nullable T t = CraftBukkitReflection.firstNonNullOrNull(elements);
        if (t == null) {
            throw new IllegalArgumentException(errorMessage.get());
        }
        return t;
    }

    public static @NonNull Class<?> needNMSClassOrElse(@NonNull String nms, String ... classNames) throws RuntimeException {
        Class<?> nmsClass = CraftBukkitReflection.findNMSClass(nms);
        if (nmsClass != null) {
            return nmsClass;
        }
        return CraftBukkitReflection.firstNonNullOrThrow(() -> String.format("Cound't find the NMS class '%s', or any of the following fallbacks: %s", nms, Arrays.toString(classNames)), (Class[])Arrays.stream(classNames).map(CraftBukkitReflection::findClass).toArray(Class[]::new));
    }

    public static @NonNull Class<?> needMCClass(@NonNull String name) throws RuntimeException {
        return CraftBukkitReflection.needClass(PREFIX_MC + name);
    }

    public static @NonNull Class<?> needNMSClass(@NonNull String className) throws RuntimeException {
        return CraftBukkitReflection.needClass(PREFIX_NMS + CB_PKG_VERSION + className);
    }

    public static @NonNull Class<?> needOBCClass(@NonNull String className) throws RuntimeException {
        return CraftBukkitReflection.needClass(PREFIX_CRAFTBUKKIT + CB_PKG_VERSION + className);
    }

    public static @Nullable Class<?> findMCClass(@NonNull String name) throws RuntimeException {
        return CraftBukkitReflection.findClass(PREFIX_MC + name);
    }

    public static @Nullable Class<?> findNMSClass(@NonNull String className) throws RuntimeException {
        return CraftBukkitReflection.findClass(PREFIX_NMS + CB_PKG_VERSION + className);
    }

    public static @Nullable Class<?> findOBCClass(@NonNull String className) throws RuntimeException {
        return CraftBukkitReflection.findClass(PREFIX_CRAFTBUKKIT + CB_PKG_VERSION + className);
    }

    public static @NonNull Class<?> needClass(@NonNull String className) throws RuntimeException {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable Class<?> findClass(@NonNull String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static @NonNull Field needField(@NonNull Class<?> holder, @NonNull String name) throws RuntimeException {
        try {
            Field field = holder.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable Field findField(@NonNull Class<?> holder, @NonNull String name) throws RuntimeException {
        try {
            return CraftBukkitReflection.needField(holder, name);
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    public static @NonNull Constructor<?> needConstructor(@NonNull Class<?> holder, Class<?> ... parameters) {
        try {
            return holder.getDeclaredConstructor(parameters);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static @Nullable Constructor<?> findConstructor(@NonNull Class<?> holder, Class<?> ... parameters) {
        try {
            return holder.getDeclaredConstructor(parameters);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static boolean classExists(@NonNull String className) {
        return CraftBukkitReflection.findClass(className) != null;
    }

    public static @Nullable Method findMethod(@NonNull Class<?> holder, @NonNull String name, Class<?> ... params) throws RuntimeException {
        try {
            return holder.getMethod(name, params);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static @NonNull Method needMethod(@NonNull Class<?> holder, @NonNull String name, Class<?> ... params) throws RuntimeException {
        try {
            return holder.getMethod(name, params);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Method> streamMethods(@NonNull Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods());
    }

    public static Object invokeConstructorOrStaticMethod(Executable executable, Object ... args) throws ReflectiveOperationException {
        if (executable instanceof Constructor) {
            return ((Constructor)executable).newInstance(args);
        }
        if (!Modifier.isStatic(executable.getModifiers())) {
            throw new IllegalArgumentException("Method " + executable + " is not static.");
        }
        return ((Method)executable).invoke(null, args);
    }

    private CraftBukkitReflection() {
    }

    static {
        Class serverClass = Bukkit.getServer() == null ? CraftBukkitReflection.needClass("org.bukkit.craftbukkit.CraftServer") : Bukkit.getServer().getClass();
        String pkg = serverClass.getPackage().getName();
        String nmsVersion = pkg.substring(pkg.lastIndexOf(".") + 1);
        if (!nmsVersion.contains("_")) {
            int fallbackVersion = -1;
            if (Bukkit.getServer() != null) {
                try {
                    Method getMinecraftVersion = serverClass.getDeclaredMethod("getMinecraftVersion", new Class[0]);
                    fallbackVersion = CraftBukkitReflection.parseMajorRevision(getMinecraftVersion.invoke((Object)Bukkit.getServer(), new Object[0]).toString());
                }
                catch (Exception getMinecraftVersion) {}
            } else {
                try {
                    Class<?> sharedConstants = CraftBukkitReflection.needClass("net.minecraft.SharedConstants");
                    Method getCurrentVersion = sharedConstants.getDeclaredMethod("getCurrentVersion", new Class[0]);
                    Object currentVersion = getCurrentVersion.invoke(null, new Object[0]);
                    Method getName = null;
                    try {
                        getName = currentVersion.getClass().getDeclaredMethod("getName", new Class[0]);
                    }
                    catch (NoSuchMethodException noSuchMethodException) {
                        // empty catch block
                    }
                    if (getName == null) {
                        getName = currentVersion.getClass().getDeclaredMethod("name", new Class[0]);
                    }
                    String versionName = (String)getName.invoke(currentVersion, new Object[0]);
                    try {
                        fallbackVersion = CraftBukkitReflection.parseMajorRevision(versionName);
                    }
                    catch (Exception exception) {}
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
            MAJOR_REVISION = fallbackVersion;
        } else {
            MAJOR_REVISION = Integer.parseInt(nmsVersion.split("_")[1]);
        }
        String name = serverClass.getName();
        name = name.substring(PREFIX_CRAFTBUKKIT.length());
        CB_PKG_VERSION = name = name.substring(0, name.length() - CRAFT_SERVER.length());
    }
}

