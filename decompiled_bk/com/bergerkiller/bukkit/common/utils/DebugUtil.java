/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.AsyncTask;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.util.SecureField;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.management.ObjectName;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class DebugUtil {
    public static void randomizeBlock(Block block) {
        DebugUtil.randomizeBlock(block, Material.STONE, Material.DIRT, MaterialUtil.getFirst("GRASS_BLOCK", "GRASS", "LEGACY_GRASS"), Material.IRON_ORE, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK);
    }

    public static void randomizeBlock(Block block, Material ... materials) {
        Material mat;
        do {
            mat = materials[(int)(Math.random() * (double)materials.length)];
        } while (MaterialUtil.isType(block, mat));
        block.setType(mat);
    }

    public static String formatBlock(Block block) {
        return DebugUtil.formatBlock(block, "#world [#x, #y, #z] #type");
    }

    public static String formatBlock(Block block, String format) {
        StringBuilder buffer = new StringBuilder(format);
        StringUtil.replaceAll(buffer, "#x", Integer.toString(block.getX()));
        StringUtil.replaceAll(buffer, "#y", Integer.toString(block.getY()));
        StringUtil.replaceAll(buffer, "#z", Integer.toString(block.getZ()));
        StringUtil.replaceAll(buffer, "#world", block.getWorld().getName());
        StringUtil.replaceAll(buffer, "#type", block.getType().toString());
        return buffer.toString();
    }

    public static void heartbeat() {
        CommonUtil.broadcast("HEARTBEAT: " + System.currentTimeMillis());
    }

    public static <T> TypedValue<T> getVariable(String name, T value) {
        return CommonPlugin.getInstance().getDebugVariable(name, value.getClass(), value);
    }

    public static <T> TypedValue<T> getVariable(String name, Class<T> type, T value) {
        return CommonPlugin.getInstance().getDebugVariable(name, type, value);
    }

    public static <T> T getVariableValue(String name, T value) {
        return DebugUtil.getVariable((String)name, value).value;
    }

    public static double getDoubleValue(String name, double value) {
        return DebugUtil.getVariableValue(name, value);
    }

    public static float getFloatValue(String name, double value) {
        return DebugUtil.getVariableValue(name, value).floatValue();
    }

    public static short getShortValue(String name, int value) {
        return DebugUtil.getVariableValue(name, value).shortValue();
    }

    public static int getIntValue(String name, int value) {
        return DebugUtil.getVariableValue(name, value);
    }

    public static boolean getBooleanValue(String name, boolean value) {
        return DebugUtil.getVariableValue(name, value);
    }

    public static Vector getVectorValue(String name, Vector value) {
        return new Vector(DebugUtil.getDoubleValue(name + "x", value.getX()), DebugUtil.getDoubleValue(name + "y", value.getY()), DebugUtil.getDoubleValue(name + "z", value.getZ()));
    }

    public static IntVector3 getIntVectorValue(String name, IntVector3 value) {
        return new IntVector3(DebugUtil.getIntValue(name + "x", value.x), DebugUtil.getIntValue(name + "y", value.y), DebugUtil.getIntValue(name + "z", value.z));
    }

    public static String getPluginCauses() {
        Plugin[] plugins = CommonUtil.findPlugins(Thread.currentThread().getStackTrace());
        if (plugins == null || plugins.length == 0) {
            return "Unknown";
        }
        String[] names = new String[plugins.length];
        for (int i = 0; i < plugins.length; ++i) {
            names[i] = plugins[i].getName();
        }
        return StringUtil.combineNames(names);
    }

    public static void logInstances(Object value) {
        DebugUtil.logInstances(Bukkit.class, value);
    }

    public static void logInstancesMatching(Predicate<Object> matcher) {
        DebugUtil.logInstancesMatching(Bukkit.class, matcher);
    }

    public static void logInstances(Object startObject, Object value) {
        InstanceSearcher searcher = new InstanceSearcher(o -> o == value);
        searcher.logger.info("Searching for [" + value.getClass().getName() + "] " + value.toString() + ":");
        searcher.searchFrom(new StackElement(startObject));
        searcher.run();
        searcher.logger.info("Search completed.");
    }

    public static void logInstancesMatching(Object startObject, Predicate<Object> matcher) {
        InstanceSearcher searcher = new InstanceSearcher(matcher);
        searcher.logger.info("Searching for matching objects...");
        searcher.searchFrom(new StackElement(startObject));
        searcher.run();
        searcher.logger.info("Search completed.");
    }

    public static void logInstances(Class<?> startClass, Object value) {
        InstanceSearcher searcher = new InstanceSearcher(o -> o == value);
        searcher.logger.info("Searching for [" + value.getClass().getName() + "] " + value.toString() + ":");
        searcher.searchFromClassFields(startClass);
        searcher.run();
        searcher.logger.info("Search completed.");
    }

    public static void logInstancesMatching(Class<?> startClass, Predicate<Object> matcher) {
        InstanceSearcher searcher = new InstanceSearcher(matcher);
        searcher.logger.info("Searching for matching objects...");
        searcher.searchFromClassFields(startClass);
        searcher.run();
        searcher.logger.info("Search completed.");
    }

    public static void logStackTraceAsynchronously(long delay) {
        DebugUtil.logStackTraceAsynchronously(delay, LogicUtil.constantSupplier(Collections.singletonList(Thread.currentThread())));
    }

    public static void logStackTraceAsynchronously(long delay, Predicate<Thread> threadPredicate) {
        DebugUtil.logStackTraceAsynchronously(delay, () -> {
            Thread loggerThread = Thread.currentThread();
            Thread[] tmp = new Thread[Thread.activeCount() + 32];
            ArrayList<Thread> result = new ArrayList<Thread>();
            int count = Thread.enumerate(tmp);
            for (int i = 0; i < count; ++i) {
                if (tmp[i] == loggerThread || !threadPredicate.test(tmp[i])) continue;
                result.add(tmp[i]);
            }
            return result;
        });
    }

    public static void logStackTraceAsynchronously(final long delay, final Supplier<? extends Iterable<Thread>> threadSelector) {
        new AsyncTask(){

            @Override
            public void run() {
                1.sleep(delay);
                DebugUtil.logStackTraces(threadSelector);
            }
        }.start();
    }

    public static void logStackTraces(Supplier<? extends Iterable<Thread>> threadSelector) {
        for (Thread thread : threadSelector.get()) {
            StackTraceElement[] stack = thread.getStackTrace();
            Logging.LOGGER_DEBUG.warning("Stack trace of thread [" + thread.getName() + "]:");
            for (StackTraceElement element : stack) {
                Logging.LOGGER_DEBUG.warning("  at " + element.toString());
            }
        }
    }

    private static boolean isInterestingType(Class<?> type) {
        return !type.isPrimitive() && type != String.class && !Number.class.isAssignableFrom(type);
    }

    public static void waitForVisualVMProfiler(long timeout, long setupDuration) {
        long startTime = System.currentTimeMillis();
        boolean found = false;
        try {
            block4: do {
                Thread.sleep(100L);
                ObjectName diagnosticsCommandName = new ObjectName("com.sun.management:type=DiagnosticCommand");
                String result = (String)ManagementFactory.getPlatformMBeanServer().invoke(diagnosticsCommandName, "vmDynlibs", null, null);
                for (String line : result.split("\n")) {
                    if (!line.endsWith("profilerinterface.dll") && !line.endsWith("libprofilerinterface.so") && !line.endsWith("libprofilerinterface.jnilib")) continue;
                    found = true;
                    continue block4;
                }
            } while (!found && System.currentTimeMillis() - startTime < timeout);
        }
        catch (Throwable t) {
            throw new UnsupportedOperationException("Failed to check whether VisualVM is hooked", t);
        }
        if (!found) {
            throw new IllegalStateException("Timed out waiting for VisualVM to be hooked!");
        }
        try {
            Thread.sleep(setupDuration);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private static class InstanceSearcher {
        private final ModuleLogger logger = Logging.LOGGER_DEBUG;
        private final IdentityHashMap<Object, Boolean> crossedValues = new IdentityHashMap();
        private final HashMap<Class<?>, ArrayList<Field>> classFieldMapping = new HashMap();
        private final Predicate<Object> matcher;
        private final StackTreeElement resultTree = new StackTreeElement(null);
        private List<StackElement> pending = new ArrayList<StackElement>();
        private List<StackElement> pending_tmp = new ArrayList<StackElement>();

        public InstanceSearcher(Predicate<Object> matcher) {
            this.matcher = matcher;
        }

        public void run() {
            while (!this.pending.isEmpty()) {
                List<StackElement> c = this.pending;
                this.pending = this.pending_tmp;
                this.pending_tmp = c;
                this.pending.clear();
                for (StackElement el : this.pending_tmp) {
                    this.searchFrom(el);
                }
            }
            if (!this.resultTree.children.isEmpty()) {
                this.resultTree.log(this.logger, "");
            }
        }

        public ArrayList<Field> searchFromClassFields(Class<?> type) {
            ArrayList<Field> staticFields = new ArrayList<Field>();
            ArrayList<Field> localFields = new ArrayList<Field>();
            Class<?> t = type;
            do {
                Field[] fields;
                try {
                    fields = t.getDeclaredFields();
                }
                catch (Throwable err) {
                    continue;
                }
                for (Field f : fields) {
                    if (!DebugUtil.isInterestingType(f.getType())) continue;
                    try {
                        SecureField sf = new SecureField();
                        sf.init(f);
                        sf.read();
                        if (Modifier.isStatic(f.getModifiers())) {
                            staticFields.add(f);
                            continue;
                        }
                        localFields.add(f);
                    }
                    catch (RuntimeException runtimeException) {
                        // empty catch block
                    }
                }
            } while ((t = t.getSuperclass()) != null);
            this.classFieldMapping.put(type, localFields);
            StackElement class_start_stack = new StackElement(type);
            for (Field staticField : staticFields) {
                Object staticValue = null;
                try {
                    staticValue = staticField.get(null);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (staticValue == null) continue;
                this.pending.add(class_start_stack.next(staticValue, FieldWithType.wrapInfo(staticField, staticValue)));
            }
            return localFields;
        }

        /*
         * WARNING - void declaration
         */
        public void searchFrom(StackElement current) {
            StackElement list_el;
            if (this.matcher.test(current.value)) {
                List stack = MountiplexUtil.iterateNullTerminated(current, s -> s.parent).collect(Collectors.toCollection(ArrayList::new));
                Collections.reverse(stack);
                StackTreeElement curr = this.resultTree;
                for (StackElement stackElement : stack) {
                    curr = curr.next(stackElement);
                }
                curr.valueIsHere = true;
            }
            if (this.crossedValues.put(current.value, Boolean.TRUE) != null) {
                return;
            }
            Class<?> type = current.value.getClass();
            ArrayList<Field> localFields = this.classFieldMapping.get(type);
            if (localFields == null) {
                localFields = this.searchFromClassFields(type);
            }
            try {
                if (Map.class.isAssignableFrom(type)) {
                    for (Map.Entry entry : ((Map)current.value).entrySet()) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        if (key != null && DebugUtil.isInterestingType(key.getClass())) {
                            this.pending.add(current.next(key, "M{v=" + value + "}.key " + key.getClass().getName()));
                        }
                        if (value == null || !DebugUtil.isInterestingType(value.getClass())) continue;
                        this.pending.add(current.next(value, "M{k=" + key + "}.value " + value.getClass().getName()));
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (List.class.isAssignableFrom(type)) {
                    StackElement list_parent = current.parent == null || current.index != -1 ? current : current.parent;
                    boolean bl = false;
                    for (Object currItem : (List)current.value) {
                        void var5_12;
                        if (currItem != null && DebugUtil.isInterestingType(currItem.getClass())) {
                            list_el = list_parent.next(currItem, current.info);
                            list_el.index = var5_12;
                            this.pending.add(list_el);
                        }
                        ++var5_12;
                    }
                }
            }
            catch (Throwable list_parent) {
                // empty catch block
            }
            try {
                if (Collection.class.isAssignableFrom(type) && !List.class.isAssignableFrom(type)) {
                    for (Object e : (Collection)current.value) {
                        if (e == null || !DebugUtil.isInterestingType(e.getClass())) continue;
                        this.pending.add(current.next(e, "C[?]"));
                    }
                }
            }
            catch (Throwable list_parent) {
                // empty catch block
            }
            if (type.isArray()) {
                if (type.getComponentType().isPrimitive()) {
                    return;
                }
                Object[] arr = (Object[])current.value;
                StackElement stackElement = current.parent == null || current.index != -1 ? current : current.parent;
                for (int i = 0; i < arr.length; ++i) {
                    Object currItem;
                    currItem = arr[i];
                    if (currItem == null || !DebugUtil.isInterestingType(currItem.getClass())) continue;
                    list_el = stackElement.next(currItem, current.info);
                    list_el.index = i;
                    this.pending.add(list_el);
                }
                return;
            }
            for (Field field : localFields) {
                Object fieldValue = null;
                try {
                    fieldValue = field.get(current.value);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (fieldValue == null) continue;
                this.pending.add(current.next(fieldValue, FieldWithType.wrapInfo(field, fieldValue)));
            }
        }
    }

    private static class StackElement {
        public final StackElement parent;
        public final Object value;
        public final Object info;
        public int index;

        public StackElement(Object value) {
            this(value, value);
        }

        public StackElement(Object value, Object info) {
            this(null, value, info);
        }

        private StackElement(StackElement parent, Object value, Object info) {
            this.parent = parent;
            this.value = value;
            this.info = info;
            this.index = -1;
        }

        public StackElement next(Object value, Object info) {
            return new StackElement(this, value, info);
        }
    }

    private static class StackTreeElement {
        public final StackElement self;
        public List<StackTreeElement> children;
        public boolean valueIsHere;

        public StackTreeElement(StackElement self) {
            this.self = self;
            this.children = Collections.emptyList();
            this.valueIsHere = false;
        }

        public StackTreeElement next(StackElement child) {
            for (StackTreeElement childTreeEl : this.children) {
                if (childTreeEl.self != child) continue;
                return childTreeEl;
            }
            StackTreeElement newElement = new StackTreeElement(child);
            if (this.children.isEmpty()) {
                this.children = Collections.singletonList(newElement);
            } else {
                this.children = new ArrayList<StackTreeElement>(this.children);
                this.children.add(newElement);
            }
            return newElement;
        }

        public void log(ModuleLogger logger, String indent) {
            String nextIndent = "";
            if (this.self != null) {
                nextIndent = indent + "  ";
                if (this.self.info instanceof Class) {
                    logger.info("[Static members of " + ((Class)this.self.info).getSimpleName() + "]");
                } else {
                    String infoStr;
                    if (this.self.info instanceof Field) {
                        Field f = (Field)this.self.info;
                        infoStr = Modifier.toString(f.getModifiers()) + " " + f.getType().getSimpleName() + " " + f.getName();
                    } else if (this.self.info instanceof FieldWithType) {
                        FieldWithType fwt = (FieldWithType)this.self.info;
                        Field f = fwt.field;
                        infoStr = Modifier.toString(f.getModifiers()) + " [" + fwt.valueType.getSimpleName() + "] " + f.getType().getSimpleName() + " " + f.getName();
                    } else {
                        infoStr = this.self.info.toString();
                    }
                    if (this.self.index == -2) {
                        infoStr = infoStr + " [Recursive]";
                    } else if (this.self.index != -1) {
                        infoStr = infoStr + " [" + this.self.index + "]";
                    }
                    if (this.valueIsHere) {
                        logger.info(indent + "- " + infoStr + " <<< HERE");
                    } else {
                        logger.info(indent + "- " + infoStr);
                    }
                }
            }
            for (StackTreeElement child : this.children) {
                child.log(logger, nextIndent);
            }
        }
    }

    private static class FieldWithType {
        public final Field field;
        public final Class<?> valueType;

        public FieldWithType(Field field, Class<?> valueType) {
            this.field = field;
            this.valueType = valueType;
        }

        public static Object wrapInfo(Field field, Object value) {
            Class<?> type = value.getClass();
            if (type != field.getType()) {
                return new FieldWithType(field, value.getClass());
            }
            return field;
        }
    }
}

