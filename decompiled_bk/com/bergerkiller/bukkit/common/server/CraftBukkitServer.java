/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangRemapper;
import com.bergerkiller.bukkit.common.internal.cdn.SpigotMappings;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.bukkit.common.server.MinecraftVersionDiscovery;
import com.bergerkiller.bukkit.common.server.VersionIdentificationFailureException;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Bukkit;

public class CraftBukkitServer
extends CommonServerBase
implements MethodNameResolver,
FieldNameResolver,
FieldAliasResolver,
MethodAliasResolver,
ClassPathResolver {
    private static final String PACKAGE_CB_ROOT = "org.bukkit.craftbukkit";
    private static final String PACKAGE_NMS_ROOT = "net.minecraft.server";
    private static final String CB_ROOT = "org.bukkit.craftbukkit.";
    private static final String NM_ROOT = "net.minecraft.";
    public String PACKAGE_VERSION;
    public String MC_VERSION;
    public String NMS_ROOT_VERSIONED;
    public String CB_ROOT_VERSIONED;
    public String CB_ROOT_LIBS;
    private boolean HAS_MOJANG_FIELD_MAPPINGS = false;
    private boolean HAS_MOJANG_METHOD_MAPPINGS = false;
    private boolean MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT = false;
    private boolean REMAP_TO_NMS = false;
    private MojangRemapper mojangRemapper = null;
    private SpigotMappings.ClassMappings mojangToSpigotClassRemapper = null;
    private boolean isInitializingMojangSpigotRemapper = false;
    private Map<String, String> remappings = Collections.emptyMap();

    @Override
    public boolean init() {
        if (SERVER_CLASS == null) {
            return false;
        }
        this.CB_ROOT_VERSIONED = SERVER_CLASS.getPackage().getName();
        this.CB_ROOT_LIBS = "org.bukkit.craftbukkit.libs";
        if (this.CB_ROOT_VERSIONED.startsWith(CB_ROOT)) {
            this.PACKAGE_VERSION = this.CB_ROOT_VERSIONED.substring(CB_ROOT.length());
            this.NMS_ROOT_VERSIONED = "net.minecraft.server." + this.PACKAGE_VERSION;
        } else {
            this.PACKAGE_VERSION = "";
            this.NMS_ROOT_VERSIONED = PACKAGE_NMS_ROOT;
        }
        this.MC_VERSION = this.PACKAGE_VERSION;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void postInit(CommonServer.PostInitEvent event) {
        try {
            this.MC_VERSION = new MinecraftVersionDiscovery(this.PACKAGE_VERSION).detect();
        }
        catch (VersionIdentificationFailureException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new VersionIdentificationFailureException(t);
        }
        boolean isUnobfuscatedMojangMapJar = false;
        if (TextValueSequence.evaluateText(this.MC_VERSION, ">=", "26.1")) {
            isUnobfuscatedMojangMapJar = true;
        } else if (TextValueSequence.evaluateText(this.MC_VERSION, ">=", "1.17")) {
            Class<?> mojangClass = null;
            Class<?> spigotClass = null;
            try {
                mojangClass = Resolver.getClassByExactName("net.minecraft.server.level.ServerPlayer");
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                spigotClass = Resolver.getClassByExactName("net.minecraft.server.level.EntityPlayer");
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (mojangClass != null && spigotClass == null) {
                boolean hasMojangMethod = false;
                try {
                    MPLType.getDeclaredMethod(mojangClass, "getCamera", new Class[0]);
                    hasMojangMethod = true;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (hasMojangMethod) {
                    isUnobfuscatedMojangMapJar = true;
                }
            }
        }
        if (isUnobfuscatedMojangMapJar) {
            this.MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT = false;
            this.HAS_MOJANG_FIELD_MAPPINGS = false;
            this.HAS_MOJANG_METHOD_MAPPINGS = false;
            this.REMAP_TO_NMS = false;
        } else {
            this.MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT = true;
            this.HAS_MOJANG_FIELD_MAPPINGS = TextValueSequence.evaluateText(this.MC_VERSION, ">=", "1.17");
            this.HAS_MOJANG_METHOD_MAPPINGS = TextValueSequence.evaluateText(this.MC_VERSION, ">=", "1.18");
            this.REMAP_TO_NMS = TextValueSequence.evaluateText(this.MC_VERSION, "<", "1.17");
        }
        if (!event.getResolver().isSupported(this.MC_VERSION)) {
            event.signalIncompatible("Minecraft " + this.MC_VERSION + " is not supported!");
        }
        if (!isUnobfuscatedMojangMapJar) {
            this.mojangToSpigotClassRemapper = SpigotMappings.forVersion(this.MC_VERSION);
            if (this.HAS_MOJANG_FIELD_MAPPINGS || this.HAS_MOJANG_METHOD_MAPPINGS) {
                try {
                    this.isInitializingMojangSpigotRemapper = true;
                    this.mojangRemapper = MojangRemapper.load(this.MC_VERSION, this);
                    try {
                        Class<?> declaringClass = Resolver.getClassByExactName(this.resolveClassPath("net.minecraft.server.level.ServerPlayer"));
                        this.mojangRemapper.removeMethodMapping(declaringClass, "nextContainerCounter", new Class[0]);
                    }
                    catch (ClassNotFoundException ex) {
                        Logging.LOGGER.severe("Mapping filter fail: declaring class net.minecraft.server.level.ServerPlayer not found");
                        this.isInitializingMojangSpigotRemapper = false;
                        return;
                    }
                }
                finally {
                    this.isInitializingMojangSpigotRemapper = false;
                }
            }
        }
    }

    @Override
    public String resolveClassPath(String path) {
        if (this.isInitializingMojangSpigotRemapper) {
            if (this.mojangToSpigotClassRemapper != null) {
                return this.mojangToSpigotClassRemapper.toSpigot(path);
            }
            return path;
        }
        path = this.remappings.getOrDefault(path, path);
        if (this.mojangToSpigotClassRemapper != null) {
            path = this.mojangToSpigotClassRemapper.toSpigot(path);
        }
        if (path.startsWith(CB_ROOT) && !path.startsWith(this.CB_ROOT_VERSIONED) && !path.startsWith(this.CB_ROOT_LIBS)) {
            path = this.CB_ROOT_VERSIONED + path.substring(PACKAGE_CB_ROOT.length());
        }
        if (this.REMAP_TO_NMS && path.startsWith(NM_ROOT) && !path.startsWith(this.NMS_ROOT_VERSIONED)) {
            int index = path.length();
            String remapped = path;
            boolean isLastPart = true;
            while ((index = path.lastIndexOf(46, index - 1)) != -1) {
                if (isLastPart) {
                    isLastPart = false;
                } else if (index < path.length() && !Character.isUpperCase(path.charAt(index + 1))) break;
                remapped = this.NMS_ROOT_VERSIONED + path.substring(index);
            }
            path = remapped;
        }
        return path;
    }

    @Override
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        if (this.HAS_MOJANG_FIELD_MAPPINGS) {
            fieldName = this.mojangRemapper.remapFieldName(declaringClass, fieldName, fieldName);
        }
        return fieldName;
    }

    @Override
    public String resolveFieldAlias(Field field, String name) {
        if (this.HAS_MOJANG_FIELD_MAPPINGS) {
            return this.mojangRemapper.remapFieldNameReverse(field.getDeclaringClass(), name, null);
        }
        return null;
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        if (this.HAS_MOJANG_METHOD_MAPPINGS) {
            return this.mojangRemapper.remapMethodName(declaringClass, methodName, parameterTypes, methodName);
        }
        return methodName;
    }

    @Override
    public String resolveMethodAlias(Method method, String name) {
        if (this.HAS_MOJANG_METHOD_MAPPINGS) {
            return this.mojangRemapper.remapMethodNameReverse(method.getDeclaringClass(), name, method.getParameterTypes(), null);
        }
        return null;
    }

    @Override
    public String getMinecraftVersion() {
        return this.MC_VERSION;
    }

    @Override
    public String getServerVersion() {
        return (this.PACKAGE_VERSION.isEmpty() ? "(Unknown)" : this.PACKAGE_VERSION) + " (Minecraft " + this.MC_VERSION + ")";
    }

    @Override
    public boolean isForgeServer() {
        return false;
    }

    @Override
    public boolean isMojangMappings() {
        return !this.MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT;
    }

    @Override
    public String getServerDescription() {
        String desc = Bukkit.getServer().getVersion();
        desc = desc.replace(" (MC: " + this.MC_VERSION + ")", "");
        return desc;
    }

    @Override
    public String getServerName() {
        return "CraftBukkit";
    }

    @Override
    public String getNMSRoot() {
        return this.NMS_ROOT_VERSIONED;
    }

    @Override
    public String getCBRoot() {
        return this.CB_ROOT_VERSIONED;
    }

    public void setEarlyRemappings(Map<String, String> remappings) {
        this.remappings = remappings;
    }
}

