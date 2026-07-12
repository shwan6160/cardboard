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
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

public class ArclightServerLegacy
extends SpigotServer
implements FieldNameResolver,
MethodNameResolver {
    private ArclightRemapper arclightRemapper = null;
    private Object classLoaderRemapper = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("io.izzel.arclight.common.ArclightMain");
        }
        catch (Throwable t) {
            return false;
        }
        try {
            Class.forName("io.izzel.arclight.common.mod.util.remapper.resource.RemapSourceHandler");
            return false;
        }
        catch (Throwable t) {
            this.arclightRemapper = Template.Class.create(ArclightRemapper.class);
            if (!this.arclightRemapper.isAvailable()) {
                return false;
            }
            this.arclightRemapper.forceInitialization();
            try {
                ClassLoader loader = this.getClass().getClassLoader();
                Field field = loader.getClass().getDeclaredField("remapper");
                field.setAccessible(true);
                this.classLoaderRemapper = field.get(loader);
                field.setAccessible(false);
            }
            catch (Throwable t2) {
                Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize Arclight remapper", t2);
                return false;
            }
            return true;
        }
    }

    @Override
    public String getServerName() {
        return "Arclight (Legacy)";
    }

    @Override
    public boolean isForgeServer() {
        return true;
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
        path = this.arclightRemapper.mapClassName(this.classLoaderRemapper, path);
        return path;
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }
        if (classPath.startsWith("net.minecraft.server.")) {
            return false;
        }
        return false;
    }

    @Override
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        return this.arclightRemapper.mapFieldName(this.classLoaderRemapper, declaringClass, fieldName);
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return this.arclightRemapper.mapMethodName(this.classLoaderRemapper, declaringClass, methodName, parameterTypes);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "arclight");
    }

    @Template.Optional
    @Template.InstanceType(value="io.izzel.arclight.common.mod.util.remapper.ClassLoaderRemapper")
    public static abstract class ArclightRemapper
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static Object createRemapper(ClassLoader classLoader) {\n    return ArclightRemapper.createClassLoaderRemapper(classLoader);\n}")
        public abstract Object createRemapper(ClassLoader var1);

        @Template.Generated(value="public static String mapClassName(Object classLoaderRemapper, String className) {\n    ClassLoaderRemapper remapper = (ClassLoaderRemapper) classLoaderRemapper;\n    String internalName = className.replace('.', '/');\n    String mappedName = remapper.map(internalName);\n    if (!mappedName.equals(internalName)) {\n        return mappedName.replace('/', '.');\n    }\n               return className;\n}")
        public abstract String mapClassName(Object var1, String var2);

        @Template.Generated(value="public static String mapMethodName(Object classLoaderRemapper, Class<?> type, String name, Class<?>[] parameterTypes) {\n    ClassLoaderRemapper remapper = (ClassLoaderRemapper) classLoaderRemapper;\n    java.lang.reflect.Method method = remapper.tryMapMethodToSrg(type, name, parameterTypes);\n    if (method != null) {\n        return com.bergerkiller.mountiplex.reflection.util.asm.MPLType.getName(method);\n    }\n\n               return name;\n}")
        public abstract String mapMethodName(Object var1, Class<?> var2, String var3, Class<?>[] var4);

        @Template.Generated(value="public static String mapFieldName(Object classLoaderRemapper, Class<?> type, String fieldName) {\n    ClassLoaderRemapper remapper = (ClassLoaderRemapper) classLoaderRemapper;\n    try {\n                   return remapper.tryMapFieldToSrg(type, fieldName);\n    } catch (Throwable t) {\n        com.bergerkiller.bukkit.common.Logging.LOGGER_REFLECTION.log(java.util.logging.Level.WARNING, \"Failed to remap field \" + fieldName, t);\n        return fieldName;\n    }\n           }")
        public abstract String mapFieldName(Object var1, Class<?> var2, String var3);
    }
}

