/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

interface YamlNodeMappedIterator<T>
extends Iterator<T> {
    public static <T> YamlNodeMappedIterator<T> shallow(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
        return new ShallowMappedIterator<T>(node, mapper);
    }

    public static <T> YamlNodeMappedIterator<T> deep(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
        return new DeepMappedIterator<T>(node, mapper);
    }

    @Override
    public boolean hasNext();

    @Override
    public T next();

    @Override
    public void remove();

    public Object setValue(Object var1);

    public static class ShallowMappedIterator<T>
    implements YamlNodeMappedIterator<T> {
        private final ShallowEntryIterator _iter;
        private final Function<YamlEntry, T> _mapper;

        public ShallowMappedIterator(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
            this._iter = new ShallowEntryIterator(node);
            this._mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return this._iter.hasNext();
        }

        @Override
        public T next() {
            return this._mapper.apply(this._iter.next());
        }

        @Override
        public void remove() {
            this._iter.remove();
        }

        @Override
        public Object setValue(Object value) {
            return this._iter.setValue(value);
        }
    }

    public static class DeepMappedIterator<T>
    implements YamlNodeMappedIterator<T> {
        private ChainedShallowEntryIterator _iter;
        private ChainedShallowEntryIterator _next;
        private final Function<YamlEntry, T> _mapper;

        public DeepMappedIterator(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
            this._next = this._iter = new ChainedShallowEntryIterator(node, null);
            this._mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return this._next.chainedHasNext();
        }

        @Override
        public T next() {
            this._iter = this._next;
            if (this._iter.hasNext()) {
                return this.handleNext();
            }
            while (this._iter._after != null) {
                this._next = this._iter = this._iter._after;
                if (!this._iter.hasNext()) continue;
                return this.handleNext();
            }
            throw new NoSuchElementException("No next element available");
        }

        private T handleNext() {
            YamlEntry e = this._iter.unsafeNext();
            if (e.isAbstractNode()) {
                this._next = new ChainedShallowEntryIterator(e.getAbstractNode(), this._iter);
            }
            return this._mapper.apply(e);
        }

        @Override
        public void remove() {
            this._iter.remove();
        }

        @Override
        public Object setValue(Object value) {
            return this._iter.setValue(value);
        }
    }

    public static class ShallowEntryIterator
    implements YamlNodeMappedIterator<YamlEntry> {
        private final YamlNodeAbstract<?> _node;
        private int _index = 0;
        private boolean _nextCalled = false;

        public ShallowEntryIterator(YamlNodeAbstract<?> node) {
            this._node = node;
        }

        @Override
        public boolean hasNext() {
            return this._index < this._node._children.size();
        }

        protected YamlEntry unsafeNext() {
            this._nextCalled = true;
            return this._node._children.get(this._index++);
        }

        @Override
        public YamlEntry next() {
            if (this.hasNext()) {
                return this.unsafeNext();
            }
            throw new NoSuchElementException("No next element available");
        }

        @Override
        public void remove() {
            if (!this._nextCalled) {
                throw new NoSuchElementException("Next must be called before remove()");
            }
            this._nextCalled = false;
            this._node.removeChildEntryAtAndGetValue(--this._index);
        }

        @Override
        public Object setValue(Object value) {
            if (this._nextCalled) {
                return this._node._children.get(this._index - 1).setValue(value);
            }
            throw new NoSuchElementException("Next must be called before set(value)");
        }
    }

    public static class ChainedShallowEntryIterator
    extends ShallowEntryIterator {
        private final ChainedShallowEntryIterator _after;

        public ChainedShallowEntryIterator(YamlNodeAbstract<?> node, ChainedShallowEntryIterator after) {
            super(node);
            this._after = after;
        }

        public boolean chainedHasNext() {
            return this.hasNext() || this._after != null && this._after.chainedHasNext();
        }
    }
}

