/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

class PluginLoaderHandlerPaperFallback
extends PluginLoaderHandler {
    private final List<String> classDependPlugins;

    public PluginLoaderHandlerPaperFallback(Plugin plugin, String pluginConfigText) {
        super(plugin, pluginConfigText);
        List classDepend = this.pluginConfig.getStringList("classdepend");
        if (classDepend == null) {
            classDepend = Collections.emptyList();
        }
        this.classDependPlugins = classDepend;
    }

    @Override
    public void bootstrap() {
        this.addAccessToClassloaders(this.classDependPlugins);
    }

    @Override
    public void onPluginLoaded(Plugin plugin) {
        if (this.classDependPlugins.isEmpty()) {
            return;
        }
        if (this.classDependPlugins.contains(plugin.getName())) {
            this.addAccessToClassloader(plugin);
            return;
        }
        for (String provides : plugin.getDescription().getProvides()) {
            if (!this.classDependPlugins.contains(provides)) continue;
            this.addAccessToClassloader(plugin);
            return;
        }
    }

    @Override
    public void addAccessToClassloader(Plugin plugin) {
        this.addPluginClassLoader(plugin.getClass().getClassLoader());
    }

    @Override
    public void addAccessToClassloaders(List<String> pluginNames) {
        try {
            Class<?> paperClassLoaderStorageType = Class.forName("io.papermc.paper.plugin.provider.classloader.PaperClassLoaderStorage");
            Object paperClassLoaderStorage = paperClassLoaderStorageType.getMethod("instance", new Class[0]).invoke(null, new Object[0]);
            Object globalGroup = paperClassLoaderStorage.getClass().getMethod("getGlobalGroup", new Class[0]).invoke(paperClassLoaderStorage, new Object[0]);
            globalGroup = this.unwrapLockingClassLoader(globalGroup);
            Class<?> simpleListPluginGroupType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.group.SimpleListPluginClassLoaderGroup");
            Method getClassLoadersMethod = simpleListPluginGroupType.getMethod("getClassLoaders", new Class[0]);
            List classloaders = (List)getClassLoadersMethod.invoke(globalGroup, new Object[0]);
            ArrayList<Object> loadersToAdd = new ArrayList<Object>();
            Class<?> configuredPluginClassLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
            Method pluginMetaMethod = configuredPluginClassLoaderType.getMethod("getConfiguration", new Class[0]);
            Class<?> pluginMetaType = Class.forName("io.papermc.paper.plugin.configuration.PluginMeta");
            Method pluginMetaGetNameMethod = pluginMetaType.getMethod("getName", new Class[0]);
            Method pluginMetaGetProvidesMethod = pluginMetaType.getMethod("getProvidedPlugins", new Class[0]);
            for (Object loader : classloaders) {
                Object config = pluginMetaMethod.invoke(loader, new Object[0]);
                String name = (String)pluginMetaGetNameMethod.invoke(config, new Object[0]);
                if (pluginNames.contains(name)) {
                    loadersToAdd.add(loader);
                    continue;
                }
                List provides = (List)pluginMetaGetProvidesMethod.invoke(config, new Object[0]);
                if (provides == null || provides.isEmpty()) continue;
                boolean hasProvideDependedOn = false;
                for (String provide : provides) {
                    if (!pluginNames.contains(provide)) continue;
                    hasProvideDependedOn = true;
                    break;
                }
                if (!hasProvideDependedOn) continue;
                loadersToAdd.add(loader);
            }
            loadersToAdd.forEach(this::addPluginClassLoader);
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to grant access to dependency classes using Paper API", t);
        }
    }

    private void addPluginClassLoader(Object configuredPluginClassLoader) {
        try {
            Object pluginClassLoaderGroup;
            Field classLoaderGroupField;
            Class<?> configuredPluginClassLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
            if (!configuredPluginClassLoaderType.isInstance(configuredPluginClassLoader)) {
                throw new IllegalStateException("Invalid plugin class loader type: " + configuredPluginClassLoader.getClass());
            }
            ClassLoader loader = this.getClassLoader();
            Class<?> pluginClassLoaderType = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            Class<?> paperPluginLoaderType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
            if (pluginClassLoaderType.isInstance(loader)) {
                classLoaderGroupField = pluginClassLoaderType.getDeclaredField("classLoaderGroup");
                classLoaderGroupField.setAccessible(true);
                pluginClassLoaderGroup = classLoaderGroupField.get(loader);
            } else if (paperPluginLoaderType.isInstance(loader)) {
                classLoaderGroupField = paperPluginLoaderType.getDeclaredField("group");
                classLoaderGroupField.setAccessible(true);
                pluginClassLoaderGroup = classLoaderGroupField.get(loader);
            } else {
                throw new IllegalStateException("Unknown loader type: " + loader.getClass().getName());
            }
            pluginClassLoaderGroup = this.unwrapLockingClassLoader(pluginClassLoaderGroup);
            Class<?> simpleListPluginGroupType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.group.SimpleListPluginClassLoaderGroup");
            if (!simpleListPluginGroupType.isInstance(pluginClassLoaderGroup)) {
                throw new IllegalStateException("Unsupported class loader group type: " + pluginClassLoaderGroup.getClass().getName());
            }
            Method getClassLoadersMethod = simpleListPluginGroupType.getMethod("getClassLoaders", new Class[0]);
            List existingLoaders = (List)getClassLoadersMethod.invoke(pluginClassLoaderGroup, new Object[0]);
            if (existingLoaders.contains(configuredPluginClassLoader)) {
                return;
            }
            Method addMethod = simpleListPluginGroupType.getMethod("add", configuredPluginClassLoaderType);
            addMethod.invoke(pluginClassLoaderGroup, configuredPluginClassLoader);
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to grant access to dependency classes using Paper API", t);
        }
    }

    private Object unwrapLockingClassLoader(Object loader) {
        try {
            Class<?> lockingType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.group.LockingClassLoaderGroup");
            if (lockingType.isInstance(loader)) {
                return lockingType.getMethod("getParent", new Class[0]).invoke(loader, new Object[0]);
            }
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to unwrap LockingClassLoaderGroup", t);
        }
        return loader;
    }
}

