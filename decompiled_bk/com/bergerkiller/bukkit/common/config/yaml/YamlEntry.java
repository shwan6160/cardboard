/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.StringTreeNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener;
import com.bergerkiller.bukkit.common.config.yaml.YamlListNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeIndexedValueList;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeLinkedValue;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.config.yaml.YamlRoot;
import com.bergerkiller.bukkit.common.config.yaml.YamlSerializer;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;

public class YamlEntry
implements Map.Entry<String, Object>,
YamlPath.Supplier {
    private static final YamlChangeListener[] NO_LISTENERS = new YamlChangeListener[0];
    private final YamlNodeAbstract<?> parent;
    private YamlPath path;
    protected final StringTreeNode yaml;
    protected boolean yaml_needs_generating;
    protected boolean yaml_check_children;
    protected boolean disposed = false;
    private String header;
    protected Object value;
    protected YamlChangeListener[] listeners;
    protected YamlChangeListener[] all_listeners;

    protected YamlEntry(YamlNodeAbstract<?> rootNode) {
        this.parent = null;
        this.path = YamlPath.ROOT;
        this.header = "";
        this.value = rootNode;
        this.yaml = new StringTreeNode();
        this.yaml_needs_generating = true;
        this.yaml_check_children = false;
        this.listeners = NO_LISTENERS;
        this.all_listeners = NO_LISTENERS;
    }

    protected YamlEntry(YamlNodeAbstract<?> parent, YamlPath path, StringTreeNode yaml) {
        this.parent = parent;
        this.path = path;
        this.header = "";
        this.value = null;
        this.yaml = yaml;
        this.yaml_needs_generating = false;
        this.yaml_check_children = false;
        this.listeners = NO_LISTENERS;
        this.all_listeners = parent == null ? NO_LISTENERS : parent._entry.all_listeners;
        this.markYamlChanged();
    }

    protected void checkNotDisposed() {
        if (this.disposed) {
            throw new IllegalStateException("This YamlEntry [" + this.path + "] is disposed. Why is it still referenced?");
        }
    }

    @Override
    public YamlPath getYamlPath() {
        return this.path;
    }

    public void setPath(YamlPath path) {
        if (this.parent != null) {
            this.setPath(this.parent._root, path);
        } else if (this.isAbstractNode()) {
            this.setPath(this.getAbstractNode()._root, path);
        } else {
            this.path = path;
        }
    }

    private void setPath(YamlRoot root, YamlPath path) {
        root.updateEntryPath(this, path);
        this.path = path;
        if (this.isAbstractNode()) {
            for (YamlEntry childEntry : this.getAbstractNode()._children) {
                childEntry.setPath(root, path.child(childEntry.path.name()));
            }
        }
    }

    public YamlNodeAbstract<?> getParentNode() {
        return this.parent;
    }

    public void addChangeListener(YamlChangeListener listener) {
        int length = this.listeners.length;
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        if (length == 0) {
            this.listeners = new YamlChangeListener[]{listener};
            this.recalculateListeners();
        } else {
            this.listeners = Arrays.copyOf(this.listeners, length + 1);
            this.listeners[length] = listener;
            this.recalculateListeners();
        }
    }

    public void addChangeListeners(YamlChangeListener[] listeners) {
        if (listeners != NO_LISTENERS) {
            for (YamlChangeListener listener : listeners) {
                this.addChangeListener(listener);
            }
        }
    }

    public boolean removeChangeListener(YamlChangeListener listener) {
        int length = this.listeners.length;
        if (length == 0) {
            return false;
        }
        if (length > 1) {
            for (int i = 0; i < length; ++i) {
                if (!this.listeners[i].equals(listener)) continue;
                this.listeners = LogicUtil.removeArrayElement(this.listeners, i);
                this.recalculateListeners();
                return true;
            }
            return false;
        }
        if (this.listeners[0].equals(listener)) {
            this.clearChangeListeners();
            return true;
        }
        return false;
    }

    public void clearChangeListeners() {
        this.listeners = NO_LISTENERS;
        this.recalculateListeners();
    }

    public StringTreeNode getYaml() {
        this.generateYaml();
        return this.yaml;
    }

    public YamlNodeAbstract<?> createNodeValue() {
        if (this.value instanceof YamlNodeAbstract && !(this.value instanceof YamlListNode)) {
            return (YamlNodeAbstract)this.value;
        }
        YamlNodeAbstract<?> closeParent = this.parent;
        while (closeParent instanceof YamlListNode) {
            closeParent = closeParent.getYamlParent();
        }
        Object newNode = closeParent != null ? closeParent.createNode(this) : new YamlNode(this);
        this.setValue(newNode);
        return newNode;
    }

    public YamlListNode createListNodeValue() {
        if (this.value instanceof YamlListNode) {
            return (YamlListNode)this.value;
        }
        YamlListNode newNode = new YamlListNode(this);
        this.setValue(newNode);
        return newNode;
    }

    public List<Object> createList() {
        Object value = this.value;
        if (value instanceof YamlListNode) {
            return (YamlListNode)value;
        }
        if (value instanceof YamlNodeAbstract) {
            return YamlNodeIndexedValueList.sortAndCreate((YamlNodeAbstract)value);
        }
        return this.createListNodeValue();
    }

    public boolean isAbstractNode() {
        return this.value instanceof YamlNodeAbstract;
    }

    public boolean isNode() {
        return this.value instanceof YamlNodeAbstract && !(this.value instanceof YamlListNode);
    }

    public YamlNodeAbstract<?> getAbstractNode() {
        return (YamlNodeAbstract)this.value;
    }

    @Override
    public String getKey() {
        return this.path.name();
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public Object setValue(Object value) {
        this.checkNotDisposed();
        oldValue = this.value;
        if (oldValue == value) {
            return oldValue;
        }
        if (value == null) {
            this.removeNodeValue();
            this.value = null;
            this.markYamlChanged();
            if (this.parent != null) {
                this.parent._entry.callChangeListeners();
            }
            return oldValue;
        }
        if (!(oldValue instanceof YamlNodeAbstract) && value.equals(oldValue)) {
            this.value = value;
            return oldValue;
        }
        if (value instanceof YamlNodeLinkedValue) {
            ((YamlNodeLinkedValue)value).assignTo(this);
            return oldValue;
        }
        if (!(value instanceof YamlNodeAbstract)) {
            if (value instanceof Collection) {
                targetList = this.createList();
                collection = (Collection)value;
                iter = collection.iterator();
                len = collection.size();
                for (i = 0; i < len; ++i) {
                    if (i < targetList.size()) {
                        targetList.set(i, iter.next());
                        continue;
                    }
                    targetList.add(iter.next());
                }
                for (i = targetList.size() - 1; i >= len; --i) {
                    targetList.remove(i);
                }
                return oldValue;
            }
            if (value.getClass().isArray()) {
                targetList = this.createList();
                len = Array.getLength(value);
                for (i = 0; i < len; ++i) {
                    if (i < targetList.size()) {
                        targetList.set(i, Array.get(value, i));
                        continue;
                    }
                    targetList.add(Array.get(value, i));
                }
                for (i = targetList.size() - 1; i >= len; --i) {
                    targetList.remove(i);
                }
                return oldValue;
            }
            if (value instanceof Map) {
                node = this.createNodeValue();
                node.clear();
                node.setValues((Map)value);
                return oldValue;
            }
        }
        listenersToSet = YamlEntry.NO_LISTENERS;
        if (!(value instanceof YamlNodeAbstract)) ** GOTO lbl-1000
        newNode = (YamlNodeAbstract)value;
        if (newNode._entry != this) {
            if (this.parent == null) {
                throw new IllegalArgumentException("Cannot assign a new node to a root node");
            }
            if (newNode.hasParent()) {
                index = newNode.getYamlParent()._children.indexOf(newNode._entry);
                if (index == -1) {
                    value = newNode = newNode.clone();
                } else {
                    newNode.getYamlParent().cloneChildEntry(index);
                }
            }
            this.removeNodeValue();
            this.assignProperties(newNode._entry);
            listenersToSet = newNode._entry.listeners;
            oldRoot = newNode._root;
            newNode._entry = this;
            newNode._root = this.parent._root;
            if (!newNode._children.isEmpty()) {
                iter = newNode._children.listIterator();
                while (iter.hasNext()) {
                    childEntry = iter.next();
                    newChildPath = this.path.child(childEntry.path.name());
                    oldRoot.removeEntry(childEntry);
                    newChildYaml = this.yaml.add();
                    iter.set(childEntry.copyToParent((YamlNodeAbstract<?>)newNode, newNode._root, newChildPath, newChildYaml, true));
                }
            }
        } else lbl-1000:
        // 2 sources

        {
            this.removeNodeValue();
        }
        this.value = value;
        this.markYamlChanged();
        this.callChangeListeners();
        this.addChangeListeners(listenersToSet);
        return oldValue;
    }

    private void removeNodeValue() {
        if (this.isAbstractNode()) {
            this.copyToParent(null, new YamlRoot(), YamlPath.ROOT, new StringTreeNode(), false);
        }
    }

    protected YamlEntry copyToParent(YamlNodeAbstract<?> newParent, YamlRoot newRoot, YamlPath newPath, StringTreeNode newYaml, boolean copyListeners) {
        YamlEntry newEntry = new YamlEntry(newParent, newPath, newYaml);
        newEntry.assignProperties(this);
        newEntry.value = this.value;
        newRoot.putEntry(newEntry);
        if (copyListeners) {
            newEntry.addChangeListeners(this.listeners);
        }
        if (this.isAbstractNode()) {
            YamlNodeAbstract<?> node = this.getAbstractNode();
            YamlRoot oldRoot = node._root;
            node._root = newRoot;
            node._entry = newEntry;
            ListIterator<YamlEntry> iter = node._children.listIterator();
            while (iter.hasNext()) {
                YamlEntry childEntry = iter.next();
                YamlPath newChildPath = newPath.child(childEntry.getKey());
                oldRoot.removeEntry(childEntry);
                StringTreeNode newChildYaml = newYaml.add();
                iter.set(childEntry.copyToParent(node, newRoot, newChildPath, newChildYaml, copyListeners));
            }
        }
        return newEntry;
    }

    protected void assignProperties(YamlEntry entry) {
        this.header = entry.header;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        if (!header.equals(this.header)) {
            this.header = header;
            this.markYamlChanged();
            this.callChangeListeners();
        }
    }

    public void addHeader(String header) {
        this.header = this.header.isEmpty() ? header : this.header + "\n" + header;
        this.markYamlChanged();
        this.callChangeListeners();
    }

    public void markYamlChanged() {
        if (!this.yaml_needs_generating) {
            this.yaml_needs_generating = true;
            for (YamlNodeAbstract<?> node = this.parent; node != null && !node._entry.yaml_check_children; node = node.getYamlParent()) {
                node._entry.yaml_check_children = true;
            }
        }
    }

    private String serializeYamlValue(Object value) throws YamlSerializer.SerializeException {
        int indent = this.path.depth();
        YamlEntry entry = this;
        while (entry != null && entry.parent._children.get(0) == entry && entry.parent.getYamlParent() instanceof YamlListNode) {
            value = Collections.singletonList(value);
            --indent;
            entry = entry.parent._entry;
        }
        return YamlSerializer.INSTANCE.serialize(value, this.header, indent);
    }

    private void generateYaml() {
        this.checkNotDisposed();
        if (this.yaml_check_children) {
            this.yaml_check_children = false;
            if (this.isAbstractNode()) {
                for (YamlEntry entry : this.getAbstractNode()._children) {
                    entry.generateYaml();
                }
            }
        }
        if (this.yaml_needs_generating) {
            YamlNodeAbstract<?> node;
            this.yaml_needs_generating = false;
            YamlNodeAbstract<?> yamlNodeAbstract = node = this.isAbstractNode() ? this.getAbstractNode() : null;
            if (this.parent == null) {
                if (this.header.isEmpty()) {
                    this.yaml.setValue("");
                } else {
                    StringBuilder builder = new StringBuilder();
                    YamlSerializer.INSTANCE.appendHeader(builder, this.header, 0);
                    this.yaml.setValue(builder.toString());
                }
            } else if (node != null && !node._children.isEmpty()) {
                if (this.parent instanceof YamlListNode) {
                    this.yaml.setValue("");
                } else {
                    boolean isFirstNodeListElement = this.parent.getYamlParent() instanceof YamlListNode && this == this.parent._children.get(0);
                    this.yaml.setValue(YamlSerializer.INSTANCE.serializeKey(this.path.name(), this.header, this.path.depth(), isFirstNodeListElement));
                }
            } else {
                Object value = this.value;
                if (node instanceof YamlListNode) {
                    value = Collections.emptyList();
                } else if (node instanceof YamlNodeAbstract) {
                    value = Collections.emptyMap();
                } else if (value instanceof Enum) {
                    String text = value.toString();
                    value = text.equalsIgnoreCase("true") ? Boolean.TRUE : (text.equalsIgnoreCase("false") ? Boolean.FALSE : text);
                } else if (value instanceof CommonEntityType) {
                    value = ((CommonEntityType)value).entityType.name();
                }
                value = this.parent instanceof YamlListNode ? Collections.singletonList(value) : Collections.singletonMap(this.getYamlPath().name(), value);
                try {
                    this.yaml.setValue(this.serializeYamlValue(value));
                }
                catch (YamlSerializer.SerializeException ex) {
                    Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to serialize YAML value stored at \"" + this.path + "\"", ex);
                    this.yaml.setValue("");
                }
            }
        }
    }

    protected void callChangeListeners() {
        if (this.all_listeners != NO_LISTENERS) {
            for (YamlChangeListener listener : this.all_listeners) {
                try {
                    listener.onNodeChanged(this.path);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to fire Yaml change callback", t);
                }
            }
        }
    }

    private void recalculateListeners() {
        if (this.parent == null || this.parent._entry.all_listeners == NO_LISTENERS) {
            if (this.all_listeners == this.listeners) {
                return;
            }
            this.all_listeners = this.listeners;
        } else if (this.listeners == NO_LISTENERS) {
            if (this.all_listeners == this.parent._entry.all_listeners) {
                return;
            }
            this.all_listeners = this.parent._entry.all_listeners;
        } else {
            int numParent = this.parent._entry.all_listeners.length;
            int numSelf = this.listeners.length;
            this.all_listeners = new YamlChangeListener[numParent + numSelf];
            System.arraycopy(this.parent._entry.all_listeners, 0, this.all_listeners, 0, numParent);
            System.arraycopy(this.listeners, 0, this.all_listeners, numParent, numSelf);
        }
        if (this.isAbstractNode()) {
            for (YamlEntry entry : this.getAbstractNode()._children) {
                entry.recalculateListeners();
            }
        }
    }
}

