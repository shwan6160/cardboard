/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class YamlNodeEntrySetProxy
implements Set<Map.Entry<String, Object>> {
    private final YamlNodeAbstract<?> _node;

    public YamlNodeEntrySetProxy(YamlNodeAbstract<?> node) {
        this._node = node;
    }

    @Override
    public void clear() {
        this._node.clear();
    }

    @Override
    public int size() {
        return this._node._children.size();
    }

    @Override
    public boolean isEmpty() {
        return this._node._children.isEmpty();
    }

    private int indexOfEntry(Object o) {
        Map.Entry e;
        int index;
        if (o instanceof Map.Entry && (index = this._node.indexOfKey((e = (Map.Entry)o).getKey())) != -1 && LogicUtil.bothNullOrEqual(this._node._children.get(index).getValue(), e.getValue())) {
            return index;
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOfEntry(o) != -1;
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return new Iterator<Map.Entry<String, Object>>(){
            private int _index = 0;
            private boolean _canRemove = false;

            @Override
            public boolean hasNext() {
                return this._index < ((YamlNodeEntrySetProxy)YamlNodeEntrySetProxy.this)._node._children.size();
            }

            @Override
            public Map.Entry<String, Object> next() {
                if (this.hasNext()) {
                    this._canRemove = true;
                    return ((YamlNodeEntrySetProxy)YamlNodeEntrySetProxy.this)._node._children.get(this._index++);
                }
                throw new NoSuchElementException("No next element available");
            }

            @Override
            public void remove() {
                if (!this._canRemove) {
                    throw new NoSuchElementException("Next must be called before remove()");
                }
                this._canRemove = false;
                YamlNodeEntrySetProxy.this._node.removeChildEntryAtAndGetValue(--this._index);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean remove(Object o) {
        int index = this.indexOfEntry(o);
        if (index == -1) {
            return false;
        }
        this._node.removeChildEntryAtAndGetValue(index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return CollectionBasics.containsAll(this, c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return CollectionBasics.removeAll(this, c);
    }

    @Override
    public boolean add(Map.Entry<String, Object> e) {
        throw new UnsupportedOperationException("Node entries cannot be added");
    }

    @Override
    public boolean addAll(Collection<? extends Map.Entry<String, Object>> c) {
        throw new UnsupportedOperationException("Node entries cannot be added");
    }
}

