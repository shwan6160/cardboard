/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.OverwritingCircularBuffer;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.logging.WeakLoggingHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;

public class CommonServerLogRecorder
extends Handler
implements LibraryComponent {
    private final Task startupDoneTask;
    private final List<FormattedLogRecord> startupLogRecords = new ArrayList<FormattedLogRecord>();
    private final OverwritingCircularBuffer<LogRecord> lastLogRecords = OverwritingCircularBuffer.create(32);
    private final Set<Logger> hookedLoggers = new HashSet<Logger>();
    private boolean isStartingUp = true;

    public CommonServerLogRecorder(CommonPlugin plugin) {
        this.startupDoneTask = new Task(plugin){

            @Override
            public void run() {
                CommonServerLogRecorder.this.isStartingUp = false;
            }
        };
        this.hookedLoggers.add(Bukkit.getLogger());
        this.hookedLoggers.add(Logger.getLogger(Bukkit.getServer().getClass().getName()));
        this.hookedLoggers.forEach(l -> WeakLoggingHandler.addHandler(l, this));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<FormattedLogRecord> getStartupLogRecords() {
        CommonServerLogRecorder commonServerLogRecorder = this;
        synchronized (commonServerLogRecorder) {
            return new ArrayList<FormattedLogRecord>(this.startupLogRecords);
        }
    }

    public List<FormattedLogRecord> getRuntimeLogRecords() {
        return this.lastLogRecords.values().stream().map(FormattedLogRecord::format).collect(Collectors.toList());
    }

    @Override
    public void enable() {
        this.startupDoneTask.start();
    }

    @Override
    public void disable() {
        this.startupDoneTask.stop();
        this.hookedLoggers.forEach(l -> WeakLoggingHandler.removeHandler(l, this));
        this.hookedLoggers.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void publish(LogRecord record) {
        if (this.isStartingUp) {
            FormattedLogRecord formatted = FormattedLogRecord.format(record);
            CommonServerLogRecorder commonServerLogRecorder = this;
            synchronized (commonServerLogRecorder) {
                this.startupLogRecords.add(formatted);
            }
        } else {
            this.lastLogRecords.add(record);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    public static final class FormattedLogRecord
    implements Comparable<FormattedLogRecord> {
        public final long timestamp;
        public final String content;

        private FormattedLogRecord(LogRecord record) {
            this.timestamp = record.getMillis();
            this.content = ServerLogFormatter.INSTANCE.format(record);
        }

        @Override
        public int compareTo(FormattedLogRecord o) {
            return Long.compare(this.timestamp, o.timestamp);
        }

        public void appendTo(StringBuilder builder) {
            builder.append(this.content).append('\n');
        }

        public static FormattedLogRecord format(LogRecord record) {
            return new FormattedLogRecord(record);
        }
    }

    private static final class ServerLogFormatter
    extends Formatter {
        public static final ServerLogFormatter INSTANCE = new ServerLogFormatter();

        private ServerLogFormatter() {
        }

        @Override
        public String format(LogRecord record) {
            String source;
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault());
            if (record.getSourceClassName() != null) {
                source = record.getSourceClassName();
                if (record.getSourceMethodName() != null) {
                    source = source + " " + record.getSourceMethodName();
                }
            } else {
                source = record.getLoggerName();
            }
            String message = this.formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
                if (throwable.endsWith("\n")) {
                    throwable = throwable.substring(0, throwable.length() - 1);
                }
            }
            return String.format("[%tT] [%4$s] [%3$s] %5$s%6$s", zdt, source, record.getLoggerName(), record.getLevel().getName(), message, throwable);
        }
    }
}

