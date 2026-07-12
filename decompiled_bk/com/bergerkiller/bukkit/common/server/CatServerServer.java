/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.server.ForgeSupport;
import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bukkit.entity.EntityType;

public class CatServerServer
extends SpigotServer
implements FieldNameResolver,
MethodNameResolver {
    private RemapUtils remapUtils = null;
    private List<Class<?>> customEntityBaseClasses = new ArrayList();

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("catserver.server.CatServer");
        }
        catch (Throwable t) {
            return false;
        }
        this.remapUtils = Template.Class.create(RemapUtils.class);
        if (!this.remapUtils.isAvailable()) {
            return false;
        }
        this.remapUtils.forceInitialization();
        Stream.of("CraftCustomEntity", "CraftCustomChestHorse", "CraftCustomHorse").forEach(n -> {
            try {
                Class<?> type = Class.forName("catserver.server.entity." + n);
                this.customEntityBaseClasses.add(type);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        });
        return true;
    }

    @Override
    public String getServerName() {
        return "CatServer";
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
        path = this.remapUtils.mapClassName(path);
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
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        return this.remapUtils.mapFieldName(declaringClass, fieldName);
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return this.remapUtils.mapMethod(declaringClass, methodName, parameterTypes);
    }

    @Override
    public boolean isCustomEntityType(EntityType entityType) {
        Class entityClass = entityType.getEntityClass();
        if (entityClass != null) {
            for (Class<?> customType : this.customEntityBaseClasses) {
                if (!customType.isAssignableFrom(entityClass)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "catserver");
        variables.put("forge_nms_obfuscated", "true");
    }

    @Template.Optional
    @Template.InstanceType(value="catserver.server.remapper.RemapUtils")
    public static abstract class RemapUtils
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static String mapClassName(String className) {\n    if (className.startsWith(\"net.minecraft.\")) {\n        String internalClassName = className.replace('.', '/');\n        String mapping_name = (String) ReflectionTransformer.jarMapping.classes.get(internalClassName);\n        if (mapping_name != null) {\n            return mapping_name.replace('/', '.');\n        } else {\n            // CatServer BUGFIX!!!\n            // If we do not do this, it will suffer a NPE in the PluginClassLoader\n            return \"missing.type.\" + className;\n        }\n               }\n    return className;\n}")
        public abstract String mapClassName(String var1);

        @Template.Generated(value="public static String mapMethod(Class<?> inst, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethod(Class<?> var1, String var2, Class<?>[] var3);

        @Template.Generated(value="public static String mapFieldName(Class<?> inst, String name)")
        public abstract String mapFieldName(Class<?> var1, String var2);
    }
}

