/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.google.common.collect.MapMaker;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public class ImmutableCachedSet<E>
implements Iterable<E> {
    private final Cache<E> cache;
    private final Set<E> values;
    private final Set<E> unmodifiableValues;
    private final int hashCode;
    private Cache.AddOperation<E> lastAddOperationGC = null;
    private WeakReference<Cache.AddOperation<E>> lastAddOperation = LogicUtil.nullWeakReference();
    private Cache.RemoveOperation<E> lastRemoveOperation = Cache.RemoveOperation.none();

    public static <E> ImmutableCachedSet<E> createNew() {
        return new ImmutableCachedSet<E>();
    }

    public static ImmutableCachedSet<Player> createNewPlayerSet() {
        ImmutableCachedSet<Player> set = ImmutableCachedSet.createNew();
        CommonListener.registerImmutablePlayerSet(set);
        return set;
    }

    protected ImmutableCachedSet() {
        this.cache = new Cache(this);
        this.values = Collections.emptySet();
        this.unmodifiableValues = Collections.emptySet();
        this.hashCode = 0;
    }

    protected ImmutableCachedSet(ImmutableCachedSet<E> emptyRoot, Set<E> values, int hashCode) {
        this.cache = emptyRoot.cache;
        this.values = values;
        this.unmodifiableValues = Collections.unmodifiableSet(values);
        this.hashCode = hashCode;
    }

    protected ImmutableCachedSet<E> createNew(Set<E> values, int hashCode) {
        return new ImmutableCachedSet<E>(this, values, hashCode);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public int size() {
        return this.values.size();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return this.unmodifiableValues.iterator();
    }

    public Stream<E> stream() {
        return this.values.stream();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ImmutableCachedSet) {
            ImmutableCachedSet other = (ImmutableCachedSet)o;
            return this.values.equals(other.values);
        }
        if (o instanceof Collection) {
            Collection other = (Collection)o;
            return this.values.equals(other);
        }
        return false;
    }

    public String toString() {
        Iterator<E> iter = this.values.iterator();
        if (iter.hasNext()) {
            StringBuilder str = new StringBuilder();
            str.append('{').append(iter.next());
            while (iter.hasNext()) {
                str.append(", ").append(iter.next());
            }
            str.append('}');
            return str.toString();
        }
        return "{}";
    }

    public boolean contains(E o) {
        return this.values.contains(o);
    }

    public boolean containsAll(Collection<E> values) {
        return this.values.containsAll(values);
    }

    public ImmutableCachedSet<E> remove(E value) {
        ImmutableCachedSet result;
        Cache.RemoveOperation<E> op = this.lastRemoveOperation;
        if (op.removedElement.equals(value) && (result = (ImmutableCachedSet)op.result.get()) != null) {
            return result;
        }
        if (!this.values.contains(value)) {
            return this;
        }
        ImmutableCachedSet result2 = this.values.size() == 1 ? this.cache.EMPTY : this.cache.remove(this, value);
        this.lastRemoveOperation = new Cache.RemoveOperation(value, result2);
        return result2;
    }

    public ImmutableCachedSet<E> add(E value) {
        if (this.values.contains(value)) {
            return this;
        }
        Cache.AddOperation<E> op = (Cache.AddOperation<E>)this.lastAddOperation.get();
        if (op != null && op.addedElement.equals(value)) {
            return op.result;
        }
        ImmutableCachedSet<E> result = this.cache.add(this, value);
        op = new Cache.AddOperation<E>(value, result, this);
        this.lastAddOperation = new WeakReference<Cache.AddOperation<E>>(op);
        result.lastAddOperationGC = op;
        return result;
    }

    public ImmutableCachedSet<E> addAll(Iterable<E> values) {
        ImmutableCachedSet<E> result = this;
        for (E value : values) {
            result = result.add(value);
        }
        return result;
    }

    public ImmutableCachedSet<E> removeAll(Iterable<E> values) {
        ImmutableCachedSet<E> result = this;
        for (E value : values) {
            result = result.remove(value);
        }
        return result;
    }

    public ImmutableCachedSet<E> addOrRemove(E value, boolean add) {
        return add ? this.add(value) : this.remove(value);
    }

    public ImmutableCachedSet<E> clear() {
        return this.cache.EMPTY;
    }

    public void releaseFromCache(E value) {
        if (value != null) {
            this.cache.release(value);
        }
    }

    private static final class Cache<E> {
        private final ConcurrentMap<Key<E>, ImmutableCachedSet<E>> _cache;
        public final ImmutableCachedSet<E> EMPTY;

        public Cache(ImmutableCachedSet<E> empty) {
            this.EMPTY = empty;
            this._cache = new MapMaker().concurrencyLevel(1).weakValues().makeMap();
        }

        public ImmutableCachedSet<E> add(ImmutableCachedSet<E> current, E elementToAdd) {
            Key key;
            try {
                key = current.isEmpty() ? new KeyAddOne<E>(elementToAdd) : new KeyAdd<E>(current, elementToAdd);
            }
            catch (NullPointerException ex) {
                if (elementToAdd == null) {
                    throw new IllegalArgumentException("Input element to add is null");
                }
                throw ex;
            }
            ImmutableCachedSet<E> set = (ImmutableCachedSet<E>)this._cache.get(key);
            if (set == null) {
                key = new KeyValues<E>(key);
                set = this.EMPTY.createNew(key.values(), key.hashCode());
                this._cache.put(key, set);
            }
            return set;
        }

        public ImmutableCachedSet<E> remove(ImmutableCachedSet<E> current, Object elementToRemove) {
            Key key = new KeyRemove<E>(current, elementToRemove);
            ImmutableCachedSet<E> set = (ImmutableCachedSet<E>)this._cache.get(key);
            if (set == null) {
                key = new KeyValues<E>(key);
                set = this.EMPTY.createNew(key.values(), key.hashCode());
                this._cache.put(key, set);
            }
            return set;
        }

        public void release(E value) {
            Iterator iter = this._cache.values().iterator();
            while (iter.hasNext()) {
                ImmutableCachedSet set = (ImmutableCachedSet)iter.next();
                if (!set.contains(value)) continue;
                AddOperation op = set.lastAddOperationGC;
                if (op != null) {
                    set.lastAddOperationGC = null;
                    ImmutableCachedSet creator = (ImmutableCachedSet)op.creator.get();
                    if (creator != null && creator.lastAddOperation.get() == op) {
                        creator.lastAddOperation = LogicUtil.nullWeakReference();
                    }
                }
                set.lastRemoveOperation = RemoveOperation.none();
                iter.remove();
            }
        }

        private static final class KeyAddOne<E>
        extends Key<E> {
            private final E added;

            public KeyAddOne(E added) {
                super(Collections.emptySet(), added.hashCode());
                this.added = added;
            }

            @Override
            public Set<E> values() {
                return Collections.singleton(this.added);
            }

            @Override
            public boolean equals(Object o) {
                return this.equalsValues(((KeyValues)o).values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return values.size() == 1 && values.contains(this.added);
            }
        }

        private static final class KeyAdd<E>
        extends Key<E> {
            private final E added;

            public KeyAdd(ImmutableCachedSet<E> current, E added) {
                super(((ImmutableCachedSet)current).values, ((ImmutableCachedSet)current).hashCode ^ added.hashCode());
                this.added = added;
            }

            @Override
            public Set<E> values() {
                HashSet<E> combined = new HashSet<E>(this.values.size() + 1);
                combined.addAll(this.values);
                combined.add(this.added);
                return combined;
            }

            @Override
            public boolean equals(Object o) {
                return this.equalsValues(((KeyValues)o).values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return values.size() == this.values.size() + 1 && values.containsAll(this.values) && values.contains(this.added);
            }
        }

        private static final class KeyValues<E>
        extends Key<E> {
            public KeyValues(Key<E> input) {
                super(input.values(), input.hashCode);
            }

            @Override
            public Set<E> values() {
                return this.values;
            }

            @Override
            public boolean equals(Object o) {
                return ((Key)o).equalsValues(this.values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return this.values.equals(values);
            }
        }

        private static abstract class Key<E> {
            public final Set<E> values;
            public final int hashCode;

            public Key(Set<E> values, int hashCode) {
                this.values = values;
                this.hashCode = hashCode;
            }

            public final int hashCode() {
                return this.hashCode;
            }

            public abstract boolean equals(Object var1);

            public abstract boolean equalsValues(Set<E> var1);

            public abstract Set<E> values();
        }

        private static final class KeyRemove<E>
        extends Key<E> {
            private final Object removed;

            public KeyRemove(ImmutableCachedSet<E> current, Object removed) {
                super(((ImmutableCachedSet)current).values, ((ImmutableCachedSet)current).hashCode ^ removed.hashCode());
                this.removed = removed;
            }

            @Override
            public Set<E> values() {
                HashSet combined = new HashSet(this.values);
                combined.remove(this.removed);
                return combined;
            }

            @Override
            public boolean equals(Object o) {
                return this.equalsValues(((KeyValues)o).values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return values.size() == this.values.size() - 1 && this.values.containsAll(values) && !values.contains(this.removed);
            }
        }

        private static class AddOperation<E> {
            public final E addedElement;
            public final ImmutableCachedSet<E> result;
            public final WeakReference<ImmutableCachedSet<E>> creator;

            public AddOperation(E addedElement, ImmutableCachedSet<E> result, ImmutableCachedSet<E> creator) {
                this.addedElement = addedElement;
                this.result = result;
                this.creator = new WeakReference<ImmutableCachedSet<E>>(creator);
            }
        }

        private static class RemoveOperation<E> {
            private static final RemoveOperation NONE = new RemoveOperation();
            public final Object removedElement;
            public final WeakReference<ImmutableCachedSet<E>> result;

            private RemoveOperation() {
                this.removedElement = new NoneValue();
                this.result = LogicUtil.nullWeakReference();
            }

            public RemoveOperation(Object removedElement, ImmutableCachedSet<E> result) {
                this.removedElement = removedElement;
                this.result = new WeakReference<ImmutableCachedSet<E>>(result);
            }

            public static <E> RemoveOperation<E> none() {
                return NONE;
            }

            private static final class NoneValue {
                private NoneValue() {
                }

                public boolean equals(Object o) {
                    return false;
                }
            }
        }

        @FunctionalInterface
        public static interface Constructor<E> {
            public ImmutableCachedSet<E> create(Cache<E> var1, Set<E> var2, int var3);
        }
    }
}

