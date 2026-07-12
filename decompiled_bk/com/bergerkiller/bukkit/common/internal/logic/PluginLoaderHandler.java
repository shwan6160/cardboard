/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandlerPaper;
import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandlerPaperFallback;
import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandlerSpigot;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginLoaderHandler {
    protected final Plugin plugin;
    protected final YamlConfiguration pluginConfig;
    private final String pluginConfigText;

    protected PluginLoaderHandler(Plugin plugin, String pluginConfigText) {
        this.plugin = plugin;
        this.pluginConfigText = pluginConfigText;
        this.pluginConfig = new YamlConfiguration();
        try {
            this.pluginConfig.loadFromString(pluginConfigText);
        }
        catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading plugin.yml resource for plugin " + plugin.getName() + ":", t);
        }
    }

    public abstract void bootstrap();

    public abstract void onPluginLoaded(Plugin var1);

    public abstract void addAccessToClassloader(Plugin var1);

    public abstract void addAccessToClassloaders(List<String> var1);

    public final String getPluginConfigText() {
        return this.pluginConfigText;
    }

    public final YamlConfiguration getPluginConfig() {
        return this.pluginConfig;
    }

    public final boolean hasPreloadingFailed() {
        if (!(this.plugin instanceof JavaPlugin) || !this.plugin.getClass().getSimpleName().equals("Preloader")) {
            return false;
        }
        String depExpectedMain = this.pluginConfig.getString("preloader.main", "");
        return !depExpectedMain.isEmpty() && !depExpectedMain.equals(this.plugin.getClass().getName());
    }

    public final String getDebugVersion() {
        String buildInfo = this.pluginConfig.getString("build", "");
        String debugVersion = this.plugin.getDescription().getVersion();
        if (buildInfo.length() > 0) {
            debugVersion = debugVersion + " (build: " + buildInfo + ")";
        }
        return debugVersion;
    }

    protected ClassLoader getClassLoader() {
        return this.plugin.getClass().getClassLoader();
    }

    public static PluginLoaderHandler createFor(Plugin plugin) {
        String paperPluginConfig;
        try {
            Class<?> paperLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
        }
        catch (Throwable t) {
            String config = PluginLoaderHandler.readPluginYAML(plugin, "plugin.yml");
            return new PluginLoaderHandlerSpigot(plugin, config);
        }
        try {
            paperPluginConfig = PluginLoaderHandler.readPluginYAML(plugin, "paper-plugin.yml");
        }
        catch (YamlNotReadException e) {
            String config = PluginLoaderHandler.readPluginYAML(plugin, "plugin.yml");
            return new PluginLoaderHandlerPaperFallback(plugin, config);
        }
        return new PluginLoaderHandlerPaper(plugin, paperPluginConfig);
    }

    public static boolean isPluginFullyEnabled(String pluginName) {
        return PluginLoaderHandler.isPluginFullyEnabled(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public static boolean isPluginFullyEnabled(Plugin plugin) {
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }
        if (plugin instanceof JavaPlugin && plugin.getClass().getSimpleName().equals("Preloader")) {
            try {
                if (PluginLoaderHandler.createFor(plugin).hasPreloadingFailed()) {
                    return false;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return true;
    }

    private static String readPluginYAML(Plugin plugin, String fileName) throws YamlNotReadException {
        String string;
        block17: {
            InputStream found_stream = null;
            if (plugin instanceof JavaPlugin) {
                try {
                    URL resource;
                    Method m = JavaPlugin.class.getDeclaredMethod("getClassLoader", new Class[0]);
                    m.setAccessible(true);
                    ClassLoader loader = (ClassLoader)m.invoke((Object)plugin, new Object[0]);
                    if (loader instanceof URLClassLoader && (resource = ((URLClassLoader)loader).findResource(fileName)) != null) {
                        URLConnection connection = resource.openConnection();
                        connection.setUseCaches(false);
                        found_stream = connection.getInputStream();
                    }
                }
                catch (Throwable m) {
                    // empty catch block
                }
            }
            if (found_stream == null) {
                try {
                    found_stream = plugin.getResource(fileName);
                }
                catch (Throwable t) {
                    throw new YamlNotReadException("Failed to read " + fileName, t);
                }
            }
            if (found_stream == null) {
                throw new YamlNotReadException("Failed to find " + fileName);
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
                if (stream == null) break block17;
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
                catch (Throwable t) {
                    throw new YamlNotReadException("Failed to read " + fileName, t);
                }
            }
            stream.close();
        }
        return string;
    }

    private static class YamlNotReadException
    extends IllegalStateException {
        public YamlNotReadException(String message) {
            super(message);
        }

        public YamlNotReadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

