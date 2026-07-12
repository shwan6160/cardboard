/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class SortedIdentityCache<K, V>
implements Iterable<V> {
    protected final Synchronizer<K, V> synchronizer;

    public static <K, V> SortedIdentityCache<K, V> create(Synchronizer<K, V> synchronizer) {
        return new SortedIdentityCacheList<K, V>(synchronizer);
    }

    public static <K, V> SortedIdentityCache<K, V> createLinked(Synchronizer<K, V> synchronizer) {
        return new SortedIdentityCacheLinkedList<K, V>(synchronizer);
    }

    protected SortedIdentityCache(Synchronizer<K, V> synchronizer) {
        this.synchronizer = synchronizer;
    }

    public abstract int size();

    public abstract void clear();

    @Override
    public abstract Iterator<V> iterator();

    public abstract Iterable<K> keys();

    public abstract Iterable<Map.Entry<K, V>> entries();

    @Override
    public abstract void forEach(Consumer<? super V> var1);

    public Stream<V> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), 1040), false);
    }

    public final boolean sync(Stream<? extends K> keyStream) {
        return this.sync(keyStream::forEachOrdered);
    }

    public final boolean sync(Iterable<? extends K> keys) {
        return this.sync(keys::forEach);
    }

    public final boolean sync(Iterator<? extends K> keysIterator) {
        return this.sync(keysIterator::forEachRemaining);
    }

    public abstract boolean sync(Consumer<Consumer<K>> var1);

    public abstract V remove(K var1);

    public abstract V get(K var1);

    public abstract V addFirst(K var1);

    public abstract V addLast(K var1);

    private static final class SortedIdentityCacheList<K, V>
    extends SortedIdentityCache<K, V> {
        private static final int INDEX_NOT_INSERTED = -1;
        private static final int INDEX_PENDING_FLATTENING = -2;
        private final Map<K, LinkEntry<K, V>> entriesByKey = new IdentityHashMap<K, LinkEntry<K, V>>();
        private LinkEntry<K, V>[] entries = new LinkEntry[10];

        public SortedIdentityCacheList(Synchronizer<K, V> synchronizer) {
            super(synchronizer);
        }

        @Override
        public int size() {
            return this.entriesByKey.size();
        }

        @Override
        public void clear() {
            if (!this.entriesByKey.isEmpty()) {
                this.entriesByKey.clear();
                Arrays.fill(this.entries, null);
            }
        }

        @Override
        public Iterator<V> iterator() {
            return new MappedIterator<Object>(0, LinkEntry::getValue);
        }

        @Override
        public Iterable<K> keys() {
            return () -> new MappedIterator<Object>(0, LinkEntry::getKey);
        }

        @Override
        public Iterable<Map.Entry<K, V>> entries() {
            return () -> new MappedIterator(0, Function.identity());
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            LinkEntry<K, V>[] entries = this.entries;
            int numEntries = this.entriesByKey.size();
            for (int i = 0; i < numEntries; ++i) {
                action.accept(entries[i].getValue());
            }
        }

        @Override
        public boolean sync(Consumer<Consumer<K>> fillMethod) {
            Syncher syncher = new Syncher();
            fillMethod.accept(syncher);
            syncher.trimRemaining();
            return syncher.hasChanges;
        }

        @Override
        public V remove(K key) {
            LinkEntry<K, V> removedEntry = this.entriesByKey.remove(key);
            if (removedEntry == null) {
                return null;
            }
            LinkEntry<K, V>[] entries = this.entries;
            int numEntries = this.entriesByKey.size();
            for (int i = ((LinkEntry)removedEntry).index; i < numEntries; ++i) {
                LinkEntry<K, V> entry = entries[i + 1];
                ((LinkEntry)entry).index--;
                entries[i] = entry;
            }
            return removedEntry.getValue();
        }

        @Override
        public V get(K key) {
            LinkEntry<K, V> entry = this.entriesByKey.get(key);
            return entry == null ? null : (V)entry.getValue();
        }

        @Override
        public V addFirst(K key) {
            LinkEntry<K, V> entry = this.computeValue(key);
            if (((LinkEntry)entry).index == -1) {
                this.insertAt(0, entry);
            }
            return (V)((LinkEntry)entry).value;
        }

        @Override
        public V addLast(K key) {
            LinkEntry<K, V> entry = this.computeValue(key);
            if (((LinkEntry)entry).index == -1) {
                int numEntries = this.entriesByKey.size() - 1;
                this.insertAtEnd(entry, numEntries);
            }
            return (V)((LinkEntry)entry).value;
        }

        private void insertAt(int index, LinkEntry<K, V> newEntry) {
            LinkEntry<K, V>[] entries = this.entries;
            int numEntries = this.entriesByKey.size() - 1;
            if (numEntries == entries.length) {
                int newSize = Math.max(20, numEntries * 4 / 3);
                if (index == numEntries) {
                    this.entries = entries = Arrays.copyOf(entries, newSize);
                } else {
                    LinkEntry[] newEntries = new LinkEntry[newSize];
                    System.arraycopy(entries, 0, newEntries, 0, index);
                    for (int i = index; i < numEntries; ++i) {
                        LinkEntry<K, V> entry = entries[i];
                        ((LinkEntry)entry).index++;
                        newEntries[i + 1] = entry;
                    }
                    entries = newEntries;
                    this.entries = newEntries;
                }
            } else if (index < numEntries) {
                for (int i = numEntries - 1; i >= index; --i) {
                    LinkEntry<K, V> entry = entries[i];
                    ((LinkEntry)entry).index++;
                    entries[i + 1] = entry;
                }
            }
            ((LinkEntry)newEntry).index = index;
            entries[index] = newEntry;
        }

        private void insertAtEnd(LinkEntry<K, V> newEntry, int numEntries) {
            LinkEntry<K, V>[] entries = this.entries;
            if (numEntries == entries.length) {
                int newSize = Math.max(20, numEntries * 4 / 3);
                this.entries = entries = Arrays.copyOf(entries, newSize);
            }
            ((LinkEntry)newEntry).index = numEntries;
            entries[numEntries] = newEntry;
        }

        private LinkEntry<K, V> computeValue(K key) {
            return this.entriesByKey.computeIfAbsent(key, k -> new LinkEntry(k, this.synchronizer.onAdded(k)));
        }

        private static <K, V> int setEntries(LinkEntry<K, V>[] entries, int destIndex, LinkEntry<K, V> entry) {
            entries[++destIndex] = entry;
            ((LinkEntry)entry).index = destIndex;
            for (LinkEntry compacted : ((LinkEntry)entry).insertedAfter) {
                entries[++destIndex] = compacted;
                compacted.index = destIndex;
            }
            ((LinkEntry)entry).insertedAfter = Collections.emptyList();
            return destIndex;
        }

        private static <K, V> int setEntriesReverse(LinkEntry<K, V>[] entries, int destIndex, LinkEntry<K, V> entry) {
            int entryNumCompacted = ((LinkEntry)entry).insertedAfter.size();
            if (entryNumCompacted > 0) {
                for (int i = entryNumCompacted - 1; i >= 0; --i) {
                    LinkEntry compacted = (LinkEntry)((LinkEntry)entry).insertedAfter.get(i);
                    entries[--destIndex] = compacted;
                    compacted.index = destIndex;
                }
                ((LinkEntry)entry).insertedAfter = Collections.emptyList();
            }
            entries[--destIndex] = entry;
            ((LinkEntry)entry).index = destIndex;
            return destIndex;
        }

        static /* synthetic */ LinkEntry[] access$502(SortedIdentityCacheList x0, LinkEntry[] x1) {
            x0.entries = x1;
            return x1;
        }

        private static final class LinkEntry<K, V>
        implements Map.Entry<K, V> {
            private final K key;
            private V value;
            private int index;
            private List<LinkEntry<K, V>> insertedAfter;

            public LinkEntry(K key, V value) {
                this.key = key;
                this.value = value;
                this.index = -1;
                this.insertedAfter = Collections.emptyList();
            }

            @Override
            public K getKey() {
                return this.key;
            }

            @Override
            public V getValue() {
                return this.value;
            }

            @Override
            public V setValue(V value) {
                V oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        }

        private final class MappedIterator<E>
        implements Iterator<E> {
            private final Function<LinkEntry<K, V>, E> mapper;
            private int currentIndex;

            public MappedIterator(int currentIndex, Function<LinkEntry<K, V>, E> mapper) {
                this.mapper = mapper;
                this.currentIndex = currentIndex;
            }

            @Override
            public boolean hasNext() {
                return this.currentIndex < SortedIdentityCacheList.this.size();
            }

            @Override
            public E next() {
                LinkEntry curr;
                int index = this.currentIndex++;
                try {
                    curr = SortedIdentityCacheList.this.entries[index];
                }
                catch (IndexOutOfBoundsException ex) {
                    if (index < SortedIdentityCacheList.this.size()) {
                        throw ex;
                    }
                    curr = null;
                }
                if (curr == null) {
                    this.currentIndex = index;
                    throw new NoSuchElementException("End reached");
                }
                return this.mapper.apply(curr);
            }

            @Override
            public void remove() {
                int indexRemoved;
                --this.currentIndex;
                if ((indexRemoved = this.currentIndex++) < 0) {
                    throw new NoSuchElementException("Next not called before remove()");
                }
                LinkEntry[] entries = SortedIdentityCacheList.this.entries;
                LinkEntry removed = entries[indexRemoved];
                int numEntries = SortedIdentityCacheList.this.entriesByKey.size();
                for (int i = indexRemoved; i < numEntries; ++i) {
                    LinkEntry entry = entries[i + 1];
                    entry.index = i;
                    entries[i] = entry;
                }
                entries[numEntries - 1] = null;
                SortedIdentityCacheList.this.entriesByKey.remove(removed.getKey());
                SortedIdentityCacheList.this.synchronizer.onRemoved(removed.getKey(), removed.getValue());
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                int index;
                LinkEntry[] entries = SortedIdentityCacheList.this.entries;
                int numEntries = SortedIdentityCacheList.this.entriesByKey.size();
                for (index = this.currentIndex; index < numEntries; ++index) {
                    action.accept(this.mapper.apply(entries[index]));
                }
                this.currentIndex = index;
            }
        }

        private final class Syncher
        implements Consumer<K> {
            private int currentIndex = 0;
            private int numEntries;
            private LinkEntry<K, V> firstCompactedEntry;
            private int numCompacted;
            public boolean hasChanges;

            public Syncher() {
                this.numEntries = SortedIdentityCacheList.this.entriesByKey.size();
                this.firstCompactedEntry = null;
                this.numCompacted = 0;
                this.hasChanges = false;
            }

            @Override
            public void accept(K key) {
                boolean isValidEntry;
                int index = this.currentIndex;
                int count = this.numEntries;
                if (index == count) {
                    LinkEntry newEntry = SortedIdentityCacheList.this.computeValue(key);
                    if (newEntry.index == -1) {
                        SortedIdentityCacheList.this.insertAtEnd(newEntry, count);
                        this.numEntries = count + 1;
                        this.currentIndex = index + 1;
                        this.hasChanges = true;
                    }
                    return;
                }
                LinkEntry[] entries = SortedIdentityCacheList.this.entries;
                LinkEntry entry = entries[index];
                boolean bl = isValidEntry = entry.index == index;
                if (isValidEntry && entry.getKey() == key) {
                    this.currentIndex = index + 1;
                    return;
                }
                LinkEntry newEntry = SortedIdentityCacheList.this.computeValue(key);
                if (!isValidEntry) {
                    if (newEntry.index == -1) {
                        this.hasChanges = true;
                    }
                    if (newEntry.index == -1 || newEntry.index > index) {
                        newEntry.index = index;
                        entries[index] = newEntry;
                        this.currentIndex = index + 1;
                    }
                    return;
                }
                if (newEntry.index == -1) {
                    newEntry.index = -2;
                    this.hasChanges = true;
                    if (index == 0) {
                        LinkEntry first = this.firstCompactedEntry;
                        if (first == null) {
                            this.firstCompactedEntry = newEntry;
                            this.numCompacted = 1;
                        } else {
                            if (first.insertedAfter.isEmpty()) {
                                first.insertedAfter = new ArrayList(16);
                            }
                            first.insertedAfter.add(newEntry);
                            ++this.numCompacted;
                        }
                    } else {
                        LinkEntry previous = entries[index - 1];
                        if (previous.insertedAfter.isEmpty()) {
                            previous.insertedAfter = new ArrayList(16);
                            if (this.firstCompactedEntry == null) {
                                this.firstCompactedEntry = previous;
                            }
                        }
                        previous.insertedAfter.add(newEntry);
                        ++this.numCompacted;
                    }
                    return;
                }
                if (newEntry.index < index) {
                    return;
                }
                if (count == entries.length) {
                    int newSize = Math.max(20, count * 4 / 3);
                    entries = Arrays.copyOf(entries, newSize);
                    SortedIdentityCacheList.access$502(SortedIdentityCacheList.this, entries);
                }
                newEntry.index = index;
                entries[index] = newEntry;
                entry.index = count;
                entries[count] = entry;
                this.currentIndex = index + 1;
                this.numEntries = count + 1;
            }

            private void flatten() {
                if (this.numCompacted == 0) {
                    return;
                }
                LinkEntry[] entries = SortedIdentityCacheList.this.entries;
                int numEntries = this.numEntries;
                for (int i = this.currentIndex; i < numEntries; ++i) {
                    if (entries[i].index == i) continue;
                    entries[i] = null;
                }
                int numTotalEntries = numEntries + this.numCompacted;
                if (numTotalEntries > entries.length) {
                    int i;
                    int newSize = numTotalEntries * 4 / 3;
                    LinkEntry[] newEntries = new LinkEntry[newSize];
                    int destIndex = -1;
                    LinkEntry frontEntry = this.firstCompactedEntry;
                    if (frontEntry.index == -2) {
                        destIndex = SortedIdentityCacheList.setEntries(newEntries, destIndex, frontEntry);
                    }
                    int numValidEntries = this.currentIndex;
                    for (i = 0; i < numValidEntries; ++i) {
                        destIndex = SortedIdentityCacheList.setEntries(newEntries, destIndex, entries[i]);
                    }
                    for (i = numValidEntries; i < numEntries; ++i) {
                        LinkEntry entry = entries[i];
                        if (entry != null) {
                            destIndex = SortedIdentityCacheList.setEntries(newEntries, destIndex, entry);
                            continue;
                        }
                        ++destIndex;
                    }
                    SortedIdentityCacheList.access$502(SortedIdentityCacheList.this, newEntries);
                } else {
                    int startIndex = Math.max(0, this.firstCompactedEntry.index);
                    int safeIndex = this.currentIndex;
                    int srcIndex = numEntries;
                    int destIndex = numTotalEntries;
                    while (srcIndex > safeIndex) {
                        LinkEntry entry;
                        if ((entry = entries[--srcIndex]) != null) {
                            destIndex = SortedIdentityCacheList.setEntriesReverse(entries, destIndex, entry);
                            continue;
                        }
                        entries[--destIndex] = null;
                    }
                    while (srcIndex > startIndex) {
                        destIndex = SortedIdentityCacheList.setEntriesReverse(entries, destIndex, entries[--srcIndex]);
                    }
                    if (this.firstCompactedEntry.index == -2) {
                        SortedIdentityCacheList.setEntriesReverse(entries, destIndex, this.firstCompactedEntry);
                    }
                }
                this.firstCompactedEntry = null;
                this.numCompacted = 0;
                this.currentIndex = numTotalEntries - (numEntries - this.currentIndex);
                this.numEntries = numTotalEntries;
            }

            public void trimRemaining() {
                this.flatten();
                LinkEntry[] entries = SortedIdentityCacheList.this.entries;
                int numEntries = this.numEntries;
                int removeStart = this.currentIndex;
                while (--numEntries >= removeStart) {
                    LinkEntry entry = entries[numEntries];
                    if (entry == null || entry.index != numEntries) continue;
                    entries[numEntries] = null;
                    SortedIdentityCacheList.this.entriesByKey.remove(entry.getKey());
                    SortedIdentityCacheList.this.synchronizer.onRemoved(entry.getKey(), entry.getValue());
                    this.hasChanges = true;
                }
            }
        }
    }

    @FunctionalInterface
    public static interface Synchronizer<K, V> {
        public V onAdded(K var1);

        default public void onRemoved(K key, V value) {
        }
    }

    private static final class SortedIdentityCacheLinkedList<K, V>
    extends SortedIdentityCache<K, V> {
        private final Map<K, LinkEntry<K, V>> entriesByKey = new IdentityHashMap<K, LinkEntry<K, V>>();
        private final LinkEntry<K, V> first = new LinkEntry();
        private final LinkEntry<K, V> last = new LinkEntry();
        private boolean fillState = false;

        public SortedIdentityCacheLinkedList(Synchronizer<K, V> synchronizer) {
            super(synchronizer);
            this.first.next = this.last;
            this.last.prev = this.first;
        }

        @Override
        public int size() {
            return this.entriesByKey.size();
        }

        @Override
        public void clear() {
            LinkEntry curr = this.first.next;
            LinkEntry<K, V> last = this.last;
            this.entriesByKey.clear();
            this.first.next = last;
            last.prev = this.first;
            while (curr != last) {
                this.synchronizer.onRemoved(curr.key, curr.value);
                curr = curr.next;
            }
        }

        @Override
        public Iterator<V> iterator() {
            return new MappedIterator<Object>(this.first.next, LinkEntry::getValue);
        }

        @Override
        public Iterable<K> keys() {
            return () -> new MappedIterator<Object>(this.first.next, LinkEntry::getKey);
        }

        @Override
        public Iterable<Map.Entry<K, V>> entries() {
            return () -> new MappedIterator(this.first.next, Function.identity());
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            LinkEntry<K, V> last = this.last;
            LinkEntry curr = this.first.next;
            while (curr != last) {
                action.accept(curr.value);
                curr = curr.next;
            }
        }

        @Override
        public boolean sync(Consumer<Consumer<K>> fillMethod) {
            Syncher syncher = new Syncher();
            fillMethod.accept(syncher);
            syncher.trimRemaining();
            return syncher.hasChanges;
        }

        @Override
        public V remove(K key) {
            LinkEntry<K, V> entry = this.entriesByKey.remove(key);
            if (entry != null) {
                entry.unbind();
                this.synchronizer.onRemoved(entry.key, entry.value);
                return entry.value;
            }
            return null;
        }

        @Override
        public V get(K key) {
            LinkEntry<K, V> entry = this.entriesByKey.get(key);
            return entry == null ? null : (V)entry.value;
        }

        @Override
        public V addFirst(K key) {
            LinkEntry<K, V> newEntry = this.computeValue(key);
            if (newEntry.next == null) {
                newEntry.bind(this.first, this.first.next);
            }
            return newEntry.value;
        }

        @Override
        public V addLast(K key) {
            LinkEntry newEntry = this.computeValue(key);
            if (newEntry.next == null) {
                newEntry.bind(this.last.prev, this.last);
            }
            return newEntry.value;
        }

        private LinkEntry<K, V> computeValue(K key) {
            return this.entriesByKey.computeIfAbsent(key, k -> new LinkEntry(k, this.synchronizer.onAdded(k), this.fillState));
        }

        private static final class LinkEntry<K, V>
        implements Map.Entry<K, V> {
            public final K key;
            public final V value;
            public LinkEntry<K, V> prev;
            public LinkEntry<K, V> next;
            private boolean fillState;

            public LinkEntry() {
                this.key = new Object();
                this.value = null;
                this.fillState = false;
            }

            public LinkEntry(K key, V value, boolean fillState) {
                this.key = key;
                this.value = value;
                this.fillState = fillState;
            }

            public void bind(LinkEntry<K, V> prev, LinkEntry<K, V> next) {
                this.prev = prev;
                this.next = next;
                prev.next = this;
                next.prev = this;
            }

            public void unbind() {
                LinkEntry<K, V> next;
                LinkEntry<K, V> prev = this.prev;
                prev.next = next = this.next;
                next.prev = prev;
            }

            @Override
            public K getKey() {
                return this.key;
            }

            @Override
            public V getValue() {
                return this.value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException("Values are immutable");
            }
        }

        private final class MappedIterator<E>
        implements Iterator<E> {
            private final Function<LinkEntry<K, V>, E> mapper;
            private final LinkEntry<K, V> end;
            private LinkEntry<K, V> curr;

            public MappedIterator(LinkEntry<K, V> start, Function<LinkEntry<K, V>, E> mapper) {
                this.mapper = mapper;
                this.end = SortedIdentityCacheLinkedList.this.last;
                this.curr = start;
            }

            @Override
            public boolean hasNext() {
                return this.curr != this.end;
            }

            @Override
            public E next() {
                LinkEntry curr = this.curr;
                if (curr == this.end) {
                    throw new NoSuchElementException("End reached");
                }
                this.curr = curr.next;
                return this.mapper.apply(curr);
            }

            @Override
            public void remove() {
                LinkEntry lastReturned = this.curr.prev;
                if (lastReturned == SortedIdentityCacheLinkedList.this.first) {
                    throw new NoSuchElementException("Next not called before remove()");
                }
                lastReturned.unbind();
                SortedIdentityCacheLinkedList.this.entriesByKey.remove(lastReturned.key);
                SortedIdentityCacheLinkedList.this.synchronizer.onRemoved(lastReturned.key, lastReturned.value);
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                while (this.curr != this.end) {
                    action.accept(this.mapper.apply(this.curr));
                    this.curr = this.curr.next;
                }
            }
        }

        private final class Syncher
        implements Consumer<K> {
            private LinkEntry<K, V> prev;
            private LinkEntry<K, V> curr;
            public boolean hasChanges;

            public Syncher() {
                this.prev = SortedIdentityCacheLinkedList.this.first;
                this.curr = this.prev.next;
                this.hasChanges = false;
                SortedIdentityCacheLinkedList.this.fillState = !SortedIdentityCacheLinkedList.this.fillState;
            }

            @Override
            public void accept(K key) {
                LinkEntry curr = this.curr;
                boolean fillState = SortedIdentityCacheLinkedList.this.fillState;
                if (key == curr.key) {
                    curr.fillState = fillState;
                    this.prev = curr;
                    this.curr = curr.next;
                    return;
                }
                LinkEntry newEntry = SortedIdentityCacheLinkedList.this.computeValue(key);
                if (newEntry.next != null) {
                    if (newEntry.fillState == fillState) {
                        return;
                    }
                    newEntry.unbind();
                    newEntry.bind(this.prev, curr.next);
                    newEntry.fillState = fillState;
                    curr.bind(((SortedIdentityCacheLinkedList)SortedIdentityCacheLinkedList.this).last.prev, SortedIdentityCacheLinkedList.this.last);
                    this.prev = newEntry;
                    this.curr = newEntry.next;
                } else {
                    newEntry.bind(this.prev, curr);
                    newEntry.fillState = fillState;
                    this.prev = newEntry;
                    this.hasChanges = true;
                }
            }

            public void trimRemaining() {
                LinkEntry end;
                this.prev.next = end = SortedIdentityCacheLinkedList.this.last;
                end.prev = this.prev;
                LinkEntry curr = this.curr;
                while (curr != end) {
                    SortedIdentityCacheLinkedList.this.entriesByKey.remove(curr.key);
                    SortedIdentityCacheLinkedList.this.synchronizer.onRemoved(curr.key, curr.value);
                    this.hasChanges = true;
                    curr = curr.next;
                }
            }
        }
    }
}

