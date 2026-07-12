/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlListNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeEntrySetProxy;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeKeySetProxy;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeValueCollectionProxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class YamlNodeMapProxy
implements Map<String, Object> {
    private final YamlNodeAbstract<?> _node;

    public YamlNodeMapProxy(YamlNodeAbstract<?> node) {
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
    public boolean containsKey(Object key) {
        return this._node.indexOfKey(key) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        return this._node.indexOfValue(value) != -1;
    }

    @Override
    public Object get(Object key) {
        int index = this._node.indexOfKey(key);
        return index == -1 ? null : this._node._children.get(index).getValue();
    }

    @Override
    public Object put(String key, Object value) {
        int index = this._node.indexOfKey(key);
        if (index == -1) {
            YamlEntry entry = this._node.createChildEntry(this._node._children.size(), this._node.getYamlPath().child(key));
            entry.setValue(value);
            return null;
        }
        YamlEntry entry = this._node._children.get(index);
        Object previousValue = entry.getValue();
        entry.setValue(value);
        return previousValue;
    }

    @Override
    public Object remove(Object key) {
        int index = this._node.indexOfKey(key);
        if (index == -1) {
            return null;
        }
        return this._node.removeChildEntryAtAndGetValue(index);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<String> keySet() {
        return YamlNodeKeySetProxy.stringKeysOf(this._node);
    }

    @Override
    public Collection<Object> values() {
        if (this._node instanceof YamlListNode) {
            return (YamlListNode)this._node;
        }
        return new YamlNodeValueCollectionProxy(this._node);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return new YamlNodeEntrySetProxy(this._node);
    }
}

