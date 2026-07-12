/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.dimension.DimensionTypeHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DimensionResourceKeyConversion {
    private static Tracker TRACKER = null;
    private static final FastMethod<Object> registryGetByKeyMethod = new FastMethod();
    private static final FastMethod<Optional<?>> registryGetByValueMethod = new FastMethod();

    public static void init() throws Throwable {
        Class<?> minecraftServerType = Resolver.loadClass("net.minecraft.server.MinecraftServer", false);
        if (minecraftServerType == null) {
            throw new IllegalStateException("MinecraftServer type not found");
        }
        Class<?> registryType = Resolver.loadClass("net.minecraft.core.Registry", false);
        if (registryType == null) {
            throw new IllegalStateException("IRegistry type not found");
        }
        Class<?> resourceKeyType = Resolver.loadClass("net.minecraft.resources.ResourceKey", false);
        if (resourceKeyType == null) {
            throw new IllegalStateException("ResourceKey type not found");
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.2")) {
            registryGetByKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "getValue", resourceKeyType));
            registryGetByValueMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "getResourceKey", Object.class));
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.19")) {
            registryGetByKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "get", resourceKeyType));
            registryGetByValueMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "getResourceKey", Object.class));
        } else {
            registryGetByKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "a", resourceKeyType));
            registryGetByValueMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "c", Object.class));
        }
        registryGetByKeyMethod.forceInitialization();
        registryGetByValueMethod.forceInitialization();
    }

    @ConverterMethod(input="net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.DimensionType>")
    public static DimensionType toDimensionType(Object resourceKeyHandle) {
        Object registry = DimensionTypeHandle.getDimensionTypeRegistry();
        Object dimensionManagerHandle = registryGetByKeyMethod.invoke(registry, resourceKeyHandle);
        if (dimensionManagerHandle == null) {
            Tracker tracker = TRACKER;
            if (tracker != null) {
                dimensionManagerHandle = tracker.findDimensionManager(ResourceKey.fromResourceKeyHandle(resourceKeyHandle));
            }
            if (dimensionManagerHandle == null) {
                throw new IllegalArgumentException("Dimension key is not registered: " + resourceKeyHandle);
            }
        }
        return DimensionType.fromDimensionManagerHandle(dimensionManagerHandle);
    }

    @ConverterMethod(output="net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.DimensionType>")
    public static Object toResourceKey(DimensionType dimensionType) {
        ResourceKey<?> resourceKey;
        Object dimensionManagerHandle = dimensionType.getDimensionManagerHandle();
        Object registry = DimensionTypeHandle.getDimensionTypeRegistry();
        Optional<?> resourceKeyHandle = registryGetByValueMethod.invoke(registry, dimensionManagerHandle);
        if (resourceKeyHandle != null && resourceKeyHandle.isPresent()) {
            return resourceKeyHandle.get();
        }
        Tracker tracker = TRACKER;
        if (tracker != null && (resourceKey = tracker.findResourceKey(dimensionManagerHandle)) != null) {
            return resourceKey.getRawHandle();
        }
        throw new IllegalArgumentException("Dimension is not registered: " + dimensionManagerHandle);
    }

    public static class Tracker
    implements LibraryComponent {
        private final BiMap<Object, ResourceKey<?>> RESOURCE_KEY_BY_DIMENSION_MANAGER = HashBiMap.create();
        private final BiMap<ResourceKey<?>, Object> DIMENSION_MANAGER_BY_RESOURCE_KEY = this.RESOURCE_KEY_BY_DIMENSION_MANAGER.inverse();
        private final FastMethod<Object> getWorldTypeKeyMethod = new FastMethod();
        private final JavaPlugin plugin;
        private final Listener listener;

        public Tracker(JavaPlugin plugin) {
            this.plugin = plugin;
            this.listener = new Listener(){

                @EventHandler(priority=EventPriority.MONITOR)
                protected void onWorldInit(WorldInitEvent event) {
                    this.registerDimensionManager(ServerLevelHandle.fromBukkit(event.getWorld()));
                }

                @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
                protected void onWorldUnload(WorldUnloadEvent event) {
                    this.deregisterDimensionManager(ServerLevelHandle.fromBukkit(event.getWorld()));
                }
            };
        }

        @Override
        public void enable() throws Throwable {
            this.getWorldTypeKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(LevelHandle.T.getType(), "getTypeKey", new Class[0]));
            this.getWorldTypeKeyMethod.forceInitialization();
            for (World world : Bukkit.getWorlds()) {
                this.registerDimensionManager(ServerLevelHandle.fromBukkit(world));
            }
            Bukkit.getPluginManager().registerEvents(this.listener, (Plugin)this.plugin);
            TRACKER = this;
        }

        @Override
        public void disable() {
            TRACKER = null;
            CommonUtil.unregisterListener(this.listener);
        }

        public synchronized Object findDimensionManager(ResourceKey<?> resourceKey) {
            return this.DIMENSION_MANAGER_BY_RESOURCE_KEY.get(resourceKey);
        }

        public synchronized ResourceKey<?> findResourceKey(Object dimensionManager) {
            return (ResourceKey)this.RESOURCE_KEY_BY_DIMENSION_MANAGER.get(dimensionManager);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void registerDimensionManager(ServerLevelHandle world) {
            Object dimensionManager = world.getDimensionType().getDimensionManagerHandle();
            Object rawResourceKey = this.getWorldTypeKeyMethod.invoke(world.getRaw());
            ResourceKey resourceKey = ResourceKey.fromResourceKeyHandle(rawResourceKey);
            Tracker tracker = this;
            synchronized (tracker) {
                this.RESOURCE_KEY_BY_DIMENSION_MANAGER.forcePut(dimensionManager, resourceKey);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void deregisterDimensionManager(ServerLevelHandle world) {
            Object dimensionManager = world.getDimensionType().getDimensionManagerHandle();
            Tracker tracker = this;
            synchronized (tracker) {
                this.RESOURCE_KEY_BY_DIMENSION_MANAGER.remove(dimensionManager);
            }
        }
    }
}

