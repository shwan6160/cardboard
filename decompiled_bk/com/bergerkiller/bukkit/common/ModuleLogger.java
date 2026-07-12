/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ModuleLogger
extends Logger {
    private final String[] modulePath;
    private final String prefix;
    private final HashSet<String> logOnceSet = new HashSet();

    private static Logger createDefaultLogger(String[] modulePath) {
        Plugin plugin;
        if (modulePath != null && modulePath.length > 0 && Bukkit.getServer() != null && Bukkit.getServer().getPluginManager() != null && (plugin = Bukkit.getServer().getPluginManager().getPlugin(modulePath[0])) != null) {
            return plugin.getLogger();
        }
        if (Bukkit.getServer() != null && Bukkit.getLogger() != MountiplexUtil.LOGGER) {
            return Bukkit.getLogger();
        }
        Logger log = Logger.getLogger("");
        log.setUseParentHandlers(false);
        CustomRecordFormatter formatter = new CustomRecordFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        for (Handler iHandler : log.getHandlers()) {
            log.removeHandler(iHandler);
        }
        log.addHandler(consoleHandler);
        return log;
    }

    public ModuleLogger(String ... modulePath) {
        this(ModuleLogger.createDefaultLogger(modulePath), modulePath);
    }

    public ModuleLogger(Plugin plugin, String ... modulePath) {
        this(plugin.getLogger(), LogicUtil.appendArray(new String[]{ModuleLogger.getPrefix(plugin)}, modulePath));
    }

    public ModuleLogger(Logger parent, String ... modulePath) {
        super(StringUtil.join(".", modulePath), null);
        this.setParent(parent);
        this.setLevel(Level.ALL);
        this.setUseParentHandlers(true);
        this.modulePath = modulePath;
        this.prefix = "";
    }

    private static String getPrefix(Plugin plugin) {
        return LogicUtil.fixNull(plugin.getDescription().getPrefix(), plugin.getDescription().getName());
    }

    public ModuleLogger getModule(String ... path) {
        return new ModuleLogger(this.getParent(), LogicUtil.appendArray(this.modulePath, path));
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(this.prefix + logRecord.getMessage());
        super.log(logRecord);
    }

    public void trace(String message) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length >= 3) {
            this.log(Level.INFO, stack[2].getMethodName() + " " + message + " (" + stack[2].getFileName() + ":" + stack[2].getLineNumber() + ")");
        }
    }

    public void trace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length >= 3) {
            this.log(Level.INFO, stack[2].getMethodName() + " (" + stack[2].getFileName() + ":" + stack[2].getLineNumber() + ")");
        }
    }

    public void warnOnce(String message) {
        this.once(Level.WARNING, message);
    }

    public void once(Level level, String message) {
        this.once(level, message, null);
    }

    public void once(Level level, String message, Throwable t) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String token = message;
        for (int i = 1; i < stack.length; ++i) {
            if (stack[i].getClassName().equals(ModuleLogger.class.getName())) continue;
            token = message + "  -  " + stack[i].toString();
            break;
        }
        String key = token;
        if (t != null) {
            key = key + t.toString();
        }
        if (this.logOnceSet.add(key)) {
            if (t != null) {
                Logging.LOGGER_DEBUG.log(level, token, t);
            } else {
                Logging.LOGGER_DEBUG.log(level, token);
            }
        }
    }

    public void handleReflectionMissing(String type, String name, Class<?> source) {
        String msg = type + " '" + name + "' does not exist in class file " + source.getName();
        Exception ex = new Exception(msg);
        for (StackTraceElement elem : ex.getStackTrace()) {
            if (!elem.getClassName().startsWith("com.bergerkiller.reflection")) continue;
            msg = msg + " (Update BKCommonLib?)";
            break;
        }
        this.log(Level.WARNING, msg, ex);
    }

    private static class CustomRecordFormatter
    extends Formatter {
        private CustomRecordFormatter() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String format(LogRecord r) {
            StringBuilder sb = new StringBuilder();
            sb.append("[" + r.getLevel().getName() + "] ");
            sb.append(this.formatMessage(r)).append(System.getProperty("line.separator"));
            if (null != r.getThrown()) {
                sb.append("Throwable occurred: ");
                Throwable t = r.getThrown();
                PrintWriter pw = null;
                try {
                    StringWriter sw = new StringWriter();
                    pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    sb.append(sw.toString());
                }
                finally {
                    if (pw != null) {
                        try {
                            pw.close();
                        }
                        catch (Exception exception) {}
                    }
                }
            }
            return sb.toString();
        }
    }
}

