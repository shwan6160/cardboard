/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlListNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeIndexedValueList;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeLinkedValue;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class YamlNodeLazyCreateValueList
extends AbstractList<Object>
implements YamlNodeLinkedValue {
    private final boolean _createIndexedValueList;
    private YamlNodeAbstract<?> _parent;
    private String _path;
    private List<Object> _list;

    public YamlNodeLazyCreateValueList(YamlNodeAbstract<?> parent, String path, boolean createIndexedValueList) {
        this._createIndexedValueList = createIndexedValueList;
        this._parent = parent;
        this._path = path;
        this._list = null;
    }

    @Override
    public void clear() {
        this.getList().clear();
    }

    @Override
    public int size() {
        return this.getList().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getList().isEmpty();
    }

    @Override
    public Iterator<Object> iterator() {
        return this.getList().iterator();
    }

    @Override
    public boolean contains(Object value) {
        return this.getList().contains(value);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.getList().containsAll(c);
    }

    @Override
    public int indexOf(Object value) {
        return this.getList().indexOf(value);
    }

    @Override
    public int lastIndexOf(Object value) {
        return this.getList().lastIndexOf(value);
    }

    @Override
    public Object get(int index) {
        return this.getList().get(index);
    }

    @Override
    public Object set(int index, Object value) {
        return this.getList().set(index, value);
    }

    @Override
    public Object remove(int index) {
        return this.getList().remove(index);
    }

    @Override
    public boolean remove(Object value) {
        return this.getList().remove(value);
    }

    @Override
    public boolean removeAll(Collection<? extends Object> c) {
        return this.createList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<? extends Object> c) {
        return this.createList().retainAll(c);
    }

    @Override
    public boolean add(Object value) {
        return this.createList().add(value);
    }

    @Override
    public void add(int index, Object value) {
        this.createList().add(index, value);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return this.createList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        return this.createList().addAll(index, c);
    }

    @Override
    public void assignTo(YamlEntry entry) {
        this._parent = entry.getParentNode();
        this._path = entry.getKey();
        if (this._list != null) {
            entry.setValue(this._list);
        } else {
            this._list = this._createIndexedValueList ? YamlNodeIndexedValueList.sortAndCreate(entry.createNodeValue()) : entry.createListNodeValue();
        }
    }

    private List<Object> getList() {
        if (this._list != null) {
            return this._list;
        }
        YamlEntry entry = this._parent.getEntryIfExists(this._path);
        if (entry == null) {
            return Collections.emptyList();
        }
        Object value = entry.getValue();
        this._list = value instanceof YamlListNode ? (YamlListNode)value : (value instanceof YamlNodeAbstract ? YamlNodeIndexedValueList.sortAndCreate((YamlNodeAbstract)value) : (this._createIndexedValueList ? YamlNodeIndexedValueList.sortAndCreate(entry.createNodeValue()) : entry.createListNodeValue()));
        return this._list;
    }

    private List<Object> createList() {
        if (this._list == null) {
            YamlEntry entry = this._parent.getEntry(this._path);
            this._list = this._createIndexedValueList ? YamlNodeIndexedValueList.sortAndCreate(entry.createNodeValue()) : entry.createListNodeValue();
        }
        return this._list;
    }
}

