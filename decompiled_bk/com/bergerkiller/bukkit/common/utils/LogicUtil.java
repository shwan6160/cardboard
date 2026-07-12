/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableMap
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.serialization.ConfigurationSerializable
 *  org.bukkit.configuration.serialization.DelegateDeserialization
 *  org.bukkit.configuration.serialization.SerializableAs
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.collections.BlockSet;
import com.bergerkiller.bukkit.common.collections.ImmutableArrayList;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

public class LogicUtil {
    private static Map<Class<?>, UnaryOperator<Object>> _cloneMethodCache;
    private static final Consumer<Object> _noopConsumer;
    private static final Predicate<Object> _alwaysTruePredicate;
    private static final Predicate<Object> _alwaysFalsePredicate;
    private static final Supplier<Object> _nullSupplier;
    private static final WeakReference<Object> _nullWeakReference;
    private static Map<Class<?>, String> configurationSerializableNames;
    private static final ItemSynchronizer _identityItemSynchronizer;
    private static final ExceptionallyAsyncHandler _exceptionallyAsyncHandler;

    public static Class<?> getUnboxedType(Class<?> boxedType) {
        return BoxedType.getUnboxedType(boxedType);
    }

    public static Class<?> getBoxedType(Class<?> unboxedType) {
        return BoxedType.getBoxedType(unboxedType);
    }

    public static Class<?> tryBoxType(Class<?> type) {
        return BoxedType.tryBoxType(type);
    }

    public static boolean bothNullOrEqual(Object value1, Object value2) {
        return value1 == null ? value2 == null : value1.equals(value2);
    }

    public static boolean bothNullOrEqualIgnoreCase(String value1, String value2) {
        return value1 == null ? value2 == null : value1.equalsIgnoreCase(value2);
    }

    public static boolean nullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean nullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean nullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean nullOrEmpty(ItemStack item) {
        return ItemUtil.isEmpty(item);
    }

    public static boolean nullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static <T> T unsafeCast(Object value) {
        return (T)value;
    }

    public static <T> T tryCast(Object object, Class<T> type) {
        return LogicUtil.tryCast(object, type, null);
    }

    public static <T> T tryCast(Object object, Class<T> type, T def) {
        if (type.isInstance(object)) {
            return type.cast(object);
        }
        return def;
    }

    public static boolean isInBounds(Collection<?> collection, int index) {
        return collection != null && index >= 0 && index < collection.size();
    }

    public static boolean isInBounds(Object[] array, int index) {
        return array != null && index >= 0 && index < array.length;
    }

    public static boolean isInBounds(Object array, int index) {
        return array != null && index >= 0 && index < Array.getLength(array);
    }

    public static <I, O> O applyIfNotNull(I input, Function<I, O> methodToCall, O def) {
        return input == null ? def : methodToCall.apply(input);
    }

    public static <T> T fixNull(T value, T def) {
        return value == null ? def : value;
    }

