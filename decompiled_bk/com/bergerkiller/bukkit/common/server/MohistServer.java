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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bukkit.entity.EntityType;

public class MohistServer
extends SpigotServer
implements FieldNameResolver,
MethodNameResolver {
    private RemapUtilsClass remapUtils = null;
    private List<Class<?>> customEntityBaseClasses = new ArrayList();

    @Override
    public boolean init() {
        Class remapUtilsType;
        String namespace;
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("com.mohistmc.MohistMC");
            namespace = "com.mohistmc";
            remapUtilsType = RemapUtilsClassImpl.class;
        }
        catch (Throwable t) {
            try {
                Class.forName("red.mohist.Mohist");
                namespace = "red.mohist";
                remapUtilsType = RemapUtilsClassImplLegacy.class;
            }
            catch (Throwable t2) {
                return false;
            }
        }
        String mohistNamespace = namespace;
        Class mohistRemapUtilsType = remapUtilsType;
        this.remapUtils = Template.Class.create(mohistRemapUtilsType);
        if (!this.remapUtils.isAvailable()) {
            return false;
        }
        this.remapUtils.forceInitialization();
        Stream.of("CraftCustomEntity", "CraftCustomChestHorse", "CraftCustomAbstractHorse").forEach(n -> {
            try {
                Class<?> type = Class.forName(mohistNamespace + ".entity." + n);
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
        return "Mohist";
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
        if (path.equals("net.minecraft.server.level.ChunkMap$DistanceManager") && this.evaluateMCVersion(">=", "1.16.5")) {
            return this.tryResolveElse(path, "net.minecraft.world.server.ChunkManager$ProxyTicketManager");
        }
        if (path.equals("net.minecraft.server.level.ServerChunkCache$MainThreadExecutor") && this.evaluateMCVersion(">=", "1.16.5")) {
            return this.tryResolveElse(path, "net.minecraft.world.server.ServerChunkProvider$ChunkExecutor");
        }
        if (path.equals("net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData") && this.evaluateMCVersion(">=", "1.16.5")) {
            return this.tryResolveElse(path, "net.minecraft.world.biome.MobSpawnInfo$Spawners");
        }
        return this.resolveClassPathBase(path);
    }

    private String tryResolveElse(String path, String fallback) {
        String result = this.resolveClassPathBase(path);
        try {
            MPLType.getClassByName(result);
            return result;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return fallback;
        }
    }

    private String resolveClassPathBase(String path) {
        if (path.equals("net.minecraft.server.level.ServerChunkCache$MainThreadExecutor") && this.evaluateMCVersion(">=", "1.16.5")) {
            return "net.minecraft.world.server.ServerChunkProvider$ChunkExecutor";
        }
        String remappedPath = super.resolveClassPath(path);
        if ((remappedPath = this.remapUtils.mapClassName(remappedPath)) != null) {
            return remappedPath;
        }
        try {
            MPLType.getClassByName(path);
            return path;
        }
        catch (NullPointerException ex) {
            return "missing.type." + path;
        }
        catch (ClassNotFoundException e) {
            return "missing.type." + path;
        }
    }

    @Override
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        if (methodName.equals("a") && params.length == 1 && this.evaluateMCVersion(">=", "1.16.5") && MPLType.getName(type).equals("net.minecraft.item.crafting.Ingredient") && MPLType.getName(params[0]).equals("[Lnet.minecraft.util.IItemProvider;")) {
            for (Method m : type.getDeclaredMethods()) {
                if (!Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 1 || !m.getParameterTypes()[0].equals(params[0]) || m.getReturnType() != type) continue;
                return MPLType.getName(m);
            }
        }
        if (methodName.equals("a") && params.length == 2 && this.evaluateMCVersion(">=", "1.16.5") && MPLType.getName(type).equals("net.minecraft.util.NonNullList") && params[0].equals(Object.class) && params[1].equals(Object[].class)) {
            for (Method m : type.getDeclaredMethods()) {
                if (m.getParameterCount() != 2 || !m.getParameterTypes()[0].equals(params[0]) || !m.getParameterTypes()[1].equals(params[1]) || m.getReturnType() != type) continue;
                return MPLType.getName(m);
            }
        }
        return this.remapUtils.mapMethodName(type, methodName, params);
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        if (MPLType.getName(type).equals("net.minecraft.world.biome.MobSpawnInfo$Spawners") && this.evaluateMCVersion("==", "1.16.5")) {
            if (fieldName.equals("c")) {
                return "field_242588_c";
            }
            if (fieldName.equals("d")) {
                return "field_242589_d";
            }
            if (fieldName.equals("e")) {
                return "field_242590_e";
            }
        }
        return this.remapUtils.mapFieldName(type, fieldName);
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }
        return !classPath.startsWith("net.minecraft.");
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
        variables.put("forge", "mohist");
        if (this.evaluateMCVersion("<=", "1.12.2")) {
            variables.put("forge_nms_obfuscated", "true");
        }
    }

    public static abstract class RemapUtilsClass
    extends Template.Class<Template.Handle> {
        public abstract String mapClassName(String var1);

        public abstract String mapMethodName(Class<?> var1, String var2, Class<?>[] var3);

        public abstract String mapFieldName(Class<?> var1, String var2);
    }

    @Template.Optional
    @Template.InstanceType(value="com.mohistmc.bukkit.nms.utils.RemapUtils")
    public static abstract class RemapUtilsClassImpl
    extends RemapUtilsClass {
        @Override
        @Template.Generated(value="public static String mapClassName(String className) {\n    if (className.startsWith(\"net.minecraft.server.\")) {\n        com.mohistmc.bukkit.nms.model.ClassMapping mapping;\n        mapping = (com.mohistmc.bukkit.nms.model.ClassMapping) RemapUtils.jarMapping.byNMSName.get(className);\n        if (mapping != null) {\n            return mapping.getMcpName();\n        } else {\n            // Mohist BUGFIX!!!\n            // If we do not do this, it will suffer a NPE in the PluginClassLoader\n            return null;\n        }\n               }\n    return className;\n}")
        public abstract String mapClassName(String var1);

        @Override
        @Template.Generated(value="public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> var1, String var2, Class<?>[] var3);

        @Override
        @Template.Generated(value="public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> var1, String var2);
    }

    @Template.Optional
    @Template.InstanceType(value="red.mohist.bukkit.nms.utils.RemapUtils")
    public static abstract class RemapUtilsClassImplLegacy
    extends RemapUtilsClass {
        @Override
        @Template.Generated(value="public static String mapClassName(String className) {\n    if (className.startsWith(\"net.minecraft.server.\")) {\n        red.mohist.bukkit.nms.model.ClassMapping mapping;\n        mapping = (red.mohist.bukkit.nms.model.ClassMapping) RemapUtils.jarMapping.byNMSName.get(className);\n        if (mapping != null) {\n            return mapping.getMcpName();\n        } else {\n            // Mohist BUGFIX!!!\n            // If we do not do this, it will suffer a NPE in the PluginClassLoader\n            return null;\n        }\n               }\n    return className;\n}")
        public abstract String mapClassName(String var1);

        @Override
        @Template.Generated(value="public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> var1, String var2, Class<?>[] var3);

        @Override
        @Template.Generated(value="public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> var1, String var2);
    }
}

