/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex;

import com.bergerkiller.mountiplex.reflection.util.ArrayHelper;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Spliterators;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MountiplexUtil {
    public static Logger LOGGER = Logger.getLogger("REFLECTION");
    private static ArrayList<Runnable> unloaders = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void unloadMountiplex() {
        ArrayList<Runnable> arrayList = unloaders;
        synchronized (arrayList) {
            for (Runnable unloader : unloaders) {
                unloader.run();
            }
            unloaders.clear();
            unloaders.trimToSize();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void registerUnloader(Runnable runnable) {
        ArrayList<Runnable> arrayList = unloaders;
        synchronized (arrayList) {
            unloaders.add(runnable);
        }
    }

    public static boolean containsChar(char value, char ... values) {
        for (char v : values) {
            if (v != value) continue;
            return true;
        }
        return false;
    }

    public static Class<?>[] getTypes(Object[] values) {
        Class[] result = new Class[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = values[i] == null ? null : values[i].getClass();
        }
        return result;
    }

    public static Class<?> getArrayType(Class<?> componentType) {
        return MountiplexUtil.getArrayType(componentType, 1);
    }

    public static Class<?> getArrayType(Class<?> componentType, int num_dimensions) {
        return ArrayHelper.getArrayType(componentType, num_dimensions);
    }

    public static <T> T[] createArray(Class<T> type, int length) {
        return (Object[])Array.newInstance(type, length);
    }

    public static boolean retainAll(Collection<?> collection, Collection<?> elements) {
        Iterator<?> iter = collection.iterator();
        boolean changed = false;
        while (iter.hasNext()) {
            if (elements.contains(iter.next())) continue;
            iter.remove();
            changed = true;
        }
        return changed;
    }

    public static <T> Stream<T> toStream(T value) {
        return Collections.singleton(value).stream();
    }

    public static Object[] toArray(Collection<?> collection) {
        Object[] array = new Object[collection.size()];
        Iterator<?> iter = collection.iterator();
        for (int i = 0; i < array.length; ++i) {
            array[i] = iter.next();
        }
        return array;
    }

    public static <T> T[] toArray(Collection<?> collection, T[] array) {
        int size = collection.size();
        if (array.length < size) {
            array = MountiplexUtil.createArray(array.getClass().getComponentType(), size);
        }
        Iterator<?> iter = collection.iterator();
        for (int i = 0; i < array.length; ++i) {
            if (!iter.hasNext()) {
                array[i] = null;
                break;
            }
            array[i] = iter.next();
        }
        return array;
    }

    public static String getLastBefore(String text, String delimiter) {
        int index = text.lastIndexOf(delimiter);
        return index >= 0 ? text.substring(0, index) : "";
    }

    public static <T> T[] toArray(Collection<?> collection, Class<T> type) {
        return collection.toArray(MountiplexUtil.createArray(type, collection.size()));
    }

    public static <T> T[] getClassConstants(Class<T> theClass) {
        return MountiplexUtil.getClassConstants(theClass, theClass);
    }

    public static <T> T[] getClassConstants(Class<?> theClass, Class<T> type) {
        if (type.isEnum()) {
            return type.getEnumConstants();
        }
        try {
            Field[] declaredFields = theClass.getDeclaredFields();
            ArrayList<Object> constants = new ArrayList<Object>(declaredFields.length);
            for (Field field : declaredFields) {
                Object constant;
                if (!Modifier.isStatic(field.getModifiers()) || !type.isAssignableFrom(field.getType()) || (constant = field.get(null)) == null) continue;
                constants.add(constant);
            }
            return MountiplexUtil.toArray(constants, type);
        }
        catch (Throwable t) {
            LOGGER.log(Level.WARNING, "Failed to find class constants of " + theClass, t);
            return MountiplexUtil.createArray(type, 0);
        }
    }

    public static <T> T parseArray(T[] values, String text, T def) {
        int i;
        if (text == null || text.isEmpty()) {
            return def;
        }
        text = text.toUpperCase(Locale.ENGLISH).replace("_", "").replace(" ", "");
        String[] names = new String[values.length];
        for (i = 0; i < names.length; ++i) {
            names[i] = values[i] instanceof Enum ? ((Enum)values[i]).name() : values[i].toString();
            names[i] = names[i].toUpperCase(Locale.ENGLISH).replace("_", "");
            if (!names[i].equals(text)) continue;
            return values[i];
        }
        for (i = 0; i < names.length; ++i) {
            if (!names[i].contains(text)) continue;
            return values[i];
        }
        for (i = 0; i < names.length; ++i) {
            if (!text.contains(names[i])) continue;
            return values[i];
        }
        return def;
    }

    public static <T> T parseEnum(String text, T def) {
        return (T)MountiplexUtil.parseEnum(def.getClass(), text, def);
    }

    public static <T> T parseEnum(Class<T> enumClass, String text, T def) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("Class '" + enumClass.getSimpleName() + "' is not an Enumeration!");
        }
        return MountiplexUtil.parseArray(enumClass.getEnumConstants(), text, def);
    }

    public static RuntimeException uncheckedRethrow(Throwable t) {
        if (t instanceof InvocationTargetException) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            return (RuntimeException)t;
        }
        RuntimeException r = new RuntimeException("An exception occurred", t);
        r.setStackTrace(new StackTraceElement[0]);
        return r;
    }

    public static double similarity(String s1, String s2) {
        int longerLength;
        String longer = s1;
        String shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        if ((longerLength = longer.length()) == 0) {
            return 1.0;
        }
        return (double)(longerLength - MountiplexUtil.editDistance(longer, shorter)) / (double)longerLength;
    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase(Locale.ENGLISH);
        s2 = s2.toLowerCase(Locale.ENGLISH);
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); ++i) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); ++j) {
                if (i == 0) {
                    costs[j] = j;
                    continue;
                }
                if (j <= 0) continue;
                int newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                    newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                }
                costs[j - 1] = lastValue;
                lastValue = newValue;
            }
            if (i <= 0) continue;
            costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static <T> List<T> createUmodifiableList(Collection<T> values) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        Object[] valuesArr = new Object[values.size()];
        int index = 0;
        for (T value : values) {
            valuesArr[index++] = value;
        }
        return Arrays.asList(valuesArr);
    }

    public static String getPackagePathFromClassPath(String classPath) {
        int lastDotIndex = -100;
        for (int i = 0; i < classPath.length(); ++i) {
            char c = classPath.charAt(i);
            if (c == '.') {
                lastDotIndex = i;
                continue;
            }
            if (i == lastDotIndex + 1 && (c == '$' || Character.isUpperCase(c))) break;
        }
        return lastDotIndex == -100 ? "" : classPath.substring(0, lastDotIndex);
    }

    public static <T> Stream<T> iterateNullTerminated(final T seed, final UnaryOperator<T> f) {
        if (seed == null) {
            return Stream.empty();
        }
        Iterator iterator = new Iterator<T>(){
            T current;
            {
                this.current = seed;
            }

            @Override
            public boolean hasNext() {
                return this.current != null;
            }

            @Override
            public T next() {
                Object result = this.current;
                this.current = f.apply(this.current);
                return result;
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1040), false);
    }
}

