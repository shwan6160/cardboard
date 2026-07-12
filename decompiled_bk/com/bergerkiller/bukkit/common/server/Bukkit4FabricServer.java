/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.server.ForgeSupport;
import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

public class Bukkit4FabricServer
extends SpigotServer
implements FieldNameResolver,
MethodNameResolver {
    private ReflectionRemapperClass reflectionRemapper = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("com.javazilla.bukkitfabric.BukkitFabricMod");
        }
        catch (Throwable t) {
            return false;
        }
        this.reflectionRemapper = Template.Class.create(ReflectionRemapperClass.class);
        if (!this.reflectionRemapper.isAvailable()) {
            return false;
        }
        this.reflectionRemapper.forceInitialization();
        return true;
    }

    @Override
    public String getServerName() {
        return "Fabric (Bukkit4fabric)";
    }

    @Override
    public Collection<String> getLoadableWorldsLegacy() {
        return ForgeSupport.bukkit().getLoadableWorlds();
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return ForgeSupport.bukkit().isLoadableWorld(worldName);
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldRegionFolder(worldName);
    }

    @Override
    public File getWorldFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldFolder(worldName);
    }

    @Override
    public File getWorldLevelFile(String worldName) {
        return ForgeSupport.bukkit().getWorldLevelFile(worldName);
    }

    @Override
    public String resolveClassPath(String path) {
        path = super.resolveClassPath(path);
        path = this.reflectionRemapper.mapClassName(path);
        return path;
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }
        return !classPath.startsWith("net.minecraft.");
    }

    @Override
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        try {
            Method method = this.reflectionRemapper.getDeclaredMethodByName(type, methodName, params);
            if (method != null) {
                return method.getName();
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to resolve method " + methodName, t);
        }
        return methodName;
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        try {
            Field field = this.reflectionRemapper.getDeclaredFieldByName(type, fieldName);
            if (field != null) {
                return field.getName();
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to resolve field " + fieldName, t);
        }
        return fieldName;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("fabric", "bukkit4fabric");
        variables.put("forge_nms_obfuscated", "true");
    }

    @Template.Optional
    @Template.InstanceType(value="com.javazilla.bukkitfabric.nms.ReflectionRemapper")
    public static abstract class ReflectionRemapperClass
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static String mapClassName(String className)")
        public abstract String mapClassName(String var1);

        @Template.Generated(value="public static java.lang.reflect.Method getDeclaredMethodByName(Class<?> calling, String f, Class<?>[] parms)")
        public abstract Method getDeclaredMethodByName(Class<?> var1, String var2, Class<?>[] var3);

        @Template.Generated(value="public static java.lang.reflect.Field getDeclaredFieldByName(Class<?> calling, String f)")
        public abstract Field getDeclaredFieldByName(Class<?> var1, String var2);
    }
}

