/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.server.VersionIdentificationFailureException;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Server;

class MinecraftVersionDiscovery {
    private final String NMS_ROOT_VERSIONED;
    private final String CB_ROOT_VERSIONED;
    private Class<?> typeMinecraftServer;
    private Class<?> typeWorldVersion;
    private Class<?> typeMinecraftVersion;
    private Class<?> typeSharedConstants;
    private Class<?> typeGameVersion;
    private Method getVersionMethod;
    private Method getMinecraftVersionMethodBukkitServer;

    public MinecraftVersionDiscovery(String packageVersion) {
        if (packageVersion.isEmpty()) {
            this.NMS_ROOT_VERSIONED = "net.minecraft.server";
            this.CB_ROOT_VERSIONED = "org.bukkit.craftbukkit";
        } else {
            this.NMS_ROOT_VERSIONED = "net.minecraft.server." + packageVersion;
            this.CB_ROOT_VERSIONED = "org.bukkit.craftbukkit." + packageVersion;
        }
        try {
            this.typeMinecraftServer = MinecraftVersionDiscovery.loadClass("net.minecraft.server.MinecraftServer");
        }
        catch (ClassNotFoundException ex) {
            this.typeMinecraftServer = MinecraftVersionDiscovery.tryLoadClass(this.NMS_ROOT_VERSIONED + ".MinecraftServer");
        }
        try {
            this.typeMinecraftVersion = MinecraftVersionDiscovery.loadClass("net.minecraft.DetectedVersion");
        }
        catch (ClassNotFoundException ex1) {
            try {
                this.typeMinecraftVersion = MinecraftVersionDiscovery.loadClass("net.minecraft.MinecraftVersion");
            }
            catch (ClassNotFoundException ex2) {
                this.typeMinecraftVersion = MinecraftVersionDiscovery.tryLoadClass(this.NMS_ROOT_VERSIONED + ".MinecraftVersion");
            }
        }
        try {
            this.typeSharedConstants = MinecraftVersionDiscovery.loadClass("net.minecraft.SharedConstants");
        }
        catch (ClassNotFoundException ex) {
            this.typeSharedConstants = MinecraftVersionDiscovery.tryLoadClass(this.NMS_ROOT_VERSIONED + ".SharedConstants");
        }
        this.typeWorldVersion = MinecraftVersionDiscovery.tryLoadClass("net.minecraft.WorldVersion");
        if (this.typeMinecraftServer != null) {
            try {
                this.getVersionMethod = this.typeMinecraftServer.getDeclaredMethod("getVersion", new Class[0]);
                if (!this.getVersionMethod.getReturnType().equals(String.class)) {
                    this.getVersionMethod = null;
                }
            }
            catch (NoSuchMethodException ex) {
                this.getVersionMethod = null;
            }
        } else {
            this.getVersionMethod = null;
        }
        this.typeGameVersion = MinecraftVersionDiscovery.tryLoadClass("com.mojang.bridge.game.GameVersion");
        try {
            this.getMinecraftVersionMethodBukkitServer = Server.class.getDeclaredMethod("getMinecraftVersion", new Class[0]);
            if (!this.getMinecraftVersionMethodBukkitServer.getReturnType().equals(String.class)) {
                this.getMinecraftVersionMethodBukkitServer = null;
            }
        }
        catch (NoSuchMethodException ex) {
            this.getMinecraftVersionMethodBukkitServer = null;
        }
    }

