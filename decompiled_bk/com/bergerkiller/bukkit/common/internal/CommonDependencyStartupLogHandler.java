/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Hastebin;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.OverwritingCircularBuffer;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.CommonServerLogRecorder;
import com.bergerkiller.bukkit.common.logging.WeakLoggingHandler;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonDependencyStartupLogHandler
extends Handler {
    public static final String PERMISSION = "bkcommonlib.command.startuplog";
    private static final Object LOCK = new Object();
    private final Plugin plugin;
    private final List<CommonServerLogRecorder.FormattedLogRecord> startupLogRecords = new ArrayList<CommonServerLogRecorder.FormattedLogRecord>();
    private final StringBuilder startupLog = new StringBuilder();
    private final OverwritingCircularBuffer<LogRecord> lastLogRecords = OverwritingCircularBuffer.create(32);
    private final Set<CommonDependencyStartupLogHandler> startupLogSources = new HashSet<CommonDependencyStartupLogHandler>();
    private final Set<CommonDependencyStartupLogHandler> startupLogSinks = new HashSet<CommonDependencyStartupLogHandler>();
    protected boolean isStartup = true;
    protected volatile boolean lastLogRecordsChanged = false;

    protected CommonDependencyStartupLogHandler(Plugin plugin) {
        this.plugin = plugin;
        this.startupLogSources.add(this);
        this.startupLogSinks.add(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void publish(LogRecord j_record) {
        if (this.isStartup) {
            CommonServerLogRecorder.FormattedLogRecord record = CommonServerLogRecorder.FormattedLogRecord.format(j_record);
            Object object = LOCK;
            synchronized (object) {
                this.startupLogRecords.add(record);
                this.startupLogSinks.forEach(sink -> record.appendTo(sink.startupLog));
            }
        } else {
            this.startupLogSinks.forEach(sink -> {
                sink.lastLogRecords.add(j_record);
                sink.lastLogRecordsChanged = true;
            });
        }
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getFullStartupLog() {
        StringBuilder fullStartupLog = new StringBuilder();
        fullStartupLog.append("====================================================\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy O", Locale.ENGLISH);
        fullStartupLog.append("Date: ");
        fullStartupLog.append(ZonedDateTime.now().format(formatter));
        fullStartupLog.append('\n');
        fullStartupLog.append("Server: " + Bukkit.getServer().getVersion());
        fullStartupLog.append(" (JDK ");
        try {
            fullStartupLog.append(Runtime.version().toString());
        }
        catch (Throwable t) {
            fullStartupLog.append(System.getProperty("java.version"));
        }
        fullStartupLog.append(")\n");
        fullStartupLog.append("Relevant plugins:\n");
        this.startupLogSources.stream().map(CommonDependencyStartupLogHandler::getPlugin).forEachOrdered(plugin -> {
            fullStartupLog.append("  - ");
            fullStartupLog.append(plugin.getName());
            if (!plugin.isEnabled()) {
                fullStartupLog.append(" [DISABLED]");
            }
            fullStartupLog.append(' ');
            if (plugin instanceof PluginBase) {
                fullStartupLog.append(((PluginBase)((Object)plugin)).getDebugVersion());
            } else {
                fullStartupLog.append(plugin.getDescription().getVersion());
            }
            if (plugin.getClass().getClassLoader() instanceof URLClassLoader) {
                for (URL url : ((URLClassLoader)plugin.getClass().getClassLoader()).getURLs()) {
                    String file = url.getFile();
                    if (file.isEmpty()) continue;
                    fullStartupLog.append(" (jar: ").append(new File(file).getName()).append(')');
                }
            }
            fullStartupLog.append('\n');
        });
        fullStartupLog.append("====================================================\n\n");
        fullStartupLog.append("---- Plugin logs ----\n");
        Object t = LOCK;
        synchronized (t) {
            fullStartupLog.append(this.startupLog.toString());
            this.lastLogRecordsChanged = false;
            List<LogRecord> lastRecords = this.lastLogRecords.values();
            if (!lastRecords.isEmpty()) {
                ArrayList lastLogsFormatted = new ArrayList(lastRecords.size());
                lastRecords.forEach(r -> lastLogsFormatted.add(CommonServerLogRecorder.FormattedLogRecord.format(r)));
                Collections.sort(lastLogsFormatted);
                fullStartupLog.append("[...]\n");
                lastLogsFormatted.forEach(r -> r.appendTo(fullStartupLog));
            }
        }
        fullStartupLog.append("\n---- Miscellaneous server logs ----\n");
        if (CommonPlugin.hasInstance()) {
            CommonServerLogRecorder serverLogs = CommonPlugin.getInstance().getServerLogRecorder();
            serverLogs.getStartupLogRecords().forEach(r -> r.appendTo(fullStartupLog));
            List<CommonServerLogRecorder.FormattedLogRecord> runtimeLogs = serverLogs.getRuntimeLogRecords();
            if (!runtimeLogs.isEmpty()) {
                fullStartupLog.append("[...]\n");
                runtimeLogs.forEach(r -> r.appendTo(fullStartupLog));
            }
        }
        return fullStartupLog.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleAddDependency(CommonDependencyStartupLogHandler dependency) {
        Object object = LOCK;
        synchronized (object) {
            for (CommonDependencyStartupLogHandler sink : new ArrayList<CommonDependencyStartupLogHandler>(this.startupLogSinks)) {
                boolean changed = false;
                for (CommonDependencyStartupLogHandler source : dependency.startupLogSources) {
                    if (!sink.startupLogSources.add(source)) continue;
                    source.startupLogSinks.add(sink);
                    changed = true;
                }
                if (!changed) continue;
                sink.rebuildStartupLog();
            }
        }
    }

    private void rebuildStartupLog() {
        this.startupLog.setLength(0);
        this.startupLogSources.stream().flatMap(source -> source.startupLogRecords.stream()).sorted().forEachOrdered(record -> record.appendTo(this.startupLog));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public void bindDependency(Plugin plugin) {
        for (Handler handler : WeakLoggingHandler.unwrapHandlers(plugin.getLogger())) {
            if (!(handler instanceof CommonDependencyStartupLogHandler)) continue;
            this.handleAddDependency((CommonDependencyStartupLogHandler)handler);
            return;
        }
        if (plugin.isEnabled()) {
            CommonDependencyStartupLogHandler new_dep_handler = new CommonDependencyStartupLogHandler(plugin);
            WeakLoggingHandler.addHandler(plugin.getLogger(), new_dep_handler);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                new_dep_handler.isStartup = false;
            });
            this.handleAddDependency(new_dep_handler);
        }
    }

    public static PluginBaseHandler bindSelf(PluginBase plugin) {
        PluginBaseHandler handler = new PluginBaseHandler(plugin);
        WeakLoggingHandler.addHandler(plugin.getLogger(), handler);
        return handler;
    }

    public static class PluginBaseHandler
    extends CommonDependencyStartupLogHandler {
        private JavaPlugin pluginDelegate;
        private CompletableFuture<Hastebin.UploadResult> startupLogUpload = null;
        private String hastebinServer = "https://hastebin.com";
        private String mainCommand = null;
        private String criticalStartupFailure = null;

        protected PluginBaseHandler(PluginBase pluginBase) {
            super((Plugin)pluginBase);
            this.pluginDelegate = pluginBase;
        }

        public PluginBase getPlugin() {
            return (PluginBase)super.getPlugin();
        }

        public void setHastebinServer(String hastebinServer) {
            this.hastebinServer = hastebinServer;
        }

        public String getCriticalStartupFailure() {
            return this.criticalStartupFailure;
        }

        public boolean hasCriticalStartupFailure() {
            return this.criticalStartupFailure != null;
        }

        public void setNotStartupNextTick() {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.getPlugin(), () -> {
                this.isStartup = false;
            });
        }

        public void stopReadingLogNow() {
            this.isStartup = false;
            WeakLoggingHandler.removeHandler(this.getPlugin().getLogger(), this);
        }

        public void criticalStartupFailure(CommonPlugin commonPlugin, String reason, Collection<CriticalAltCommand> commands) {
            this.pluginDelegate = commonPlugin;
            this.criticalStartupFailure = reason;
            this.isStartup = false;
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)commonPlugin, this::stopReadingLogNow);
            Bukkit.getPluginManager().registerEvents(new Listener(){

                @EventHandler
                public void onPlayerJoin(PlayerJoinEvent event) {
                    if (mainCommand == null || !PluginBaseHandler.checkHasStartupLogPermission((CommandSender)event.getPlayer())) {
                        return;
                    }
                    String msg = ChatColor.RED + "Plugin " + this.getPlugin().getName() + " failed to enable - Check " + ChatColor.BOLD + ChatColor.UNDERLINE + "/" + mainCommand;
                    try {
                        ChatText formatted = ChatText.fromMessage(msg);
                        formatted = formatted.setClickableRunCommand("/" + mainCommand);
                        formatted.sendTo(event.getPlayer());
                    }
                    catch (Throwable t) {
                        event.getPlayer().sendMessage(msg);
                    }
                }
            }, (Plugin)this.pluginDelegate);
            CommandExecutor executor = new CommandExecutor(){

                public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                    if (args.length >= 1 && args[0].equalsIgnoreCase("startuplog")) {
                        this.handleStartupLogCommand(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Plugin " + this.getPlugin().getName() + " failed to enable!");
                        if (PluginBaseHandler.checkHasStartupLogPermission(sender)) {
                            this.tellAboutServerLog(sender, label);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Tell a server operator to fix this issue");
                        }
                    }
                    return true;
                }
            };
            try {
                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap)commandMapField.get(Bukkit.getPluginManager());
                List<String> conflicts = commands.stream().flatMap(c -> Stream.concat(MountiplexUtil.toStream(c.name), c.aliases.stream())).filter(name -> commandMap.getCommand(name) != null).collect(Collectors.toList());
                if (!conflicts.isEmpty()) {
                    Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                    knownCommandsField.setAccessible(true);
                    Map knownCommands = (Map)knownCommandsField.get(commandMap);
                    conflicts.forEach(knownCommands::remove);
                }
                for (CriticalAltCommand command : commands) {
                    if (this.mainCommand == null) {
                        this.mainCommand = command.name;
                    }
                    Constructor constr = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                    constr.setAccessible(true);
                    PluginCommand pluginCommand = (PluginCommand)constr.newInstance(command.name, this.pluginDelegate);
                    pluginCommand.setAliases(command.aliases);
                    pluginCommand.setDescription("Plugin " + this.getPlugin().getName() + " failed to enable!");
                    pluginCommand.setUsage("/" + command.name + " startuplog - Get plugin startup log");
                    pluginCommand.setExecutor(executor);
                    commandMap.register(this.pluginDelegate.getName(), (Command)pluginCommand);
                }
            }
            catch (Throwable t) {
                this.getPlugin().getLogger().log(Level.SEVERE, "Failed to register fallabck startup log handler commands", t);
            }
        }

        public void handleStartupLogCommand(final CommandSender sender) {
            if (!PluginBaseHandler.checkHasStartupLogPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "You have no permission! Ask an admin.");
                return;
            }
            Task delayedNotifyTask = new Task(this, this.pluginDelegate){
                final /* synthetic */ PluginBaseHandler this$0;
                {
                    this.this$0 = this$0;
                    super(plugin);
                }

                @Override
                public void run() {
                    sender.sendMessage(ChatColor.YELLOW + "Uploading startup log to paste server...");
                }
            }.start(10L);
            this.uploadStartupLog().thenAccept(result -> {
                delayedNotifyTask.stop();
                if (result.success()) {
                    sender.sendMessage(ChatColor.YELLOW + "Need help? Share this startup log URL with the developers!");
                    sender.sendMessage(ChatColor.YELLOW + "View the " + this.getPlugin().getName() + " startup log:");
                    this.tellAboutLogURL(sender, result.url());
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to upload startup log: " + result.error());
                }
            });
        }

        public void tellAboutServerLog(CommandSender sender, String label) {
            String[] failureList = this.criticalStartupFailure.split("\n");
            if (failureList.length == 1) {
                sender.sendMessage(ChatColor.RED + "Cause: " + this.criticalStartupFailure);
            } else {
                sender.sendMessage(ChatColor.RED + "Cause:");
                for (String failure : failureList) {
                    sender.sendMessage(ChatColor.RED + "  - " + failure);
                }
            }
            String logURL = this.getStartupLogURLIfAvailable();
            if (logURL != null) {
                sender.sendMessage(ChatColor.RED + "Check the server log, or view the plugin startup log:");
                this.tellAboutLogURL(sender, logURL);
            } else {
                sender.sendMessage(ChatColor.RED + "Check the server log, or use the following command:");
                String cmdText = ChatColor.WHITE.toString() + ChatColor.UNDERLINE + "/" + label + " startuplog";
                try {
                    ChatText formatted = ChatText.fromMessage(cmdText);
                    formatted = formatted.setClickableRunCommand("/" + label + " startuplog");
                    formatted.sendTo(sender);
                }
                catch (Throwable t) {
                    sender.sendMessage(cmdText);
                }
            }
        }

        public void tellAboutLogURL(CommandSender sender, String logURL) {
            String urlText = ChatColor.WHITE + "> " + ChatColor.UNDERLINE + logURL + ".txt";
            try {
                ChatText formatted = ChatText.fromMessage(urlText);
                formatted = formatted.setClickableURL(logURL + ".txt");
                formatted.sendTo(sender);
            }
            catch (Throwable t) {
                sender.sendMessage(urlText);
            }
        }

        public CompletableFuture<Hastebin.UploadResult> uploadStartupLog() {
            if (this.startupLogUpload == null || this.startupLogUpload.isDone() && !this.hasStartupLog()) {
                Hastebin hastebin = new Hastebin(this.pluginDelegate, this.hastebinServer);
                this.startupLogUpload = hastebin.upload(this.getFullStartupLog());
            }
            return this.startupLogUpload;
        }

        public String getStartupLogURLIfAvailable() {
            try {
                return this.hasStartupLog() ? this.startupLogUpload.get().url() : null;
            }
            catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }

        private boolean hasStartupLog() {
            if (this.lastLogRecordsChanged) {
                return false;
            }
            CompletableFuture<Hastebin.UploadResult> c = this.startupLogUpload;
            try {
                return c != null && c.isDone() && !c.isCompletedExceptionally() && c.get().success();
            }
            catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }

        private static boolean checkHasStartupLogPermission(CommandSender sender) {
            if (!(sender instanceof Player) || ((Player)sender).isOp()) {
                return true;
            }
            return sender.hasPermission(CommonDependencyStartupLogHandler.PERMISSION);
        }
    }

    public static class CriticalAltCommand {
        public final String name;
        public final List<String> aliases;

        public CriticalAltCommand(PluginCommand command) {
            this.name = command.getName();
            this.aliases = command.getAliases();
        }

        public CriticalAltCommand(String name) {
            this.name = name;
            this.aliases = Collections.emptyList();
        }
    }
}

