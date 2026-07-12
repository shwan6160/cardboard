/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.plugin.java.JavaPluginLoader
 */
package com.bergerkiller.bukkit.tc;

import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class Preloader
extends JavaPlugin {
    private final String mainClassName;
    private final List<Depend> dependList;
    private final List<String> preloaderCommands;
    private final List<Depend> missingDepends = new ArrayList<Depend>();
    private String loadError = null;
    private JavaPlugin loadedPluginInstance = null;

    public Preloader() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(this.readPluginYAML((Plugin)this));
            ConfigurationSection preloaderConfig = config.getConfigurationSection("preloader");
            if (preloaderConfig == null) {
                throw new IllegalStateException("plugin.yml has no preloader configuration");
            }
            this.mainClassName = preloaderConfig.getString("main");
            if (this.mainClassName == null) {
                throw new IllegalStateException("plugin.yml preloader configuration declares no main class");
            }
            List dependConfigList = preloaderConfig.getList("depend");
            if (dependConfigList != null) {
                this.dependList = new ArrayList<Depend>(dependConfigList.size());
                for (Object dependItem : dependConfigList) {
                    Map dependConfig;
                    String name;
                    if (!(dependItem instanceof Map) || (name = (String)(dependConfig = (Map)dependItem).getOrDefault("name", null)) == null) continue;
                    String url = dependConfig.getOrDefault("url", "");
                    this.dependList.add(new Depend(name, url));
                }
            } else {
                ConfigurationSection dependConfig = preloaderConfig.getConfigurationSection("depend");
                if (dependConfig == null) {
                    this.dependList = Collections.emptyList();
                } else {
                    Set names = dependConfig.getKeys(false);
                    this.dependList = new ArrayList<Depend>(names.size());
                    for (String name : names) {
                        this.dependList.add(new Depend(name, dependConfig.getString(name)));
                    }
                }
            }
            List preloaderCommandsTmp = preloaderConfig.getStringList("commands");
            this.preloaderCommands = preloaderCommandsTmp == null || preloaderCommandsTmp.isEmpty() ? Collections.emptyList() : new ArrayList<String>(preloaderCommandsTmp);
        }
        catch (InvalidConfigurationException ex) {
            throw new IllegalStateException("Corrupt jar: Failed to load plugin.yml", ex);
        }
    }

    public void onLoad() {
        JavaPlugin mainPlugin;
        Class<?> mainClass;
        this.missingDepends.clear();
        for (Depend depend : this.dependList) {
            if (this.getServer().getPluginManager().getPlugin(depend.name) != null) continue;
            this.missingDepends.add(depend);
        }
        if (!this.missingDepends.isEmpty()) {
            return;
        }
        PluginDescriptionFile description = this.getDescription();
        ArrayList<String> newHardDepend = new ArrayList<String>(description.getDepend());
        for (Depend depend : this.dependList) {
            if (newHardDepend.contains(depend.name)) continue;
            newHardDepend.add(depend.name);
        }
        try {
            Field field = PluginDescriptionFile.class.getDeclaredField("depend");
            field.setAccessible(true);
            field.set(description, ImmutableList.copyOf(newHardDepend));
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to update depend list", t);
        }
        String pluginName = this.getName();
        if (this.loadedPluginInstance != null) {
            this.swapPluginFieldEverywhere(this, this.loadedPluginInstance, pluginName);
            return;
        }
        try {
            mainClass = this.getClassLoader().loadClass(this.mainClassName);
        }
        catch (ClassNotFoundException e) {
            this.getLogger().log(Level.SEVERE, "Failed to load the plugin main class", e);
            this.loadError = "Failed to load the plugin main class - check server log!";
            return;
        }
        this.setLoaderPluginField(null, pluginName);
        try {
            mainPlugin = (JavaPlugin)mainClass.newInstance();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to call plugin constructor", t);
            this.loadError = "Failed to call plugin constructor - check server log!";
            this.setLoaderPluginField(this, pluginName);
            return;
        }
        this.swapPluginFieldEverywhere(this, mainPlugin, pluginName);
        try {
            mainPlugin.onLoad();
            this.loadedPluginInstance = mainPlugin;
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "An error occurred during onLoad()", t);
            this.loadError = "Failed to load the plugin - check server log!";
            this.swapPluginFieldEverywhere(mainPlugin, this, pluginName);
            return;
        }
    }

    public void onEnable() {
        if (!this.missingDepends.isEmpty()) {
            this.missingDepends.forEach(depend -> {
                PluginDescriptionFile desc = this.getDescription();
                this.getLogger().log(Level.SEVERE, "Plugin " + desc.getName() + " " + desc.getVersion() + " requires plugin " + depend.name + " to be installed! But it is not!");
                if (!depend.url.isEmpty()) {
                    this.getLogger().log(Level.SEVERE, "Download " + depend.name + " from " + depend.url);
                }
            });
        }
        if (this.loadError == null && this.missingDepends.isEmpty()) {
            Plugin actualPluginInstance = this.getServer().getPluginManager().getPlugin(this.getName());
            if (actualPluginInstance != null && actualPluginInstance == this.loadedPluginInstance) {
                this.getLogger().log(Level.INFO, "[Preloader] Plugin is enabled in a weird way, perhaps a plugin reloader like Plugman was used?");
                try {
                    Method enableMethod = JavaPlugin.class.getDeclaredMethod("setEnabled", Boolean.TYPE);
                    enableMethod.setAccessible(true);
                    enableMethod.invoke((Object)actualPluginInstance, true);
                }
                catch (InvocationTargetException ex) {
                    Throwable t = ex.getCause() != null ? ex.getCause() : ex;
                    this.loadError = "Failed to enable the plugin - check server log!";
                    this.getLogger().log(Level.SEVERE, "[Preloader] Failed to enable the plugin", t);
                }
                catch (Throwable t) {
                    this.loadError = "Failed to enable the plugin - check server log!";
                    this.getLogger().log(Level.SEVERE, "[Preloader] Failed to enable the plugin", t);
                }
                return;
            }
            this.loadError = "Preloader failed to properly register the plugin on startup - check server log";
            this.getLogger().log(Level.SEVERE, this.loadError);
        }
        if (this.loadError != null) {
            this.getLogger().log(Level.SEVERE, "Not enabled because plugin could not be loaded! - Check server log");
        }
        this.preloaderCommands.forEach(commandName -> {
            try {
                Constructor constr = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constr.setAccessible(true);
                PluginCommand command = (PluginCommand)constr.newInstance(new Object[]{commandName, this});
                command.setDescription("Plugin " + this.getName() + " could not be loaded!");
                command.setExecutor((sender, label, e_cmd, args) -> {
                    this.showErrors(sender);
                    return true;
                });
                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap)commandMapField.get(Bukkit.getPluginManager());
                commandMap.register(this.getName(), (Command)command);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to register preloader fallback command " + commandName, t);
            }
        });
        this.getServer().getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                if (event.getPlayer().isOp()) {
                    Preloader.this.showErrors((CommandSender)event.getPlayer());
                }
            }
        }, (Plugin)this);
    }

    private boolean isEnabledNormally() {
        return this.loadError == null && this.missingDepends.isEmpty() && this.loadedPluginInstance != null && this.loadedPluginInstance.isEnabled();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (this.isEnabledNormally()) {
            return this.loadedPluginInstance.onCommand(sender, command, label, args);
        }
        this.showErrors(sender);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.isEnabledNormally() ? this.loadedPluginInstance.onTabComplete(sender, command, alias, args) : super.onTabComplete(sender, command, alias, args);
    }

    public PluginCommand getCommand(String name) {
        return this.isEnabledNormally() ? this.loadedPluginInstance.getCommand(name) : super.getCommand(name);
    }

    private void showErrors(CommandSender sender) {
        if (this.loadError != null) {
            sender.sendMessage(ChatColor.RED + "There was a fatal error initializing " + this.getName());
            sender.sendMessage(ChatColor.RED + this.loadError);
        } else {
            sender.sendMessage(ChatColor.RED + "Plugin " + this.getName() + " could not be enabled!");
            sender.sendMessage(ChatColor.RED + "Please install these additional dependencies:");
            for (Depend depend : this.missingDepends) {
                sender.sendMessage(ChatColor.RED + "  ======== " + depend.name + " ========");
                if (depend.url.isEmpty()) continue;
                sender.sendMessage(ChatColor.RED + "  > " + ChatColor.WHITE + ChatColor.UNDERLINE + depend.url);
            }
        }
    }

    private void swapPluginFieldEverywhere(JavaPlugin old_plugin, JavaPlugin plugin, String pluginName) {
        this.setLoaderPluginField(plugin, pluginName);
        PluginManager manager = this.getServer().getPluginManager();
        PluginManager paperManager = null;
        try {
            Field paperPluginManagerField = manager.getClass().getDeclaredField("paperPluginManager");
            paperPluginManagerField.setAccessible(true);
            paperManager = (PluginManager)paperPluginManagerField.get(manager);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (paperManager != null) {
            this.swapPluginFieldEverywherePaper(paperManager, old_plugin, plugin, pluginName);
            return;
        }
        this.swapPluginFieldEverywhereSpigot(manager, old_plugin, plugin, pluginName);
    }

    private void swapPluginFieldEverywhereSpigot(Object manager, JavaPlugin old_plugin, JavaPlugin plugin, String pluginName) {
        try {
            Field pluginsField = manager.getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);
            List plugins = (List)pluginsField.get(manager);
            int index = plugins.indexOf(old_plugin);
            if (index != -1) {
                plugins.set(index, plugin);
            } else if (!plugins.contains(plugin)) {
                throw new IllegalStateException("Preloader does not exist in plugins list");
            }
            Field lookupNamesField = manager.getClass().getDeclaredField("lookupNames");
            lookupNamesField.setAccessible(true);
            Map lookupNames = (Map)lookupNamesField.get(manager);
            boolean found = false;
            for (Map.Entry entry : lookupNames.entrySet()) {
                if (entry.getValue() == old_plugin) {
                    entry.setValue(plugin);
                    found = true;
                    continue;
                }
                if (entry.getValue() != plugin) continue;
                found = true;
            }
            if (!found) {
                throw new IllegalStateException("Preloader does not exist in lookupNames mapping");
            }
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "[Preloader] Failed to fully register the plugin into the server", t);
        }
    }

    private void swapPluginFieldEverywherePaper(PluginManager manager, JavaPlugin old_plugin, JavaPlugin plugin, String pluginName) {
        Object instanceManager;
        try {
            Field instanceManagerField = manager.getClass().getDeclaredField("instanceManager");
            instanceManagerField.setAccessible(true);
            instanceManager = instanceManagerField.get(manager);
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "[Preloader] Failed to fully register the plugin into the server", t);
            return;
        }
        this.swapPluginFieldEverywhereSpigot(instanceManager, old_plugin, plugin, pluginName);
    }

    private void setLoaderPluginField(JavaPlugin plugin, String pluginName) {
        ClassLoader loader = this.getClassLoader();
        try {
            Field pluginField = loader.getClass().getDeclaredField("plugin");
            pluginField.setAccessible(true);
            pluginField.set(loader, plugin);
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "[Preloader] Failed to update 'plugin' field", t);
        }
        try {
            Field pluginInitField = loader.getClass().getDeclaredField("pluginInit");
            pluginInitField.setAccessible(true);
            pluginInitField.set(loader, plugin);
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "[Preloader] Failed to update 'pluginInit' field", t);
        }
        boolean isPaperLoader = false;
        try {
            Class<?> paperLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
            isPaperLoader = paperLoaderType.isInstance(loader);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (isPaperLoader) {
            this.setLoaderPluginFieldPaper(loader, plugin, pluginName);
            return;
        }
        this.setLoaderPluginFieldSpigot(loader, plugin, pluginName);
    }

    private void setLoaderPluginFieldPaper(ClassLoader loader, JavaPlugin plugin, String pluginName) {
    }

    private void setLoaderPluginFieldSpigot(ClassLoader loader, JavaPlugin plugin, String pluginName) {
        block11: {
            try {
                Field globalLoaderField = loader.getClass().getDeclaredField("loader");
                globalLoaderField.setAccessible(true);
                JavaPluginLoader globalLoader = (JavaPluginLoader)globalLoaderField.get(loader);
                Field globalLoaderPluginLoadersField = JavaPluginLoader.class.getDeclaredField("loaders");
                globalLoaderPluginLoadersField.setAccessible(true);
                Object rawLoaders = globalLoaderPluginLoadersField.get(globalLoader);
                if (rawLoaders instanceof List) {
                    List pluginLoaders = (List)rawLoaders;
                    if (plugin == null) {
                        pluginLoaders.remove(loader);
                    } else if (!pluginLoaders.contains(loader)) {
                        pluginLoaders.add(loader);
                    }
                    break block11;
                }
                if (rawLoaders instanceof Map) {
                    Map pluginLoaders = (Map)rawLoaders;
                    if (plugin == null) {
                        if (pluginLoaders.get(pluginName) == loader) {
                            pluginLoaders.remove(pluginName);
                        }
                    } else if (pluginLoaders.get(pluginName) == null) {
                        pluginLoaders.put(pluginName, loader);
                    }
                    break block11;
                }
                throw new IllegalStateException("Unknown loaders field type: " + rawLoaders.getClass());
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "[Preloader] Failed to update class loader registry", t);
            }
        }
    }

    private String readPluginYAML(Plugin plugin) {
        String string;
        block15: {
            InputStream found_stream = null;
            if (plugin instanceof JavaPlugin) {
                try {
                    URL resource;
                    Method m = JavaPlugin.class.getDeclaredMethod("getClassLoader", new Class[0]);
                    m.setAccessible(true);
                    ClassLoader loader = (ClassLoader)m.invoke((Object)plugin, new Object[0]);
                    if (loader instanceof URLClassLoader && (resource = ((URLClassLoader)loader).findResource("plugin.yml")) != null) {
                        URLConnection connection = resource.openConnection();
                        connection.setUseCaches(false);
                        found_stream = connection.getInputStream();
                    }
                }
                catch (Throwable t) {
                    this.getLogger().log(Level.WARNING, "Error selecting plugin.yml of " + plugin.getName() + ", trying fallback", t);
                }
            }
            if (found_stream == null) {
                found_stream = plugin.getResource("plugin.yml");
            }
            if (found_stream == null) {
                throw new IllegalStateException("Failed to find plugin.yml");
            }
            InputStream stream = found_stream;
            try {
                int length;
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while ((length = stream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                string = new String(result.toByteArray(), StandardCharsets.UTF_8);
                if (stream == null) break block15;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ex) {
                    throw new IllegalStateException("Failed to read plugin.yml", ex);
                }
            }
            stream.close();
        }
        return string;
    }

    private static final class Depend {
        public final String name;
        public final String url;

        public Depend(String name, String url) {
            this.name = name.replace(' ', '_');
            this.url = url;
        }
    }
}

