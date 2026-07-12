/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

public class StackTraceFilter {
    public static final StackTraceFilter SERVER = new StackTraceFilter();
    public final String className;
    public final String methodName;
    public final boolean searchMode;
    private final List<StackTraceFilter> next = new ArrayList<StackTraceFilter>(2);

    public StackTraceFilter() {
        this("*", "*", false);
    }

    private StackTraceFilter(String className, String methodName, boolean searchMode) {
        this.className = className;
        this.methodName = methodName;
        this.searchMode = searchMode;
    }

    public boolean matchClassName(String className) {
        return this.className.equals("*") || this.className.equals(className);
    }

    public boolean matchMethodName(String methodName) {
        return this.methodName.equals("*") || this.methodName.equals(methodName);
    }

    public boolean match(StackTraceElement el) {
        return this.matchClassName(el.getClassName()) && this.matchMethodName(el.getMethodName());
    }

    public void print(Throwable error) {
        this.print(error, Level.SEVERE);
    }

    public void print(Throwable error, Level level) {
        Throwable cause;
        Common.LOGGER.log(level, StackTraceFilter.getMessage(error));
        ArrayList<StackTraceElement> elements = new ArrayList<StackTraceElement>(Arrays.asList(error.getStackTrace()));
        int filteredCount = this.filter(elements);
        for (StackTraceElement element : elements) {
            Common.LOGGER.log(level, "  at " + element.toString());
        }
        if (filteredCount > 0) {
            Common.LOGGER.log(level, "  ..." + filteredCount + " more");
        }
        if ((cause = error.getCause()) != null) {
            Common.LOGGER.log(level, "Caused by: " + StackTraceFilter.getMessage(cause));
            this.print(cause, level);
        }
    }

    private static String getMessage(Throwable error) {
        String msg = error.getMessage();
        if (LogicUtil.nullOrEmpty(msg)) {
            return error.getClass().getName();
        }
        return error.getClass().getName() + ": " + msg;
    }

    public Throwable filter(Throwable throwable) {
        LinkedList<StackTraceElement> trace;
        if (throwable != null && this.filter(trace = new LinkedList<StackTraceElement>(Arrays.asList(throwable.getStackTrace()))) > 0) {
            throwable.setStackTrace(trace.toArray(new StackTraceElement[0]));
        }
        return throwable;
    }

    public int filter(List<StackTraceElement> elements) {
        int old_count = elements.size();
        StackTraceFilter filter = this;
        while ((filter = filter.next(elements)) != null) {
        }
        return old_count - elements.size();
    }

    public StackTraceFilter addNext(StackTraceFilter filter) {
        StackTraceFilter newFilter = this.next(filter.className, filter.methodName);
        if (newFilter == null || newFilter == this) {
            this.next.add(filter);
            return filter;
        }
        for (StackTraceFilter subFilter : filter.next) {
            newFilter.addNext(subFilter);
        }
        return newFilter;
    }

    public StackTraceFilter addNext(String className, String methodName) {
        StackTraceFilter filter = this.next(className, methodName);
        if (filter == null || filter == this) {
            filter = new StackTraceFilter(className, methodName, false);
            this.next.add(filter);
        }
        return filter;
    }

    public StackTraceFilter untilNext(String className, String methodName) {
        StackTraceFilter filter = this.next(className, methodName);
        if (filter == null || filter == this) {
            filter = new StackTraceFilter(className, methodName, true);
            this.next.add(filter);
        }
        return filter;
    }

    public StackTraceFilter addNext(String methodName) {
        return this.addNext(this.className, methodName);
    }

    private StackTraceFilter next(String className, String methodName) {
        ArrayList<StackTraceElement> items = new ArrayList<StackTraceElement>(1);
        items.add(new StackTraceElement(className, methodName, "", 0));
        return this.next(items);
    }

    private StackTraceFilter next(List<StackTraceElement> items) {
        ListIterator<StackTraceElement> it = items.listIterator(items.size());
        if (this.searchMode) {
            while (it.hasPrevious()) {
                StackTraceElement el = it.previous();
                it.remove();
                if (!this.match(el)) continue;
                break;
            }
        }
        if (it.hasPrevious()) {
            StackTraceElement last = it.previous();
            for (StackTraceFilter filter : this.next) {
                if (filter.searchMode) {
                    for (StackTraceElement el : items) {
                        if (!filter.match(el)) continue;
                        return filter;
                    }
                    continue;
                }
                if (!filter.match(last)) continue;
                it.remove();
                return filter;
            }
        }
        return null;
    }

    static {
        StackTraceFilter server_root = SERVER.addNext(Common.NMS_ROOT + ".ThreadServerApplication", "run");
        StackTraceFilter run = server_root.addNext(Common.NMS_ROOT + ".MinecraftServer", "run");
        StackTraceFilter f = run.addNext(Common.NMS_ROOT + ".MinecraftServer", "q");
        f = f.addNext(Common.NMS_ROOT + ".DedicatedServer", "r");
        f = run.addNext(Common.NMS_ROOT + ".MinecraftServer", "stop");
        f = f.addNext(Common.CB_ROOT + ".CraftServer", "disablePlugins");
        f = f.untilNext("org.bukkit.plugin.SimplePluginManager", "disablePlugin");
        f = SERVER.addNext("org.apache.maven.surefire.booter.ForkedBooter", "main");
        f = f.untilNext("org.junit.internal.runners.model.ReflectiveCallable", "run");
        StackTraceFilter stackTraceFilter = f.untilNext("sun.reflect.NativeMethodAccessorImpl", "invoke0");
    }
}

