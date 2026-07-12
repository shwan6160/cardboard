/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.spi.AbstractLogger
 */
package com.bergerkiller.bukkit.common.internal.logging;

import com.bergerkiller.bukkit.common.ModuleLogger;
import java.util.IdentityHashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

class CommonLog4jExtendedLogger
extends AbstractLogger {
    private static final long serialVersionUID = 2180779091871302549L;
    private final ModuleLogger logger;
    private static Map<java.util.logging.Level, Level> log4jLevelMap = new IdentityHashMap<java.util.logging.Level, Level>();
    private static Map<Level, java.util.logging.Level> log4jLevelMapRev = new IdentityHashMap<Level, java.util.logging.Level>();

    public CommonLog4jExtendedLogger(String name) {
        this.logger = new ModuleLogger(name);
    }

    public boolean isEnabled(Level log4jlevel) {
        java.util.logging.Level level = log4jLevelMapRev.get(log4jlevel);
        if (level == null) {
            level = java.util.logging.Level.ALL;
        }
        return this.logger.isLoggable(level);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, Message arg2, Throwable arg3) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, CharSequence arg2, Throwable arg3) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, Object arg2, Throwable arg3) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Throwable arg3) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object ... arg3) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return this.isEnabled(arg0);
    }

    public boolean isEnabled(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return this.isEnabled(arg0);
    }

    public Level getLevel() {
        Level level = log4jLevelMap.get(this.logger.getLevel());
        return level != null ? level : Level.ALL;
    }

    public void logMessage(String fqcn, Level log4jlevel, Marker marker, Message msg, Throwable t) {
        java.util.logging.Level level = log4jLevelMapRev.get(log4jlevel);
        if (level == null) {
            level = java.util.logging.Level.ALL;
        }
        this.logger.log(level, msg.getFormattedMessage(), t);
    }

    static {
        log4jLevelMap.put(java.util.logging.Level.FINEST, Level.TRACE);
        log4jLevelMap.put(java.util.logging.Level.FINER, Level.DEBUG);
        log4jLevelMap.put(java.util.logging.Level.FINE, Level.DEBUG);
        log4jLevelMap.put(java.util.logging.Level.CONFIG, Level.INFO);
        log4jLevelMap.put(java.util.logging.Level.INFO, Level.INFO);
        log4jLevelMap.put(java.util.logging.Level.WARNING, Level.WARN);
        log4jLevelMap.put(java.util.logging.Level.SEVERE, Level.ERROR);
        log4jLevelMap.put(java.util.logging.Level.ALL, Level.ALL);
        log4jLevelMap.put(java.util.logging.Level.OFF, Level.OFF);
        for (Map.Entry<java.util.logging.Level, Level> entry : log4jLevelMap.entrySet()) {
            log4jLevelMapRev.put(entry.getValue(), entry.getKey());
        }
    }
}

