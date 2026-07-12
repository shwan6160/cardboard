/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  org.bukkit.plugin.java.JavaPluginLoader
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarFile;
import org.bukkit.plugin.java.JavaPluginLoader;

public class CommonClassManipulation {
    public static void writeClassData(ClassLoader classLoader, String name, byte[] classData) {
        Class<?> loadedClass;
        GeneratorClassLoader loader = GeneratorClassLoader.get(classLoader);
        if (SafeField.contains(classLoader.getClass(), "jar", JarFile.class)) {
            String pkgName;
            int dot = name.lastIndexOf(46);
            if (dot != -1 && loader.getPackage(pkgName = name.substring(0, dot)) == null) {
                try {
                    loader.definePackage(pkgName, null, null, null, null, null, null, null);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            loadedClass = loader.createClassFromBytecode(name, classData, null, false);
        } else {
            loadedClass = loader.createClassFromBytecode(name, classData, null, false);
        }
        CommonClassManipulation.writeClass(classLoader, name, loadedClass);
    }

    public static void writeClass(ClassLoader classLoader, String name, Class<?> clazz) {
        Map classes = SafeField.get(classLoader, "classes", Map.class);
        JavaPluginLoader loader = SafeField.get(classLoader, "loader", JavaPluginLoader.class);
        Map loaded_classes = SafeField.get(loader, "classes", Map.class);
        classes.put(name, clazz);
        loaded_classes.put(name, clazz);
    }

    public static byte[] readClassData(ClassLoader classLoader, String name) {
        byte[] byArray;
        block8: {
            InputStream is = classLoader.getResourceAsStream(CommonClassManipulation.findClassPath(name));
            try {
                byArray = ByteStreams.toByteArray((InputStream)is);
                if (is == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            is.close();
        }
        return byArray;
    }

    private static String findClassPath(String name) {
        return name.replace('.', '/').concat(".class");
    }
}