    public static <T> T tryCreateUsingSupplier(Supplier<T> supplier, Function<Throwable, T> errorHandler) {
        try {
            return supplier.get();
        }
        catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    public static <T> T tryCreateUsingCheckedSupplier(CheckedSupplier<T> supplier, Function<Throwable, T> errorHandler) {
        try {
            return supplier.get();
        }
        catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    public static <T> T tryCreate(CheckedSupplier<T> constructor, Function<Throwable, T> errorHandler) {
        try {
            return constructor.get();
        }
        catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    @Deprecated
    public static <T> T[] cloneArray(T[] array) {
        return array == null ? null : (Object[])array.clone();
    }

    public static <I, O> O[] mapArray(I[] inputArray, Class<O> outputType, Function<I, O> mapper) {
        int len = inputArray.length;
        O[] outputArray = LogicUtil.createArray(outputType, len);
        for (int i = 0; i < len; ++i) {
            outputArray[i] = mapper.apply(inputArray[i]);
        }
        return outputArray;
    }

    public static <K, V> void mapMapValues(Map<K, V> map, BiFunction<? super K, ? super V, ? extends V> mapper) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            V newValue;
            V oldValue = entry.getValue();
            if (oldValue == (newValue = mapper.apply(entry.getKey(), oldValue))) continue;
            entry.setValue(newValue);
        }
    }

    public static <E> void mapListItems(List<E> list, UnaryOperator<E> mapper) {
        if (list instanceof RandomAccess) {
            int size = list.size();
            for (int i = 0; i < size; ++i) {
                Object newValue;
                E oldValue = list.get(i);
                if (oldValue == (newValue = mapper.apply(oldValue))) continue;
                list.set(i, newValue);
            }
        } else {
            ListIterator iter = list.listIterator();
            while (iter.hasNext()) {
                Object newValue;
                E oldValue = iter.next();
                if (oldValue == (newValue = mapper.apply(oldValue))) continue;
                iter.set(newValue);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> void registerCloneMethod(Class<T> type, UnaryOperator<T> op) {
        Class<LogicUtil> clazz = LogicUtil.class;
        synchronized (LogicUtil.class) {
            HashMap newMap = new HashMap(_cloneMethodCache);
            newMap.put(type, op);
            _cloneMethodCache = newMap;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public static <T> UnaryOperator<T> findCloneMethod(T value) {
        Class<?> type;
        try {
            type = value.getClass();
        }
        catch (NullPointerException ex) {
            if (value == null) {
                throw new IllegalArgumentException("Input value is null");
            }
            throw ex;
        }
        return LogicUtil.findCloneMethod(type);
    }

    public static <T> UnaryOperator<T> findCloneMethod(Class<T> type) {
        return (UnaryOperator)LogicUtil.unsafeCast(LogicUtil.synchronizeCopyOnWrite(LogicUtil.class, () -> _cloneMethodCache, type, (S map, K inType) -> (UnaryOperator)map.get(inType), (S map, K inType) -> {
            Method cloneMethod;
            try {
                cloneMethod = type.getMethod("clone", new Class[0]);
            }
            catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalArgumentException("Object of type " + type.getName() + " can not be cloned");
            }
            FastMethod fm = new FastMethod(cloneMethod);
            fm.forceInitialization();
            UnaryOperator op = fm::invoke;
            HashMap newMap = new HashMap((Map<Class<?>, UnaryOperator<Object>>)map);
            newMap.put((Class<?>)inType, op);
            _cloneMethodCache = newMap;
            return op;
        }));
    }

    public static <T> T clone(T value) {
        if (value == null) {
            return null;
        }
        return (T)LogicUtil.findCloneMethod(value.getClass()).apply(value);
    }

    public static <T> T[] cloneAll(T[] values) {
        if (values == null) {
            return null;
        }
        int len = values.length;
        if (len == 0) {
            return values;
        }
        Class<?> componentType = values.getClass().getComponentType();
        UnaryOperator<?> cloneFunction = LogicUtil.findCloneMethod(componentType);
        ?[] copy = LogicUtil.createArray(componentType, len);
        for (int i = 0; i < len; ++i) {
            T input = values[i];
            if (input == null) continue;
            copy[i] = cloneFunction.apply(input);
        }
        return copy;
    }

    public static <T> T[] cloneAll(T[] values, UnaryOperator<T> cloneFunction) {
        if (values == null) {
            return null;
        }
        int len = values.length;
        if (len == 0) {
            return values;
        }
        ?[] copy = LogicUtil.createArray(values.getClass().getComponentType(), len);
        for (int i = 0; i < len; ++i) {
            T input = values[i];
            if (input == null) continue;
            copy[i] = cloneFunction.apply(input);
        }
        return copy;
    }

    public static Class<?> getArrayType(Class<?> componentType) {
        if (componentType.isPrimitive()) {
            try {
                return Array.newInstance(componentType, 0).getClass();
            }
            catch (IllegalArgumentException ex) {
                if (componentType == Void.TYPE) {
                    throw new IllegalArgumentException("Cannot create an array of void");
                }
                throw ex;
            }
        }
        try {
            return Class.forName("[L" + componentType.getName() + ";");
        }
        catch (ClassNotFoundException e) {
            return Object[].class;
        }
    }

    public static Class<?> getArrayType(Class<?> componentType, int levels) {
        Class<?> type = componentType;
        while (levels-- > 0) {
            type = LogicUtil.getArrayType(type);
        }
        return type;
    }

    public static <T> T getList(List<T> list, int index, T def) {
        return LogicUtil.isInBounds(list, index) ? list.get(index) : def;
    }

    public static <T> T getArray(T[] array, int index, T def) {
        return LogicUtil.isInBounds(array, index) ? array[index] : def;
    }

    public static <T> T[] createArray(Class<T> type, int length) {
        return (Object[])Array.newInstance(type, length);
    }

    public static <T> T[] toArray(Collection<?> collection, Class<T> type) {
        return collection.toArray(LogicUtil.createArray(type, collection.size()));
    }

    @SafeVarargs
    public static <E, T extends E> boolean addArray(Collection<E> collection, T ... array) {
        if (array.length > 20) {
            return collection.addAll(Arrays.asList(array));
        }
        boolean changed = false;
        for (T element : array) {
            changed |= collection.add(element);
        }
        return changed;
    }

    public static boolean removeArray(Collection<?> collection, Object ... array) {
        if (array.length > 100) {
            return collection.removeAll(Arrays.asList(array));
        }
        boolean changed = false;
        for (Object element : array) {
            changed |= collection.remove(element);
        }
        return changed;
    }

    public static <T> T[] removeArrayElement(T[] input, T value) {
        for (int index = 0; index < input.length; ++index) {
            if (!LogicUtil.bothNullOrEqual(input[index], value)) continue;
            return LogicUtil.removeArrayElement(input, index);
        }
        return input;
    }

    public static <T> T[] removeArrayElement(T[] input, int index) {
        int len = input.length;
        if (index < 0 || index >= len) {
            return input;
        }
        if (index == 0) {
            return Arrays.copyOfRange(input, 1, len);
        }
        if (index == len - 1) {
            return Arrays.copyOf(input, len - 1);
        }
        Object[] rval = (Object[])LogicUtil.unsafeCast(LogicUtil.createArray(input.getClass().getComponentType(), input.length - 1));
        System.arraycopy(input, 0, rval, 0, index);
        System.arraycopy(input, index + 1, rval, index, input.length - index - 1);
        return rval;
    }

    public static <T> T[] appendArray(T[] array, T ... values) {
        if (array == null) {
            return values;
        }
        if (values == null) {
            return array;
        }
        int array_len = array.length;
        int values_len = values.length;
        if (array_len == 0) {
            return values;
        }
        if (values_len == 0) {
            return array;
        }
        T[] rval = Arrays.copyOf(array, array_len + values_len);
        if (values_len == 1) {
            rval[array_len] = values[0];
        } else {
            System.arraycopy(values, 0, rval, array_len, values_len);
        }
        return rval;
    }

    public static <T> T[] appendArrayElement(T[] input, T element) {
        int len = input.length;
        T[] new_arr = Arrays.copyOf(input, len + 1);
        new_arr[len] = element;
        return new_arr;
    }

    public static boolean addOrRemove(BlockSet collection, Block value, boolean add) {
        return add ? collection.add(value) : collection.remove(value);
    }

    public static <T> boolean addOrRemove(Collection<T> collection, T value, boolean add) {
        return add ? collection.add(value) : collection.remove(value);
    }

    public static boolean containsAll(Map<?, ?> map, Map<?, ?> contents) {
        for (Map.Entry<?, ?> entry : contents.entrySet()) {
            Object value = map.get(entry.getKey());
            if (!(value == null ? entry.getValue() != null || !map.containsKey(entry.getKey()) : !value.equals(entry.getValue()))) continue;
            return false;
        }
        return true;
    }

    @SafeVarargs
    public static <T> boolean contains(T value, T ... values) {
        if (value == null) {
            for (T v : values) {
                if (v != null) continue;
                return true;
            }
        } else {
            for (T v : values) {
                if (!value.equals(v)) continue;
                return true;
            }
        }
        return false;
    }

    @SafeVarargs
    public static boolean containsIgnoreCase(String value, String ... values) {
        for (String v : values) {
            if (!LogicUtil.bothNullOrEqualIgnoreCase(v, value)) continue;
            return true;
        }
        return false;
    }

    public static boolean containsByte(byte value, byte ... values) {
        for (byte v : values) {
            if (v != value) continue;
            return true;
        }
        return false;
    }

    public static boolean containsChar(char value, CharSequence sequence) {
        for (int i = 0; i < sequence.length(); ++i) {
            if (sequence.charAt(i) != value) continue;
            return true;
        }
        return false;
    }

    public static boolean containsChar(char value, char ... values) {
        for (char v : values) {
            if (v != value) continue;
            return true;
        }
        return false;
    }

    public static boolean containsInt(int value, int ... values) {
        for (int v : values) {
            if (v != value) continue;
            return true;
        }
        return false;
    }

    public static boolean containsBool(boolean value, boolean ... values) {
        for (boolean v : values) {
            if (v != value) continue;
            return true;
        }
        return false;
    }

    public static <T extends Iterator<?>> T skipIterator(T iterator, int count) {
        for (int i = 0; i < count && iterator.hasNext(); ++i) {
            iterator.next();
        }
        return iterator;
    }

    public static <K, V> K getKeyAtValue(Map<K, V> map, V value) {
        if (map instanceof BiMap) {
            return (K)((BiMap)map).inverse().get(value);
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (!LogicUtil.bothNullOrEqual(entry.getValue(), value)) continue;
            return entry.getKey();
        }
        return null;
    }

    public static Class<?>[] getTypes(Object[] values) {
        Class[] result = new Class[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = values[i] == null ? null : values[i].getClass();
        }
        return result;
    }

    public static <V, E> boolean synchronizeList(List<E> list, Collection<? extends V> values, ItemSynchronizer<V, E> synchronizer) {
        return LogicUtil.synchronizeList(list, values, synchronizer);
    }

    public static <V, E> boolean synchronizeList(List<E> list, Iterable<? extends V> values, ItemSynchronizer<V, E> synchronizer) {
        boolean has_changes = false;
        Iterator<V> value_iter = values.iterator();
        ListIterator<E> item_iter = list.listIterator();
        while (value_iter.hasNext()) {
            int old_index;
            E item;
            V value;
            block6: {
                value = value_iter.next();
                if (!item_iter.hasNext()) {
                    item_iter.add(synchronizer.onAdded(value));
                    has_changes = true;
                    continue;
                }
                item = item_iter.next();
                if (synchronizer.isItem(item, value)) continue;
                old_index = item_iter.previousIndex();
                while (item_iter.hasNext()) {
                    item = item_iter.next();
                    if (!synchronizer.isItem(item, value)) continue;
                    item_iter.remove();
                    break block6;
                }
                item = null;
            }
            item_iter = list.listIterator(old_index);
            if (item == null) {
                item_iter.add(synchronizer.onAdded(value));
            } else {
                item_iter.add(item);
            }
            has_changes = true;
        }
        while (item_iter.hasNext()) {
            E item = item_iter.next();
            synchronizer.onRemoved(item);
            item_iter.remove();
            has_changes = true;
        }
        return has_changes;
    }

    public static <E> boolean synchronizeUnordered(Collection<E> collection, Collection<E> values, ItemSynchronizer<E, E> synchronizer) {
        boolean changed = false;
        if (values.isEmpty()) {
            if (!collection.isEmpty()) {
                for (E old_value : collection) {
                    synchronizer.onRemoved(old_value);
                }
                collection.clear();
                return true;
            }
            return false;
        }
        Iterator<E> iter = collection.iterator();
        while (iter.hasNext()) {
            E old_value = iter.next();
            if (values.contains(old_value)) continue;
            synchronizer.onRemoved(old_value);
            iter.remove();
            changed = true;
        }
        if (values.size() > collection.size()) {
            for (E new_value : values) {
                if (collection.contains(new_value)) continue;
                collection.add(synchronizer.onAdded(new_value));
                changed = true;
            }
        }
        return changed;
    }

    public static <E> List<E> asImmutableList(E ... array) {
        if (array.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return new ImmutableArrayList<E>(array);
    }

    public static Map<String, Object> serializeDeep(ConfigurationSerializable serializable) {
        if (serializable == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Object> values = serializable.serialize();
        String key = LogicUtil.getSerializableKey(serializable.getClass());
        if (values instanceof ImmutableMap) {
            values = new LinkedHashMap<String, Object>(values);
            values.put("==", key);
        } else {
            try {
                values.put("==", key);
            }
            catch (UnsupportedOperationException ex) {
                values = new LinkedHashMap(values);
                values.put("==", key);
            }
        }
        boolean cloned = false;
        for (Map.Entry entry : values.entrySet()) {
            Object value;
            Object newValue = value = entry.getValue();
            if (value instanceof ConfigurationSerializable) {
                newValue = LogicUtil.serializeDeep((ConfigurationSerializable)value);
            } else if (value instanceof List) {
                ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>)value;
                boolean listCloned = false;
                for (int i = 0; i < list.size(); ++i) {
                    Object listItem = list.get(i);
                    if (!(listItem instanceof ConfigurationSerializable)) continue;
                    Map<String, Object> serializedListItem = LogicUtil.serializeDeep((ConfigurationSerializable)listItem);
                    if (!listCloned) {
                        listCloned = true;
                        list = new ArrayList<Map<String, Object>>(list);
                        newValue = list;
                    }
                    list.set(i, serializedListItem);
                }
            }
            if (value == newValue) continue;
            if (!cloned) {
                try {
                    entry.setValue(newValue);
                    continue;
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                    values = new LinkedHashMap<String, Object>(values);
                    cloned = true;
                }
            }
            values.put((String)entry.getKey(), newValue);
        }
        return values;
    }

    private static String getSerializableKey(Class<? extends ConfigurationSerializable> type) {
        return LogicUtil.synchronizeCopyOnWrite(LogicUtil.class, () -> configurationSerializableNames, type, Map::get, (S s, K t) -> {
            String key = LogicUtil.computeSerializableKey(t);
            HashMap names = new HashMap((Map<Class<?>, String>)s);
            names.put((Class<?>)t, key);
            configurationSerializableNames = names;
            return key;
        });
    }

    private static String computeSerializableKey(Class<? extends ConfigurationSerializable> type) {
        DelegateDeserialization delegate = type.getAnnotation(DelegateDeserialization.class);
        if (delegate != null) {
            return LogicUtil.computeSerializableKey(delegate.value());
        }
        SerializableAs nameAnnot = type.getAnnotation(SerializableAs.class);
        if (nameAnnot != null) {
            return nameAnnot.value();
        }
        return type.getName();
    }

    public static <T> Consumer<T> noopConsumer() {
        return _noopConsumer;
    }

    public static <T> Predicate<T> alwaysTruePredicate() {
        return _alwaysTruePredicate;
    }

    public static <T> Predicate<T> alwaysFalsePredicate() {
        return _alwaysFalsePredicate;
    }

    public static <T> Supplier<T> constantSupplier(T value) {
        return () -> value;
    }

    public static <T> Supplier<T> nullSupplier() {
        return _nullSupplier;
    }

    public static <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> future, Function<Throwable, ? extends T> fn, Executor executor) {
        return _exceptionallyAsyncHandler.exceptionallyAsync(future, fn, executor);
    }

    public static <T> DeferredSupplier<T> deferred(Supplier<T> supplier) {
        return DeferredSupplier.of(supplier);
    }

    public static <T> WeakReference<T> nullWeakReference() {
        return _nullWeakReference;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <L, S, K, V> V synchronizeCopyOnWrite(L lock, Function<L, S> source, K key, BiFunction<S, K, V> getter, BiFunction<S, K, V> computer) {
        V result = getter.apply(source.apply(lock), key);
        if (result == null) {
            L l = lock;
            synchronized (l) {
                S sourceLive = source.apply(lock);
                result = getter.apply(sourceLive, key);
                if (result == null) {
                    result = computer.apply(sourceLive, key);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <S, K, V> V synchronizeCopyOnWrite(Object lock, Supplier<S> source, K key, BiFunction<S, K, V> getter, BiFunction<S, K, V> computer) {
        V result = getter.apply(source.get(), key);
        if (result == null) {
            Object object = lock;
            synchronized (object) {
                S sourceLive = source.get();
                result = getter.apply(sourceLive, key);
                if (result == null) {
                    result = computer.apply(sourceLive, key);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <V> V synchronizeCopyOnWrite(Object lock, Supplier<V> getter, Supplier<V> computer) {
        V result = getter.get();
        if (result == null) {
            Object object = lock;
            synchronized (object) {
                result = getter.get();
                if (result == null) {
                    result = computer.get();
                }
            }
        }
        return result;
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T tryMake(CheckedSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        }
        catch (Throwable t) {
            return defaultValue;
        }
    }

    public static <K, V> V getOrComputeDefault(Map<K, V> map, K key, Function<? super K, ? extends V> computer) {
        V value = map.get(key);
        return value != null ? value : computer.apply(key);
    }

    public static <V> V getOrSupplyDefault(Map<?, V> map, Object key, Supplier<? extends V> supplier) {
        V value = map.get(key);
        return value != null ? value : supplier.get();
    }

    public static <E> List<E> combineUnmodifiableLists(List<E> a, List<E> b) {
        if (b.isEmpty()) {
            return a;
        }
        if (a.isEmpty()) {
            return b;
        }
        ArrayList<E> result = new ArrayList<E>(a.size() + b.size());
        result.addAll(a);
        result.addAll(b);
        return Collections.unmodifiableList(result);
    }

    public static <K, V> Map<K, V> createRawEnumMap(Class<?> enumClass) {
        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("Not an enum class: " + enumClass);
        }
        return new EnumMap(enumClass);
    }

    static {
        ExceptionallyAsyncHandler handler;
        _cloneMethodCache = Collections.emptyMap();
        _noopConsumer = obj -> {};
        _alwaysTruePredicate = obj -> true;
        _alwaysFalsePredicate = obj -> false;
        _nullSupplier = () -> null;
        _nullWeakReference = new WeakReference<Object>(null);
        configurationSerializableNames = Collections.emptyMap();
        _identityItemSynchronizer = new ItemSynchronizer<Object, Object>(){

            @Override
            public boolean isItem(Object item, Object value) {
                return Objects.equals(item, value);
            }

            @Override
            public Object onAdded(Object value) {
                return value;
            }

            @Override
            public void onRemoved(Object item) {
            }
        };
        try {
            CompletableFuture.class.getMethod("exceptionallyAsync", Function.class);
            handler = new ExceptionallyAsyncHandler(){

                @Override
                public <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> future, Function<Throwable, ? extends T> fn, Executor executor) {
                    return future.exceptionallyAsync(fn, executor);
                }
            };
        }
        catch (NoSuchMethodException ex) {
            handler = new ExceptionallyAsyncHandler(){

                @Override
                public <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> future, Function<Throwable, ? extends T> fn, Executor executor) {
                    return future.handleAsync((r, t) -> t == null ? r : fn.apply((Throwable)t), executor);
                }
            };
        }
        _exceptionallyAsyncHandler = handler;
        LogicUtil.registerCloneMethod(ArrayList.class, a -> (ArrayList)a.clone());
        LogicUtil.registerCloneMethod(LinkedList.class, LinkedList::new);
        LogicUtil.registerCloneMethod(Vector.class, v -> (Vector)v.clone());
        LogicUtil.registerCloneMethod(TreeSet.class, t -> (TreeSet)t.clone());
        LogicUtil.registerCloneMethod(LinkedHashSet.class, LinkedHashSet::new);
        LogicUtil.registerCloneMethod(HashSet.class, s -> (HashSet)s.clone());
    }

    public static interface ItemSynchronizer<V, E> {
        public boolean isItem(E var1, V var2);

        public E onAdded(V var1);

        public void onRemoved(E var1);

        public static <V, E extends V> ItemSynchronizer<V, E> identity() {
            return _identityItemSynchronizer;
        }
    }

    private static interface ExceptionallyAsyncHandler {
        public <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> var1, Function<Throwable, ? extends T> var2, Executor var3);
    }
}

