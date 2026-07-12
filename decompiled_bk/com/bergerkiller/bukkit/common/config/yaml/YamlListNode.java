/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.AbstractListProxy;
import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class YamlListNode
extends YamlNodeAbstract<YamlListNode>
implements List<Object> {
    public YamlListNode() {
    }

    protected YamlListNode(YamlEntry entry) {
        super(entry);
    }

    @Override
    protected YamlListNode createNode(YamlEntry entry) {
        return new YamlListNode(entry);
    }

    @Override
    protected Object removeChildEntryAtWithoutEventAndGetValue(int index) {
        Object removedValue = super.removeChildEntryAtWithoutEventAndGetValue(index);
        for (int i = index; i < this._children.size(); ++i) {
            ((YamlEntry)this._children.get(i)).setPath(this.getYamlPath().listChild(i));
        }
        return removedValue;
    }

    @Override
    protected YamlEntry createChildEntry(int index, YamlPath path) {
        YamlEntry created = super.createChildEntry(index, path);
        for (int i = this._children.size() - 1; i >= index; --i) {
            ((YamlEntry)this._children.get(i)).setPath(this.getYamlPath().listChild(i));
        }
        this._entry.callChangeListeners();
        return created;
    }

    @Override
    public int size() {
        return this._children.size();
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < YamlListNode.this._children.size();
            }

            @Override
            public Object next() {
                if (this.index >= YamlListNode.this._children.size()) {
                    throw new NoSuchElementException("No next element is available");
                }
                return ((YamlEntry)YamlListNode.this._children.get(this.index++)).getValue();
            }

            @Override
            public void remove() {
                YamlListNode.this.remove(this.index - 1);
            }
        };
    }

    @Override
    public Object get(int index) {
        return ((YamlEntry)this._children.get(index)).getValue();
    }

    @Override
    public Object set(int index, Object element) {
        return ((YamlEntry)this._children.get(index)).setValue(element);
    }

    @Override
    public boolean add(Object e) {
        this.add(this._children.size(), e);
        return true;
    }

    @Override
    public void add(int index, Object element) {
        this.createChildEntry(index, this.getYamlPath().listChild(index)).setValue(element);
    }

    @Override
    public boolean remove(Object o) {
        int index = this.indexOfValue(o);
        if (index != -1) {
            this.removeChildEntryAtWithoutEventAndGetValue(index);
            this._entry.callChangeListeners();
            return true;
        }
        return false;
    }

    @Override
    public Object remove(int index) {
        return this.removeChildEntryAtAndGetValue(index);
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOfValue(o) != -1;
    }

    @Override
    public int indexOf(Object o) {
        return this.indexOfValue(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.lastIndexOfValue(o);
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
    public boolean containsAll(Collection<?> c) {
        return CollectionBasics.containsAll(this, c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return this.addAll(this._children.size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        for (Object object : c) {
            YamlEntry entry = super.createChildEntry(index, this.getYamlPath().listChild(index));
            this._entry.callChangeListeners();
            entry.setValue(object);
            ++index;
        }
        for (int i = this._children.size() - 1; i >= index; --i) {
            ((YamlEntry)this._children.get(i)).setPath(this.getYamlPath().listChild(i));
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.removeOrRetainAll(c, true);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.removeOrRetainAll(c, false);
    }

    private boolean removeOrRetainAll(Collection<?> c, boolean remove) {
        int first_mod = Integer.MAX_VALUE;
        int i = this._children.size();
        while (--i >= 0) {
            if (remove != c.contains(((YamlEntry)this._children.get(i)).getValue())) continue;
            first_mod = i;
            super.removeChildEntryAtWithoutEventAndGetValue(i);
        }
        for (i = first_mod; i < this._children.size(); ++i) {
            ((YamlEntry)this._children.get(i)).setPath(this.getYamlPath().listChild(i));
        }
        if (first_mod != Integer.MAX_VALUE) {
            this._entry.callChangeListeners();
            return true;
        }
        return false;
    }

    @Override
    public ListIterator<Object> listIterator() {
        return AbstractListProxy.create(this).listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return AbstractListProxy.create(this).listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return AbstractListProxy.create(this).subList(fromIndex, toIndex);
    }
}