    public String detect() throws VersionIdentificationFailureException {
        Object gameVersion;
        Method m;
        if (Bukkit.getServer() != null && this.getMinecraftVersionMethodBukkitServer != null) {
            try {
                return (String)this.getMinecraftVersionMethodBukkitServer.invoke((Object)Bukkit.getServer(), new Object[0]);
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.WARNING, "An error occurred calling Server::getMinecraftVersion()", t);
            }
        }
        if (Bukkit.getServer() != null && this.getVersionMethod != null) {
            return MinecraftVersionDiscovery.cleanupVersion(this.detectUsingBukkitServerHandleGetVersion());
        }
        if (this.typeMinecraftVersion == null && this.typeSharedConstants == null && this.getVersionMethod != null) {
            String fromASM = ASMUtil.findStringConstantReturnedByMethod(this.getVersionMethod);
            if (fromASM != null) {
                return fromASM;
            }
            Logging.LOGGER.log(Level.WARNING, "Failed to detect version using ASM analysis of MinecraftServer::getVersion()");
        }
        if (Bukkit.getServer() != null && this.typeSharedConstants != null && (this.typeGameVersion != null || this.typeWorldVersion != null)) {
            m = this.findStaticGetGameVersionMethod(this.typeSharedConstants);
            if (m != null) {
                Bukkit.getVersion();
                gameVersion = null;
                try {
                    gameVersion = m.invoke(null, new Object[0]);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.WARNING, "An error occurred calling SharedConstants getGameVersion()", t);
                }
                if (gameVersion != null) {
                    return MinecraftVersionDiscovery.cleanupVersion(this.getGameVersionName(gameVersion));
                }
            } else {
                Logging.LOGGER.log(Level.WARNING, "Failed to find SharedConstants::getGameVersion()");
            }
        }
        if (this.typeMinecraftVersion != null && (this.typeGameVersion != null || this.typeWorldVersion != null)) {
            m = this.findStaticGetGameVersionMethod(this.typeMinecraftVersion);
            if (m != null) {
                gameVersion = null;
                try {
                    gameVersion = m.invoke(null, new Object[0]);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.WARNING, "An error occurred calling MinecraftVersion tryDetectVersion()", t);
                }
                if (gameVersion != null) {
                    return MinecraftVersionDiscovery.cleanupVersion(this.getGameVersionName(gameVersion));
                }
            } else if (this.typeGameVersion == null) {
                Logging.LOGGER.log(Level.WARNING, "Failed to find MinecraftVersion::tryDetectVersion()");
            }
        }
        if (this.getVersionMethod != null) {
            Class<?> dedicatedServerClass;
            try {
                dedicatedServerClass = MinecraftVersionDiscovery.loadClass("net.minecraft.server.dedicated.DedicatedServer");
            }
            catch (ClassNotFoundException ex) {
                try {
                    dedicatedServerClass = MinecraftVersionDiscovery.loadClass(this.NMS_ROOT_VERSIONED + ".DedicatedServer");
                }
                catch (ClassNotFoundException ex2) {
                    throw new VersionIdentificationFailureException("DedicatedServer class not found");
                }
            }
            Logging.LOGGER.warning("Failed to find Minecraft Version efficiently, falling back to slower null-constructing");
            ClassTemplate<?> nms_server_tpl = ClassTemplate.create(dedicatedServerClass);
            Object minecraftServerInstance = nms_server_tpl.newInstanceNull();
            try {
                return MinecraftVersionDiscovery.cleanupVersion((String)this.getVersionMethod.invoke(minecraftServerInstance, new Object[0]));
            }
            catch (Throwable t) {
                throw new VersionIdentificationFailureException(t);
            }
        }
        throw new VersionIdentificationFailureException("Failed to detect a way to retrieve the Minecraft Version information");
    }

    private String detectUsingBukkitServerHandleGetVersion() {
        Class<?> typeCraftServer;
        Server server = Bukkit.getServer();
        try {
            typeCraftServer = MinecraftVersionDiscovery.loadClass(this.CB_ROOT_VERSIONED + ".CraftServer");
        }
        catch (ClassNotFoundException ex) {
            typeCraftServer = server.getClass();
        }
        Method getServerMethod = null;
        for (Class<?> c = typeCraftServer; c != Object.class; c = c.getSuperclass()) {
            try {
                getServerMethod = Resolver.resolveAndGetDeclaredMethod(c, "getServer", new Class[0]);
                continue;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        if (getServerMethod == null) {
            throw new VersionIdentificationFailureException("CraftServer method getServer() was not found");
        }
        try {
            Object minecraftServerInstance = getServerMethod.invoke((Object)Bukkit.getServer(), new Object[0]);
            return (String)this.getVersionMethod.invoke(minecraftServerInstance, new Object[0]);
        }
        catch (Throwable t) {
            throw new VersionIdentificationFailureException(t);
        }
    }

    private static String cleanupVersion(String version) {
        if (version != null && version.endsWith("_unobfuscated")) {
            version = version.substring(0, version.length() - "_unobfuscated".length());
        }
        return version;
    }

    private Method findStaticGetGameVersionMethod(Class<?> type) {
        Method alternative = null;
        for (Method m : type.getDeclaredMethods()) {
            if (m.getParameterCount() != 0 || !Modifier.isStatic(m.getModifiers())) continue;
            Class<?> rtype = m.getReturnType();
            if (this.typeGameVersion != null && this.typeGameVersion.isAssignableFrom(rtype)) {
                return m;
            }
            if (this.typeWorldVersion == null || !this.typeWorldVersion.isAssignableFrom(rtype)) continue;
            alternative = m;
        }
        return alternative;
    }

    private String getGameVersionName(Object gameVersion) throws VersionIdentificationFailureException {
        if (this.typeGameVersion != null && this.typeGameVersion.isInstance(gameVersion)) {
            Method getNameMethod = null;
            try {
                getNameMethod = Resolver.resolveAndGetDeclaredMethod(this.typeGameVersion, "getName", new Class[0]);
                if (!getNameMethod.getReturnType().equals(String.class)) {
                    getNameMethod = null;
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            if (getNameMethod == null) {
                throw new VersionIdentificationFailureException("Method String GameVersion::getName() does not exist");
            }
            try {
                return (String)getNameMethod.invoke(gameVersion, new Object[0]);
            }
            catch (Throwable t) {
                throw new VersionIdentificationFailureException(t);
            }
        }
        if (this.typeWorldVersion != null && this.typeWorldVersion.isInstance(gameVersion)) {
            for (Method m : this.typeWorldVersion.getMethods()) {
                int mod;
                if (m.getReturnType() != String.class || m.getParameterCount() > 0 || Modifier.isStatic(mod = m.getModifiers()) || !Modifier.isPublic(mod)) continue;
                try {
                    String version = (String)m.invoke(gameVersion, new Object[0]);
                    if (!version.contains(".")) continue;
                    return version;
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.WARNING, "Failed to invoke " + m.getName() + " to get version", t);
                }
            }
            throw new VersionIdentificationFailureException("Could not identify version name getter in WorldVersion");
        }
        throw new VersionIdentificationFailureException("Unknown game version type: " + gameVersion.getClass().getName());
    }

    private static Class<?> tryLoadClass(String name) {
        try {
            return MinecraftVersionDiscovery.loadClass(name);
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Class<?> loadClass(String name) throws ClassNotFoundException {
        return MPLType.getClassByName(Resolver.resolveClassPath(name));
    }
}

