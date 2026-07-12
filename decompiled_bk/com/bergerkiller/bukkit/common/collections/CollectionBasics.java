/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionBasics {
    public static <T, E extends T> void setAll(Collection<T> collection, E ... elements) {
        CollectionBasics.setAll(collection, Arrays.asList(elements));
    }

    public static <T> void setAll(Collection<T> collection, Collection<? extends T> elements) {
        collection.clear();
        collection.addAll(elements);
    }

    public static boolean containsAll(Collection<?> collection, Collection<?> elements) {
        for (Object value : elements) {
            if (collection.contains(value)) continue;
            return false;
        }
        return true;
    }

    public static boolean removeAll(Collection<?> collection, Collection<?> elements) {
        boolean changed = false;
        for (Object value : elements) {
            changed |= collection.remove(value);
        }
        return changed;
    }

    public static <T> boolean addAll(Collection<T> collection, Collection<? extends T> elements) {
        boolean changed = false;
        for (T value : elements) {
            changed |= collection.add(value);
        }
        return changed;
    }

    public static <T> boolean addAll(List<T> list, int index, Collection<? extends T> elements) {
        boolean changed = false;
        for (T value : elements) {
            list.add(index++, value);
            changed = true;
        }
        return changed;
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
            array = LogicUtil.createArray(array.getClass().getComponentType(), size);
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

    public static <T> ListEntry<T> getEntry(Collection<List<T>> lists, int index) {
        for (List<T> list : lists) {
            int size = list.size();
            if (size >= index) {
                index -= size;
                continue;
            }
            return new ListEntry<T>(list, index);
        }
        throw new IndexOutOfBoundsException();
    }

    public static class ListEntry<T> {
        public final List<T> list;
        public final int index;

        public ListEntry(List<T> list, int index) {
            this.list = list;
            this.index = index;
        }

        public void add(T element) {
            this.list.add(this.index, element);
        }

        public T set(T newElement) {
            return this.list.set(this.index, newElement);
        }

        public T get() {
            return this.list.get(this.index);
        }

        public T remove() {
            return this.list.remove(this.index);
        }

        public boolean addAll(Collection<? extends T> c) {
            return this.list.addAll(this.index, c);
        }
    }
}

