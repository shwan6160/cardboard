/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeLinkedValue;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeMappedIterator;
import java.util.Collection;
import java.util.Iterator;

public class YamlNodeValueCollectionProxy
implements Collection<Object>,
YamlNodeLinkedValue {
    private final YamlNodeAbstract<?> _node;

    public YamlNodeValueCollectionProxy(YamlNodeAbstract<?> node) {
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

    @Override
    public boolean contains(Object o) {
        return this._node.indexOfValue(o) != -1;
    }

    @Override
    public Iterator<Object> iterator() {
        return YamlNodeMappedIterator.shallow(this._node, YamlEntry::getValue);
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
        int index = this._node.indexOfValue(o);
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
    public boolean removeAll(Collection<?> c) {
        return CollectionBasics.removeAll(this, c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException("Node values can not be added to");
    }

    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException("Node values can not be added to");
    }

    @Override
    public void assignTo(YamlEntry entry) {
        entry.setValue(this._node);
    }
}

