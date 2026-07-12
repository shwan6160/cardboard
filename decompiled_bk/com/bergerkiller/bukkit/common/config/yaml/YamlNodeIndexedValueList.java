/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeLinkedValue;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import java.util.AbstractList;
import java.util.Collections;
import java.util.Comparator;

public class YamlNodeIndexedValueList
extends AbstractList<Object>
implements YamlNodeLinkedValue {
    private final YamlNodeAbstract<?> _node;
    private boolean _namedByIndex;

    private YamlNodeIndexedValueList(YamlNodeAbstract<?> node) {
        this._node = node;
        this._namedByIndex = false;
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
    public boolean contains(Object value) {
        return this._node.indexOfValue(value) != -1;
    }

    @Override
    public int indexOf(Object value) {
        return this._node.indexOfValue(value);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this._node.lastIndexOfValue(o);
    }

    @Override
    public Object get(int index) {
        return this._node._children.get(index).getValue();
    }

    @Override
    public Object set(int index, Object value) {
        return this._node._children.get(index).setValue(value);
    }

    @Override
    public Object remove(int index) {
        Object removedValue = this._node.removeChildEntryAtWithoutEventAndGetValue(index);
        if (this._namedByIndex) {
            this.updateIndexFrom(index);
        }
        this._node._entry.callChangeListeners();
        return removedValue;
    }

    @Override
    public boolean remove(Object value) {
        int index = this._node.indexOfValue(value);
        if (index == -1) {
            return false;
        }
        this.remove(index);
        return true;
    }

    @Override
    public boolean add(Object value) {
        this.add(this._node._children.size(), value);
        return true;
    }

    @Override
    public void add(int index, Object value) {
        if (!this._namedByIndex) {
            this._namedByIndex = true;
            this.updateIndexFrom(0);
        }
        YamlEntry newEntry = this._node.createChildEntry(index, this.getIndexedPath(index));
        this.updateIndexFrom(index + 1);
        this._node._entry.callChangeListeners();
        newEntry.setValue(value);
    }

    @Override
    public void assignTo(YamlEntry entry) {
        entry.setValue(this._node);
    }

    private void updateIndexFrom(int index) {
        while (index < this._node._children.size()) {
            this._node._children.get(index).setPath(this.getIndexedPath(index));
            ++index;
        }
    }

    private YamlPath getIndexedPath(int index) {
        return this._node.getYamlPath().child(Integer.toString(index));
    }

    public static YamlNodeIndexedValueList sortAndCreate(YamlNodeAbstract<?> node) {
        Collections.sort(node._children, new Comparator<YamlEntry>(){

            @Override
            public int compare(YamlEntry e1, YamlEntry e2) {
                String n1 = e1.getKey();
                String n2 = e2.getKey();
                if (ParseUtil.isNumeric(n1) && ParseUtil.isNumeric(n2)) {
                    try {
                        int num1 = Integer.parseInt(n1);
                        int num2 = Integer.parseInt(n2);
                        return Integer.compare(num1, num2);
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                return n1.compareTo(n2);
            }
        });
        boolean changed = false;
        for (int i = 0; i < node._children.size(); ++i) {
            YamlEntry entry = node._children.get(i);
            entry.checkNotDisposed();
            changed |= entry.yaml.setIndex(i);
        }
        if (changed) {
            node._entry.callChangeListeners();
        }
        return new YamlNodeIndexedValueList(node);
    }
}

