/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.permissions.Permission
 *  org.bukkit.permissions.PermissionDefault
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.internal.CommonClassManipulation;
import com.bergerkiller.bukkit.common.internal.CommonDependencyStartupLogHandler;
import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import com.bergerkiller.bukkit.common.io.ClassRewriter;
import com.bergerkiller.bukkit.common.localization.ILocalizationDefault;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.metrics.Metrics;
import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.reflection.org.bukkit.BPluginDescriptionFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginBase
extends JavaPlugin {
    private String disableMessage;
    private String enableMessage;
    private FileConfiguration permissionconfig;
    private FileConfiguration localizationconfig;
    final PluginLoaderHandler pluginLoaderHandler;
    private BasicConfiguration pluginYamlBKCL = null;
    private final CommonDependencyStartupLogHandler.PluginBaseHandler startupLogHandler = CommonDependencyStartupLogHandler.bindSelf(this);
    private boolean enabled = false;
    private boolean wasDisableRequested = false;
    private Metrics metrics;

    public PluginBase() {
        this.pluginLoaderHandler = PluginLoaderHandler.createFor((Plugin)this);
        this.startupLogHandler.setHastebinServer(this.pluginLoaderHandler.getPluginConfig().getString("preloader.hastebinServer", "https://hastebin.com"));
        this.pluginLoaderHandler.bootstrap();
    }

    public ModuleLogger getModuleLogger(String ... modulePath) {
        return new ModuleLogger((Plugin)this, modulePath);
    }

    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    public void logAction(CommandSender by, String action) {
        if (by instanceof Player) {
            this.log(Level.INFO, ((Player)by).getName() + " " + action);
        }
    }

    public final String getVersion() {
        return this.getDescription().getVersion();
    }

    public int getVersionNumber() {
        return CommonMethods.parseVersionNumber(this.getVersion());
    }

    public File getDataFile(String ... path) {
        if (path == null || path.length == 0) {
            return this.getDataFolder();
        }
        return new File(this.getDataFolder(), StringUtil.join(File.separator, path));
    }

    public MapTexture loadTexture(String filename) {
        return MapTexture.loadPluginResource(this, filename);
    }

    public static Permission getPermission(String path) {
        return CommonPlugin.getInstance().getPermissionHandler().getOrCreatePermission(path);
    }

    public final ConfigurationNode getPermissionNode(String path) {
        return this.permissionconfig.getNode(path);
    }

    public final ConfigurationNode getLocalizationNode(String path) {
        return this.localizationconfig.getNode(path);
    }

    public final void register(String ... commands) {
        this.register((CommandExecutor)this, commands);
    }

    public final void register(CommandExecutor executor, String ... commands) {
        for (String command : commands) {
            PluginCommand cmd = this.getCommand(command);
            if (cmd == null) continue;
            cmd.setExecutor(executor);
        }
    }

    public final void register(Listener listener) {
        if (listener == null) {
            throw new RuntimeException("Can not load a listener: The listener instance is null");
        }
        if (listener != this) {
            Bukkit.getPluginManager().registerEvents(listener, (Plugin)this);
        }
    }

    public final void register(Class<? extends Listener> listener) {
        if (listener == null) {
            throw new RuntimeException("Can not load a listener: The listener class is null");
        }
        try {
            this.register(listener.newInstance());
        }
        catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register listener " + listener, t);
        }
    }

    public final void register(PacketMonitor packetMonitor, PacketType ... packetTypes) {
        PacketUtil.addPacketMonitor((Plugin)this, packetMonitor, packetTypes);
    }

    public final void register(PacketListener packetListener, PacketType ... packetTypes) {
        PacketUtil.addPacketListener((Plugin)this, packetListener, packetTypes);
    }

    public final void unregister(PacketListener packetListener) {
        PacketUtil.removePacketListener(packetListener);
    }

    public final void loadPermissions(Class<? extends IPermissionDefault> permissionDefaults) {
        for (IPermissionDefault def : CommonUtil.getClassConstants(permissionDefaults, IPermissionDefault.class)) {
            this.loadPermission(def);
        }
    }

    public final Permission loadPermission(IPermissionDefault permissionDefault) {
        return this.loadPermission(permissionDefault.getName(), permissionDefault.getDefault(), permissionDefault.getDescription());
    }

    public final Permission loadPermission(String path) {
        return this.loadPermission(PluginBase.getPermission(path));
    }

    public final Permission loadPermission(Permission permission) {
        return this.loadPermission(permission.getName(), permission.getDefault(), permission.getDescription());
    }

    public final Permission loadPermission(String path, PermissionDefault def, String description) {
        return this.loadPermission(this.getPermissionNode(path), def, description);
    }

    public final Permission loadPermission(ConfigurationNode node, PermissionDefault def, String description) {
        Permission permission = PluginBase.getPermission(node.getPath());
        permission.setDefault(node.get("default", def));
        permission.setDescription(node.get("description", description));
        return permission;
    }

    public void loadLocales(Class<? extends ILocalizationDefault> localizationDefaults) {
        for (ILocalizationDefault def : CommonUtil.getClassConstants(localizationDefaults)) {
            this.loadLocale(def);
        }
    }

    public void loadLocale(ILocalizationDefault localizationDefault) {
        localizationDefault.initDefaults(this.localizationconfig);
    }

    public void loadLocale(String path, String defaultValue) {
        if (!this.localizationconfig.contains(path = path.toLowerCase(Locale.ENGLISH))) {
            this.localizationconfig.set(path, (Object)defaultValue);
        }
    }

    private ConfigurationNode getCommandNode(String command) {
        command = command.toLowerCase(Locale.ENGLISH);
        String fullPath = "commands." + command;
        if (this.localizationconfig.isNode(fullPath)) {
            return this.localizationconfig.getNode(fullPath);
        }
        fullPath = "commands." + command.replace('.', ' ');
        if (this.localizationconfig.isNode(fullPath)) {
            return this.localizationconfig.getNode(fullPath);
        }
        return null;
    }

    public String getCommandUsage(String command) {
        ConfigurationNode node = this.getCommandNode(command);
        String defValue = "/" + command;
        if (node == null) {
            return defValue;
        }
        return node.get("usage", defValue);
    }

    public String getCommandDescription(String command) {
        ConfigurationNode node = this.getCommandNode(command);
        String defValue = "No description specified";
        if (node == null) {
            return "No description specified";
        }
        return node.get("description", "No description specified");
    }

    public String getLocale(String path, String ... arguments) {
        if (this.localizationconfig.isNode(path = path.toLowerCase(Locale.ENGLISH))) {
            String newPath = path + ".default";
            if (arguments.length > 0) {
                StringBuilder tmpPathBuilder = new StringBuilder(path);
                String tmpPath = path;
                for (String argument : arguments) {
                    tmpPathBuilder.append('.');
                    if (argument == null) {
                        tmpPathBuilder.append("null");
                    } else {
                        tmpPathBuilder.append(argument.toLowerCase(Locale.ENGLISH));
                    }
                    tmpPath = tmpPathBuilder.toString();
                    if (!this.localizationconfig.contains(tmpPath)) break;
                    newPath = tmpPath;
                }
            }
            path = newPath;
        }
        if (arguments.length > 0) {
            StringBuilder locale = new StringBuilder(this.localizationconfig.get(path, ""));
            for (int i = 0; i < arguments.length; ++i) {
                StringUtil.replaceAll(locale, "%" + i + "%", LogicUtil.fixNull(arguments[i], "null"));
            }
            return locale.toString();
        }
        return this.localizationconfig.get(path, String.class, "");
    }

    public void permissions() {
    }

    public void localization() {
    }

    public final String getDisableMessage() {
        return this.disableMessage;
    }

    public void setDisableMessage(String msg) {
        this.disableMessage = msg;
    }

    public final String getEnableMessage() {
        return this.enableMessage;
    }

    public void setEnableMessage(String msg) {
        this.enableMessage = msg;
    }

    public abstract int getMinimumLibVersion();

    public void handle(Throwable reason) {
        if (reason instanceof Exception) {
            this.getLogger().log(Level.SEVERE, reason.getMessage(), reason);
        } else if (reason instanceof OutOfMemoryError) {
            this.log(Level.SEVERE, "The server is running out of memory! Do something!");
        } else {
            String pluginCause = this.getName();
            if (CommonUtil.isInstance(reason, NoClassDefFoundError.class, NoSuchMethodError.class, NoSuchFieldError.class, IllegalAccessError.class)) {
                String fixedReason = StringUtil.trimStart(LogicUtil.fixNull(reason.getMessage(), ""), "tried to access ");
                String path = StringUtil.trimStart(fixedReason, "method ", "field ", "class ");
                if (path.startsWith(Common.NMS_ROOT)) {
                    this.log(Level.SEVERE, "This version of the plugin is incompatible with this Minecraft version:");
                } else if (path.startsWith(Common.CB_ROOT)) {
                    this.log(Level.SEVERE, "This version of the plugin is incompatible with this CraftBukkit implementation:");
                } else if (path.startsWith("org.bukkit")) {
                    this.log(Level.SEVERE, "This version of the plugin is incompatible with the current Bukkit API:");
                } else {
                    Plugin plugin = CommonUtil.getPluginByClass(path);
                    if (plugin == this) {
                        if (reason instanceof NoClassDefFoundError) {
                            this.log(Level.WARNING, "Class is missing (plugin was hot-swapped?): " + reason.getMessage());
                            return;
                        }
                        this.log(Level.SEVERE, "Encountered a compiler error");
                    } else {
                        String type = reason instanceof IllegalAccessError ? (fixedReason.startsWith("class ") ? "Class is inaccessible in" : (fixedReason.startsWith("method ") ? "Method is inaccessible in" : (fixedReason.startsWith("field ") ? "Field is inaccessible in" : "Something is inaccessible in"))) : (reason instanceof NoClassDefFoundError ? "Class is missing from" : (reason instanceof NoSuchMethodError ? "Method is missing from" : (reason instanceof NoSuchFieldError ? "Field is missing from" : "Something is missing from")));
                        if (plugin == null) {
                            this.getLogger().log(Level.SEVERE, type + " a dependency of this plugin", reason);
                            LinkedHashSet<String> dep = new LinkedHashSet<String>();
                            dep.add(this.getName());
                            dep.addAll(LogicUtil.fixNull(this.getDescription().getDepend(), Collections.EMPTY_LIST));
                            dep.addAll(LogicUtil.fixNull(this.getDescription().getSoftDepend(), Collections.EMPTY_LIST));
                            pluginCause = StringUtil.combineNames(dep);
                        } else {
                            pluginCause = this.getName() + " and " + plugin.getName();
                            this.log(Level.SEVERE, type + " dependency '" + plugin.getName() + "'");
                        }
                    }
                }
            } else {
                this.getLogger().log(Level.SEVERE, "Encountered a critical error", reason);
            }
            this.log(Level.SEVERE, "Please, check for an updated version of " + pluginCause + " before reporting this bug!");
        }
    }

    private static void setPermissions(ConfigurationNode node) {
        for (ConfigurationNode subNode : node.getNodes()) {
            PluginBase.setPermissions(subNode);
        }
        PermissionDefault def = (PermissionDefault)ParseUtil.convert((Object)PluginBase.getNodeStringValue(node, "default"), PermissionDefault.class);
        String desc = PluginBase.getNodeStringValue(node, "description");
        if (def != null || desc != null) {
            Permission permission = PluginBase.getPermission(node.getPath().toLowerCase(Locale.ENGLISH));
            if (def != null) {
                permission.setDefault(def);
            }
            if (desc != null) {
                permission.setDescription(desc);
            }
        }
    }

    private static String getNodeStringValue(ConfigurationNode node, String key) {
        Object value = node.get(key);
        if (value == null || value instanceof YamlNodeAbstract) {
            return null;
        }
        return value.toString();
    }

    public Metrics getMetrics() {
        if (this.metrics == null) {
            throw new RuntimeException("Metrics is not enabled or failed to initialize for this Plugin.");
        }
        return this.metrics;
    }

    public boolean hasMetrics() {
        return this.metrics != null;
    }

    /*
     * WARNING - void declaration
     */
    public final void onEnable() {
        void var7_17;
        boolean compatible;
        block26: {
            if (Bukkit.getPluginManager().getPermission("bkcommonlib.command.startuplog") == null) {
                Permission permission = new Permission("bkcommonlib.command.startuplog", "Use the startuplog subcommand to view the startup log of plugins", PermissionDefault.OP);
                Bukkit.getPluginManager().addPermission(permission);
            }
            compatible = false;
            try {
                compatible = Common.IS_COMPATIBLE;
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "An unexpected BKCommonLib initialization error occurred", t);
                if (!(this instanceof CommonPlugin)) break block26;
                this.onCriticalStartupFailure("Critical initialization error (unsupported server?)");
                return;
            }
        }
        if (!compatible && this instanceof CommonPlugin) {
            block27: {
                try {
                    this.enable();
                }
                catch (Throwable t) {
                    this.getLogger().log(Level.SEVERE, "An unexpected BKCommonLib initialization error occurred", t);
                    if (this.startupLogHandler.hasCriticalStartupFailure()) break block27;
                    this.onCriticalStartupFailure("Critical initialization error (unsupported server?)");
                }
            }
            return;
        }
        if (!compatible) {
            this.onCriticalStartupFailure("Installed BKCommonLib is not compatible with this server", Bukkit.getPluginManager().getPlugin("BKCommonLib"));
            return;
        }
        if (!(this instanceof CommonPlugin) && !CommonPlugin.hasInstance()) {
            this.onCriticalStartupFailure("BKCommonLib failed to enable, this plugin is disabled", Bukkit.getPluginManager().getPlugin("BKCommonLib"));
            return;
        }
        List<String> dependencies = LogicUtil.fixNull(this.getDescription().getDepend(), Collections.emptyList());
        dependencies.stream().map(arg_0 -> ((PluginManager)Bukkit.getPluginManager()).getPlugin(arg_0)).filter(Objects::nonNull).forEach(this.startupLogHandler::bindDependency);
        for (String dep : dependencies) {
            if (PluginLoaderHandler.isPluginFullyEnabled(dep)) continue;
            this.log(Level.SEVERE, "Could not enable '" + this.getName() + " v" + this.getVersion() + "' because dependency '" + dep + "' failed to enable!");
            this.log(Level.SEVERE, "Perhaps the dependency has to be updated? Please check the log for any errors related to " + dep);
            this.onCriticalStartupFailure("Plugin dependency '" + dep + "' failed to enable", Bukkit.getPluginManager().getPlugin(dep));
            return;
        }
        long startTime = System.currentTimeMillis();
        if (this.getMinimumLibVersion() > 20001) {
            this.log(Level.SEVERE, "Requires a newer BKCommonLib version, please update BKCommonLib to the latest version!");
            this.log(Level.SEVERE, "Verify that there is only one BKCommonLib.jar in the plugins folder before retrying");
            this.onCriticalStartupFailure("Plugin requires a newer version of BKCommonLib");
            return;
        }
        this.setDisableMessage(this.getName() + " disabled!");
        this.permissionconfig = new FileConfiguration(this, "PermissionDefaults.yml");
        if (this.permissionconfig.exists()) {
            this.loadPermissions();
        }
        this.permissionconfig.setHeader("Below are the default permissions set for plugin '" + this.getName() + "'.");
        this.permissionconfig.addHeader("These permissions are ignored if the permission is set for a group or player.");
        this.permissionconfig.addHeader("Use the defaults as a base to keep the permissions file small");
        this.permissionconfig.addHeader("Need help with this file? Please visit:");
        this.permissionconfig.addHeader("https://wiki.traincarts.net/p/BKCommonLib/PermissionDefaults");
        this.localizationconfig = new FileConfiguration(this, "Localization.yml");
        if (this.localizationconfig.exists()) {
            this.loadLocalization();
        }
        this.localizationconfig.setHeader("Below are the localization nodes set for plugin '" + this.getName() + "'.");
        this.localizationconfig.addHeader("For colors, use the & character followed up by 0 - F");
        this.localizationconfig.addHeader("Need help with this file? Please visit:");
        this.localizationconfig.addHeader("https://wiki.traincarts.net/p/BKCommonLib/Localization");
        HashMap commands = this.getDescription().getCommands();
        if (commands != null && BPluginDescriptionFile.commands.isValid()) {
            ConfigurationNode commandsNode = this.getLocalizationNode("commands");
            commands = new HashMap(commands);
            for (Map.Entry entry : commands.entrySet()) {
                ConfigurationNode node = commandsNode.getNode((String)entry.getKey());
                HashMap<String, Object> data = new HashMap<String, Object>((Map)entry.getValue());
                node.shareWithMap(data, "description", "No description specified");
                node.shareWithMap(data, "usage", "/" + (String)entry.getKey());
                entry.setValue(Collections.unmodifiableMap(data));
            }
            BPluginDescriptionFile.commands.set(this.getDescription(), Collections.unmodifiableMap(commands));
        }
        this.permissions();
        PluginBase.setPermissions(this.permissionconfig);
        if (!this.permissionconfig.isEmpty()) {
            this.savePermissions();
        }
        this.localization();
        if (!this.localizationconfig.isEmpty()) {
            this.saveLocalization();
        }
        try {
            YamlConfiguration pluginConfig = this.pluginLoaderHandler.getPluginConfig();
            ConfigurationSection bstatsConfig = pluginConfig.getConfigurationSection("bstats");
            if (bstatsConfig != null && bstatsConfig.getBoolean("enabled", false) && bstatsConfig.contains("plugin-id")) {
                int n = bstatsConfig.getInt("plugin-id");
                this.metrics = new Metrics(this, n);
                String pluginBuildCfg = pluginConfig.getString("build", "NO-CI");
                if (pluginBuildCfg.equals("") || pluginBuildCfg.equals("NO-CI")) {
                    pluginBuildCfg = "other";
                }
                String pluginBuild = pluginBuildCfg;
                this.metrics.addCustomChart(new Metrics.SimplePie("build", () -> pluginBuild));
                String version = this.getDescription().getVersion();
                this.metrics.addCustomChart(new Metrics.DrilldownPie("pluginBuildVersion", () -> Collections.singletonMap(version, Collections.singletonMap(pluginBuild, 1))));
            }
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize metrics for " + this.getName(), t);
        }
        try {
            this.wasDisableRequested = false;
            this.enable();
            if (this.hasCriticalStartupFailure() || this.wasDisableRequested) {
                return;
            }
            this.startupLogHandler.setNotStartupNextTick();
            this.enabled = true;
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "An error occurred while enabling, the plugin will be disabled:", t);
            this.onCriticalStartupFailure("An error occurred while enabling");
            return;
        }
        Plugin[] pluginArray = Bukkit.getPluginManager().getPlugins();
        int n = pluginArray.length;
        boolean bl = false;
        while (var7_17 < n) {
            Plugin plugin = pluginArray[var7_17];
            if (PluginLoaderHandler.isPluginFullyEnabled(plugin)) {
                this.pluginLoaderHandler.onPluginLoaded(plugin);
                this.updateDependency(plugin, plugin.getName(), true);
            }
            ++var7_17;
        }
        CommonPlugin.flushSaveOperations((Plugin)this);
        if (this.enableMessage != null) {
            this.log(Level.INFO, this.enableMessage);
        }
        this.log(Level.INFO, this.getName() + " version " + this.getDebugVersion() + " enabled! (" + MathUtil.round(0.001 * (double)(System.currentTimeMillis() - startTime), 3) + "s)");
    }

    public final void onDisable() {
        if (this.enabled) {
            for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                if (!plugin.isEnabled() || !CommonUtil.isDepending(plugin, (Plugin)this)) continue;
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        this.softDisable(false);
    }

    private void softDisable(boolean forceDisableMessage) {
        this.wasDisableRequested = true;
        boolean doDisableMessage = forceDisableMessage;
        if (this.enabled) {
            doDisableMessage = this.disableMessage != null;
            try {
                this.disable();
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "An error occurred while disabling:", t);
                doDisableMessage = false;
            }
            this.enabled = false;
        }
        if (this.metrics != null) {
            this.metrics.shutdown();
            this.metrics = null;
        }
        CommonPlugin.flushSaveOperations((Plugin)this);
        if (doDisableMessage) {
            this.getLogger().log(Level.INFO, this.disableMessage);
        }
    }

    public boolean hasCriticalStartupFailure() {
        return this.startupLogHandler.hasCriticalStartupFailure();
    }

    private void onCriticalStartupFailure(String reason, Plugin pluginCause) {
        CommonDependencyStartupLogHandler.PluginBaseHandler handler;
        if (pluginCause != null && pluginCause instanceof PluginBase && (handler = ((PluginBase)pluginCause).startupLogHandler).hasCriticalStartupFailure()) {
            this.onCriticalStartupFailure(reason + "\n" + pluginCause.getName() + ": " + handler.getCriticalStartupFailure());
            return;
        }
        this.onCriticalStartupFailure(reason);
    }

    protected void onCriticalStartupFailure(String reason) {
        List altCommands = Collections.emptyList();
        try {
            altCommands = this.getDescription().getCommands().keySet().stream().map(Bukkit::getPluginCommand).filter(Objects::nonNull).map(CommonDependencyStartupLogHandler.CriticalAltCommand::new).collect(Collectors.toCollection(ArrayList::new));
            List preloaderCommands = this.pluginLoaderHandler.getPluginConfig().getStringList("preloader.commands");
            if (preloaderCommands != null && !preloaderCommands.isEmpty()) {
                for (String preloaderCommand : preloaderCommands) {
                    boolean found = false;
                    for (CommonDependencyStartupLogHandler.CriticalAltCommand altCommand : altCommands) {
                        if (!altCommand.name.equalsIgnoreCase(preloaderCommand)) continue;
                        found = true;
                        break;
                    }
                    if (found) continue;
                    altCommands.add(new CommonDependencyStartupLogHandler.CriticalAltCommand(preloaderCommand));
                }
            }
        }
        catch (Throwable preloaderCommands) {
            // empty catch block
        }
        if (altCommands.isEmpty()) {
            altCommands.add(new CommonDependencyStartupLogHandler.CriticalAltCommand(this.getName().toLowerCase(Locale.ENGLISH)));
        }
        if (this instanceof CommonPlugin) {
            this.softDisable(true);
            CommonUtil.handlePostDisable((Plugin)this);
            this.startupLogHandler.criticalStartupFailure((CommonPlugin)this, reason, altCommands);
        } else {
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            CommonPlugin commonPlugin = (CommonPlugin)Bukkit.getPluginManager().getPlugin("BKCommonLib");
            if (commonPlugin != null && commonPlugin.isEnabled()) {
                this.startupLogHandler.criticalStartupFailure(commonPlugin, reason, altCommands);
            } else {
                this.startupLogHandler.stopReadingLogNow();
            }
        }
    }

    public boolean onVersionCommand(String command, CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + this.getName() + ": v" + this.getDebugVersion());
        sender.sendMessage(ChatColor.GREEN + "BKCommonLib: v" + CommonPlugin.getInstance().getDebugVersion());
        sender.sendMessage(ChatColor.GREEN + "Server: " + this.getServer().getName() + " " + this.getServer().getVersion());
        return true;
    }

    public boolean onStartupLogCommand(CommandSender sender, String command, String[] args) {
        this.startupLogHandler.handleStartupLogCommand(sender);
        return true;
    }

    public final boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
        try {
            String[] fixedArgs = StringUtil.convertArgs(args);
            if (fixedArgs.length >= 1 && LogicUtil.contains(fixedArgs[0].toLowerCase(Locale.ENGLISH), "version", "ver") && this.onVersionCommand(command, sender)) {
                return true;
            }
            if (fixedArgs.length >= 1 && fixedArgs[0].equalsIgnoreCase("startuplog") && this.onStartupLogCommand(sender, command, args)) {
                return true;
            }
            if (this.command(sender, command, fixedArgs)) {
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Unknown command, for help use /help " + command);
        }
        catch (NoPermissionException ex) {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            } else {
                sender.sendMessage("This command is only for players!");
            }
        }
        catch (Throwable t) {
            StringBuilder msg = new StringBuilder("Unhandled exception executing command '");
            msg.append(command).append("' in plugin ").append(this.getName()).append(" v").append(this.getVersion());
            Common.LOGGER.log(Level.SEVERE, msg.toString(), t);
            sender.sendMessage(ChatColor.RED + "An internal error occured while executing this command");
        }
        return true;
    }

    public abstract void enable();

    public abstract void disable();

    public abstract boolean command(CommandSender var1, String var2, String[] var3);

    public void rewriteClass(String name, ClassRewriter rewriter) {
        byte[] classBytes = CommonClassManipulation.readClassData(this.getClassLoader(), name);
        classBytes = rewriter.rewrite(this, name, classBytes);
        CommonClassManipulation.writeClassData(this.getClassLoader(), name, classBytes);
    }

    public final void loadLocalization() {
        this.localizationconfig.load();
    }

    public final BasicConfiguration getPluginYaml() {
        if (this.pluginYamlBKCL == null) {
            this.pluginYamlBKCL = new BasicConfiguration();
            this.pluginYamlBKCL.loadFromString(this.pluginLoaderHandler.getPluginConfigText());
        }
        return this.pluginYamlBKCL;
    }

    public final String getDebugVersion() {
        return this.pluginLoaderHandler.getDebugVersion();
    }

    public final String getDebugFullStartupLog() {
        return this.startupLogHandler.getFullStartupLog();
    }

    public final void saveLocalization() {
        this.localizationconfig.save();
    }

    public final void loadPermissions() {
        this.permissionconfig.load();
    }

    public final void savePermissions() {
        this.permissionconfig.save();
    }

    public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
    }
}

