/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.dep.me.lucko.commodore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

final class ReflectionUtil {
    private static final String SERVER_VERSION = ReflectionUtil.getServerVersion();

    private static String getServerVersion() {
        Class<?> server = Bukkit.getServer().getClass();
        if (!server.getSimpleName().equals("CraftServer")) {
            return ".";
        }
        if (server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            return ".";
        }
        String version = server.getName().substring("org.bukkit.craftbukkit".length());
        return version.substring(0, version.length() - "CraftServer".length());
    }

    public static String mc(String name) {
        return "net.minecraft." + name;
    }

    public static String nms(String className) {
        return "net.minecraft.server" + SERVER_VERSION + className;
    }

    public static Class<?> mcClass(String className) throws ClassNotFoundException {
        return Class.forName(ReflectionUtil.mc(className));
    }

    public static Class<?> nmsClass(String className) throws ClassNotFoundException {
        return Class.forName(ReflectionUtil.nms(className));
    }

    public static String obc(String className) {
        return "org.bukkit.craftbukkit" + SERVER_VERSION + className;
    }

    public static Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(ReflectionUtil.obc(className));
    }

    public static int minecraftVersion() {
        try {
            Matcher matcher = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?( .*)?\\)").matcher(Bukkit.getVersion());
            if (matcher.find()) {
                return Integer.parseInt(matcher.toMatchResult().group(2), 10);
            }
            throw new IllegalArgumentException(String.format("No match found in '%s'", Bukkit.getVersion()));
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException("Failed to determine Minecraft version", ex);
        }
    }

    private ReflectionUtil() {
    }
}

