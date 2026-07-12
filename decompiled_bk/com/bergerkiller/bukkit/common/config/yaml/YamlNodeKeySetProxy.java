/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeMappedIterator;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

class YamlNodeKeySetProxy<T>
implements Set<T> {
    private final YamlNodeAbstract<?> _node;
    private final KeyConverter<T> _keyConv;

    public static YamlNodeKeySetProxy<String> stringKeysOf(YamlNodeAbstract<?> node) {
        return new YamlNodeKeySetProxy<String>(node, KeyConverter.STRING_KEYS);
    }

    public static YamlNodeKeySetProxy<YamlPath> yamlPathKeysOf(YamlNodeAbstract<?> node) {
        return new YamlNodeKeySetProxy<YamlPath>(node, KeyConverter.PATH_KEYS);
    }

    private YamlNodeKeySetProxy(YamlNodeAbstract<?> node, KeyConverter<T> keyConv) {
        this._node = node;
        this._keyConv = keyConv;
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
        return this._node.indexOfKey(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return YamlNodeMappedIterator.shallow(this._node, e -> this._keyConv.toKey(this._node, e.getYamlPath()));
    }

    @Override
    public boolean remove(Object o) {
        YamlPath path = this._keyConv.toPath(this._node, o);
        if (path == null) {
            return false;
        }
        int index = this._node.indexOfYamlPath(path);
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
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }

    public static interface KeyConverter<T> {
        public static final KeyConverter<String> STRING_KEYS = new KeyConverter<String>(){

            @Override
            public YamlPath toPath(YamlNodeAbstract<?> node, Object key) {
                if (key instanceof String) {
                    return node.getYamlPath().childWithName((String)key);
                }
                return null;
            }

            @Override
            public String toKey(YamlNodeAbstract<?> node, YamlPath absolutePath) {
                return absolutePath.name();
            }
        };
        public static final KeyConverter<YamlPath> PATH_KEYS = new KeyConverter<YamlPath>(){

            @Override
            public YamlPath toPath(YamlNodeAbstract<?> node, Object key) {
                YamlPath path;
                if (key instanceof YamlPath && (path = (YamlPath)key).parent().equals(node.getYamlPath())) {
                    return path;
                }
                return null;
            }

            @Override
            public YamlPath toKey(YamlNodeAbstract<?> node, YamlPath absolutePath) {
                return absolutePath.makeRelative(node.getYamlPath());
            }
        };

        public YamlPath toPath(YamlNodeAbstract<?> var1, Object var2);

        public T toKey(YamlNodeAbstract<?> var1, YamlPath var2);
    }
}

