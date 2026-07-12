/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeMappedIterator;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

class YamlNodeDeepKeySetProxy
implements Set<YamlPath> {
    private final YamlNodeAbstract<?> _node;

    public static YamlNodeDeepKeySetProxy yamlPathKeysOf(YamlNodeAbstract<?> node) {
        return new YamlNodeDeepKeySetProxy(node);
    }

    private YamlNodeDeepKeySetProxy(YamlNodeAbstract<?> node) {
        this._node = node;
    }

    @Override
    public void clear() {
        this._node.clear();
    }

    @Override
    public int size() {
        Iterator<YamlPath> iter = this.iterator();
        int size = 0;
        while (iter.hasNext()) {
            iter.next();
            ++size;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this._node._children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this._node.indexOfKey(o) != -1;
    }

    @Override
    public Iterator<YamlPath> iterator() {
        return YamlNodeMappedIterator.deep(this._node, e -> e.getYamlPath().makeRelative(this._node.getYamlPath()));
    }

    @Override
    public boolean remove(Object o) {
        YamlEntry entry;
        if (o instanceof YamlPath && (entry = this._node.getEntryIfExists((YamlPath)o)) != null && entry.getParentNode() != null) {
            entry.getParentNode().removeChildEntry(entry);
            return true;
        }
        return false;
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
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean add(YamlPath e) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }

    @Override
    public boolean addAll(Collection<? extends YamlPath> c) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }
}

