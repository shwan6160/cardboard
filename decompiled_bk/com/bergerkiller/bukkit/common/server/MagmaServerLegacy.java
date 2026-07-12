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
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import org.bukkit.entity.EntityType;

public class MagmaServerLegacy
extends SpigotServer
implements FieldNameResolver,
MethodNameResolver {
    private RemappingUtilsClass remappingUtils = null;
    private Class<?> customEntityBaseClass = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("org.magmafoundation.magma.Magma");
        }
        catch (Throwable t) {
            return false;
        }
        try {
            Class.forName("org.magmafoundation.magma.remapping.handlers.RemapSourceHandler");
            return false;
        }
        catch (Throwable throwable) {
            this.remappingUtils = Template.Class.create(RemappingUtilsClass.class);
            if (!this.remappingUtils.isAvailable()) {
                return false;
            }
            this.remappingUtils.forceInitialization();
            try {
                this.customEntityBaseClass = Class.forName("org.magmafoundation.magma.entity.CraftCustomEntity");
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
            return true;
        }
    }

    @Override
    public String getServerName() {
        return "Magma (Legacy)";
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
        path = this.remappingUtils.mapClassName(path);
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
        return this.remappingUtils.mapMethodName(type, methodName, params);
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        String result;
        block1: {
            result = this.remappingUtils.mapFieldName(type, fieldName);
            if (result != fieldName || !MPLType.getName(type).startsWith("net.minecraft.")) break block1;
            while ((type = type.getSuperclass()) != null && type != Object.class && (result = this.remappingUtils.mapFieldName(type, fieldName)) == fieldName) {
            }
        }
        return result;
    }

    @Override
    public boolean isCustomEntityType(EntityType entityType) {
        Class entityClass = entityType.getEntityClass();
        return this.customEntityBaseClass != null && entityClass != null && this.customEntityBaseClass.isAssignableFrom(entityClass);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "magma");
        variables.put("forge_nms_obfuscated", "true");
    }

    @Template.Optional
    @Template.InstanceType(value="org.magmafoundation.magma.remapper.utils.RemappingUtils")
    public static abstract class RemappingUtilsClass
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static String mapClassName(String className) {\n    if (className.startsWith(\"net.minecraft.server.\")) {\n        org.magmafoundation.magma.remapper.mappingsModel.ClassMappings mapping;\n        mapping = (org.magmafoundation.magma.remapper.mappingsModel.ClassMappings) RemappingUtils.jarMapping.byNMSName.get(className);\n        if (mapping != null) {\n            return mapping.getMcpName();\n        } else {\n            // Magma BUGFIX!!!\n            // If we do not do this, it will suffer a NPE in the PluginClassLoader\n            return \"missing.type.\" + className;\n        }\n               }\n    return className;\n}")
        public abstract String mapClassName(String var1);

        @Template.Generated(value="public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> var1, String var2, Class<?>[] var3);

        @Template.Generated(value="public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> var1, String var2);
    }
}

