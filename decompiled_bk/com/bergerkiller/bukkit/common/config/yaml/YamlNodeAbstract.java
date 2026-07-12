/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.yaml.snakeyaml.error.YAMLException
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener;
import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListenerRelative;
import com.bergerkiller.bukkit.common.config.yaml.YamlDeserializer;
import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlListNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeDeepKeySetProxy;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeKeySetProxy;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeLazyCreateValueList;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeMapProxy;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeMappedIterator;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.config.yaml.YamlRoot;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.io.AsyncTextWriter;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.error.YAMLException;

public abstract class YamlNodeAbstract<N extends YamlNodeAbstract<?>>
implements YamlPath.Supplier,
Cloneable {
    protected YamlRoot _root;
    protected YamlEntry _entry;
    protected List<YamlEntry> _children = new ArrayList<YamlEntry>(10);

    public YamlNodeAbstract() {
        this(null);
    }

    protected YamlNodeAbstract(YamlEntry entry) {
        if (entry == null) {
            this._root = new YamlRoot();
            this._entry = new YamlEntry(this);
            this._root.putEntry(this._entry);
        } else {
            this._root = entry.getParentNode()._root;
            this._entry = entry;
        }
    }

    protected abstract N createNode(YamlEntry var1);

    public boolean hasParent() {
        return this.getYamlParent() != null;
    }

    public boolean isEmpty() {
        return this._children.isEmpty();
    }

    public N getParent() {
        return (N)this._entry.getParentNode();
    }

    public final YamlNodeAbstract<?> getYamlParent() {
        return this._entry.getParentNode();
    }

    @Override
    public YamlPath getYamlPath() {
        return this._entry.getYamlPath();
    }

    public String getName() {
        return this.getYamlPath().name();
    }

    public String getPath() {
        return this.getYamlPath().toString();
    }

    @Deprecated
    public String getPath(String append) {
        return this.getYamlPath().child(append).toString();
    }

    public void addChangeListener(YamlChangeListener listener) {
        listener = YamlChangeListenerRelative.create(this, listener);
        this._entry.addChangeListener(listener);
    }

    public boolean removeChangeListener(YamlChangeListener listener) {
        listener = YamlChangeListenerRelative.create(this, listener);
        return this._entry.removeChangeListener(listener);
    }

    public void clearChangeListeners() {
        this._entry.clearChangeListeners();
    }

    public boolean addChangeListener(String path, YamlChangeListener listener) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null) {
            listener = entry.isAbstractNode() ? YamlChangeListenerRelative.create(entry.getAbstractNode(), listener) : YamlChangeListenerRelative.create(entry, listener);
            entry.addChangeListener(listener);
            return true;
        }
        return false;
    }

    public boolean removeChangeListener(String path, YamlChangeListener listener) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null) {
            listener = entry.isAbstractNode() ? YamlChangeListenerRelative.create(entry.getAbstractNode(), listener) : YamlChangeListenerRelative.create(entry, listener);
            return entry.removeChangeListener(listener);
        }
        return false;
    }

    public boolean clearChangeListeners(String path) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null) {
            entry.clearChangeListeners();
            return true;
        }
        return false;
    }

    public String getHeader() {
        return this._entry.getHeader();
    }

    public void setHeader(String header) {
        this._entry.setHeader(header);
    }

    public void addHeader(String header) {
        this._entry.addHeader(header);
    }

    public void removeHeader() {
        this.setHeader("");
    }

    public String getHeader(String path) {
        return this.getEntry(path).getHeader();
    }

    public void setHeader(String path, String header) {
        this.getEntry(path).setHeader(header);
    }

    public void addHeader(String path, String header) {
        this.getEntry(path).addHeader(header);
    }

    public void removeHeader(String path) {
        this.setHeader(path, "");
    }

    public void clearHeaders() {
        this._entry.setHeader("");
        for (YamlNodeAbstract childNode : this.getNodes()) {
            childNode.clearHeaders();
        }
    }

    public boolean contains(String path) {
        return this.getEntryIfExists(path) != null;
    }

    public boolean contains(YamlPath relativePath) {
        return this.getEntryIfExists(relativePath) != null;
    }

    public Set<String> getKeys() {
        return YamlNodeKeySetProxy.stringKeysOf(this);
    }

    public Set<YamlPath> getYamlKeys() {
        return YamlNodeKeySetProxy.yamlPathKeysOf(this);
    }

    public Set<YamlPath> getDeepYamlKeys() {
        return YamlNodeDeepKeySetProxy.yamlPathKeysOf(this);
    }

    public Map<String, Object> getValues() {
        return new YamlNodeMapProxy(this);
    }

    public void setValues(Map<?, ?> values) {
        this.clear();
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            this.set(entry.getKey().toString(), entry.getValue());
        }
    }

    public <T> Map<String, T> getValues(Class<T> valueType) {
        Map<String, Object> values = this.getValues();
        YamlNodeMappedIterator<Object> iter = YamlNodeMappedIterator.shallow(this, YamlEntry::getValue);
        while (iter.hasNext()) {
            Object oldValue = iter.next();
            if (oldValue == null) continue;
            Object convertedValue = Conversion.convert(oldValue, valueType, null);
            if (convertedValue == null) {
                iter.remove();
                continue;
            }
            if (convertedValue == oldValue) continue;
            iter.setValue(convertedValue);
        }
        return values;
    }

    public void clear() {
        int num_children = this._children.size();
        if (num_children > 0) {
            do {
                this.removeChildEntryAtWithoutEventAndGetValue(--num_children);
            } while (num_children > 0);
            this._entry.callChangeListeners();
        }
    }

    public Set<N> getNodes() {
        LinkedHashSet<YamlNodeAbstract> result = new LinkedHashSet<YamlNodeAbstract>(this._children.size());
        for (YamlEntry child : this._children) {
            if (!child.isNode()) continue;
            result.add((YamlNodeAbstract)child.getValue());
        }
        return result;
    }

    public boolean isNode(String path) {
        Object value;
        YamlEntry entryAtPath = this.getEntryIfExists(path);
        return entryAtPath != null && (value = entryAtPath.getValue()) instanceof YamlNodeAbstract && !(value instanceof YamlListNode);
    }

    public boolean isNode(YamlPath relativePath) {
        Object value;
        YamlEntry entryAtPath = this.getEntryIfExists(relativePath);
        return entryAtPath != null && (value = entryAtPath.getValue()) instanceof YamlNodeAbstract && !(value instanceof YamlListNode);
    }

    public N getNode(String path) {
        return (N)this.getEntry(path).createNodeValue();
    }

    public N getNode(YamlPath relativePath) {
        return (N)this.getEntry(relativePath).createNodeValue();
    }

    public N getNodeIfExists(String path) {
        YamlEntry entry = this.getEntryIfExists(path);
        return (N)(entry != null && entry.isNode() ? (YamlNodeAbstract)entry.value : null);
    }

    public N getNodeIfExists(YamlPath relativePath) {
        YamlEntry entry = this.getEntryIfExists(relativePath);
        return (N)(entry != null && entry.isNode() ? (YamlNodeAbstract)entry.value : null);
    }

    public void setNodeList(String path, List<N> nodes) {
        YamlEntry entry = this.getEntry(path);
        if (!entry.isAbstractNode()) {
            entry.createNodeValue();
        }
        entry.setValue(nodes);
    }

    public List<N> getNodeList(String path) {
        return this.getNodeList(path, true);
    }

    public List<N> getNodeList(String path, boolean createIndexed) {
        List<Object> list = this.getList(path, createIndexed);
        for (int i = 0; i < list.size(); ++i) {
            Object value = list.get(i);
            if (value instanceof YamlNodeAbstract && !(value instanceof YamlListNode)) continue;
            list.remove(i--);
        }
        return list;
    }

    public List<Object> getList(String path) {
        return this.getList(path, false);
    }

    private List<Object> getList(String path, boolean createIndexed) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null) {
            return entry.createList();
        }
        return new YamlNodeLazyCreateValueList(this, path, createIndexed);
    }

    public <T> List<T> getList(String path, Class<T> valueType) {
        List<Object> list = this.getList(path);
        for (int i = 0; i < list.size(); ++i) {
            Object oldValue = list.get(i);
            if (oldValue == null) continue;
            Object convertedValue = Conversion.convert(oldValue, valueType, null);
            if (convertedValue == null) {
                list.remove(i--);
                continue;
            }
            if (convertedValue == oldValue) continue;
            list.set(i, convertedValue);
        }
        return list;
    }

    public <T> List<T> getList(String path, Class<T> type, List<T> def) {
        if (!this.contains(path)) {
            this.set(path, def);
        }
        return this.getList(path, type);
    }

    public Object get(String path) {
        YamlEntry entry = this.getEntryIfExists(path);
        return entry == null ? null : entry.getValue();
    }

    public Object get(YamlPath relativePath) {
        YamlEntry entry = this.getEntryIfExists(relativePath);
        return entry == null ? null : entry.getValue();
    }

    public <T> T get(String path, Class<T> type) {
        return this.get(path, type, null);
    }

    public <T> T get(YamlPath relativePath, Class<T> type) {
        return this.get(relativePath, type, null);
    }

    public <T> T get(String path, T def) {
        return (T)this.get(path, def.getClass(), def);
    }

    public <T> T get(YamlPath relativePath, T def) {
        return (T)this.get(relativePath, def.getClass(), def);
    }

    public <T> T get(String path, Class<T> type, T def) {
        Object value = this.get(path);
        if (type == String.class && value instanceof String[]) {
            return (T)StringUtil.join("\n", (String[])value);
        }
        T rval = ParseUtil.convert(value, type, null);
        if (rval == null) {
            rval = def;
            this.set(path, rval);
        }
        return rval;
    }

    public <T> T get(YamlPath relativePath, Class<T> type, T def) {
        Object value = this.get(relativePath);
        if (type == String.class && value instanceof String[]) {
            return (T)StringUtil.join("\n", (String[])value);
        }
        T rval = ParseUtil.convert(value, type, null);
        if (rval == null) {
            rval = def;
            this.set(relativePath, rval);
        }
        return rval;
    }

    public <T> T getOrDefault(String path, T def) {
        return (T)this.getOrDefault(path, def.getClass(), def);
    }

    public <T> T getOrDefault(YamlPath relativePath, T def) {
        return (T)this.getOrDefault(relativePath, def.getClass(), def);
    }

    public <T> T getOrDefault(String path, Class<T> type, T def) {
        Object value = this.get(path);
        if (type == String.class && value instanceof String[]) {
            return (T)StringUtil.join("\n", (String[])value);
        }
        T rval = ParseUtil.convert(value, type, null);
        return rval != null ? rval : (T)def;
    }

    public <T> T getOrDefault(YamlPath relativePath, Class<T> type, T def) {
        Object value = this.get(relativePath);
        if (type == String.class && value instanceof String[]) {
            return (T)StringUtil.join("\n", (String[])value);
        }
        T rval = ParseUtil.convert(value, type, null);
        return rval != null ? rval : (T)def;
    }

    public void set(String path, Object value) {
        this.getEntry(path).setValue(value);
    }

    public void set(YamlPath relativePath, Object value) {
        this.getEntry(relativePath).setValue(value);
    }

    public boolean setIfAbsent(String path, Object value) {
        YamlEntry entry = this.createEntryIfAbsent(path);
        if (entry != null) {
            entry.setValue(value);
            return true;
        }
        return false;
    }

    public boolean setIfAbsent(YamlPath relativePath, Object value) {
        YamlEntry entry = this.createEntryIfAbsent(relativePath);
        if (entry != null) {
            entry.setValue(value);
            return true;
        }
        return false;
    }

    public void remove(String path) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null && entry.getParentNode() != null) {
            entry.getParentNode().removeChildEntry(entry);
        }
    }

    public void remove(YamlPath relativePath) {
        YamlEntry entry = this.getEntryIfExists(relativePath);
        if (entry != null && entry.getParentNode() != null) {
            entry.getParentNode().removeChildEntry(entry);
        }
    }

    public void remove() {
        YamlNodeAbstract<?> parent = this._entry.getParentNode();
        if (parent != null) {
            parent.removeChildEntry(this._entry);
        }
    }

    public void shareWith(N target, String path, Object def) {
        YamlEntry entry_a = this.getEntry(path);
        YamlEntry entry_b = ((YamlNodeAbstract)target).getEntry(path);
        if (entry_a.getValue() != null) {
            entry_b.setValue(entry_a.getValue());
        } else if (entry_b.getValue() != null) {
            entry_a.setValue(entry_b.getValue());
        } else {
            entry_a.setValue(def);
            entry_b.setValue(def);
        }
    }

    public void shareWithMap(Map<String, Object> target, String path, Object def) {
        YamlEntry entry_a = this.getEntry(path);
        if (entry_a.getValue() != null) {
            target.put(path, entry_a.getValue());
        } else {
            Object value = target.get(path);
            if (value == null) {
                value = def;
                target.put(path, def);
            }
            entry_a.setValue(value);
        }
    }

    public void loadFromStream(InputStream stream) throws YAMLException {
        this.loadFromReader(new InputStreamReader((InputStream)new BufferedInputStream(stream), StandardCharsets.UTF_8));
    }

    public void loadFromReader(Reader reader) throws YAMLException {
        this.loadDeserializerOutput(YamlDeserializer.INSTANCE.deserialize(reader));
    }

    public void loadFromString(String yamlString) throws YAMLException {
        this.loadDeserializerOutput(YamlDeserializer.INSTANCE.deserialize(yamlString));
    }

    public void loadDeserializerOutput(YamlDeserializer.Output output) {
        this.setValues(output.root);
        for (Map.Entry<YamlPath, String> header : output.headers.entrySet()) {
            YamlEntry entry = this._root.getEntryIfExists(this.getYamlPath().child(header.getKey()));
            if (entry == null) continue;
            entry.setHeader(header.getValue());
        }
    }

    public void saveToStream(OutputStream stream) throws IOException {
        this.saveToWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
    }

    public void saveToWriter(Writer writer) throws IOException {
        try {
            writer.write(this.toString());
        }
        finally {
            try {
                writer.close();
            }
            catch (IOException iOException) {}
        }
    }

    public CompletableFuture<Void> saveToFileAsync(File file) {
        return AsyncTextWriter.writeSafe(file, this.toCharBuffer());
    }

    public N clone() {
        N clone = this.createNode(null);
        ((YamlNodeAbstract)clone)._entry.assignProperties(this._entry);
        this.cloneChildrenTo((YamlNodeAbstract<?>)clone, null, null, false);
        return clone;
    }

    public void setTo(N source) {
        ((YamlNodeAbstract)source).cloneChildrenTo(this, null, null, true);
    }

    public void setTo(N source, Predicate<YamlPath> filter) {
        ((YamlNodeAbstract)source).cloneChildrenTo(this, YamlPath.ROOT, filter, true);
    }

    public void setToExcept(N source, Collection<String> excludedPaths) {
        Set pathsToExclude = excludedPaths.stream().map(YamlPath::create).collect(Collectors.toSet());
        this.setTo(source, path -> !pathsToExclude.contains(path));
    }

    public void cloneInto(N target, Predicate<YamlPath> filter) {
        this.cloneChildrenTo((YamlNodeAbstract<?>)target, YamlPath.ROOT, filter, false);
    }

    public void cloneInto(N target) {
        this.cloneChildrenTo((YamlNodeAbstract<?>)target, null, null, false);
    }

    public void cloneIntoExcept(N target, Collection<String> excludedPaths) {
        Set pathsToExclude = excludedPaths.stream().map(YamlPath::create).collect(Collectors.toSet());
        this.cloneInto(target, path -> !pathsToExclude.contains(path));
    }

    protected void cloneChildrenTo(YamlNodeAbstract<?> clone, YamlPath filterRoot, Predicate<YamlPath> filter, boolean removeOthers) {
        if (this == clone) {
            return;
        }
        if (this._root == clone._root) {
            ((YamlNodeAbstract)this.clone()).cloneChildrenTo(clone, filterRoot, filter, removeOthers);
            return;
        }
        boolean isCloneEmpty = clone.isEmpty();
        if (isCloneEmpty) {
            removeOthers = false;
        }
        DelayedRemovalOperation removalOp = !removeOthers ? DelayedRemovalOperation.NONE : (clone instanceof YamlListNode ? new DelayedRemovalOperationList() : new DelayedRemovalOperationNode(clone));
        boolean parentChanged = false;
        for (YamlEntry child : this._children) {
            YamlEntry childClone;
            YamlPath filterPath;
            if (filterRoot == null) {
                filterPath = null;
            } else {
                filterPath = filterRoot.childWithName(child.getYamlPath());
                if (!filter.test(filterPath)) continue;
            }
            removalOp.childOverwritten(child);
            YamlPath childPath = clone.getYamlPath().childWithName(child.getYamlPath());
            boolean isNewNode = false;
            if (isCloneEmpty || (childClone = clone._root.getEntryIfExists(childPath)) == null) {
                childClone = clone.createChildEntry(clone._children.size(), childPath);
                isNewNode = true;
                parentChanged = true;
            }
            childClone.assignProperties(child);
            if (child.isAbstractNode()) {
                YamlNodeAbstract<YamlListNode> originalChildNode = child.getAbstractNode();
                YamlNodeAbstract childCloneNode = originalChildNode instanceof YamlListNode ? childClone.createListNodeValue() : childClone.createNodeValue();
                try {
                    originalChildNode.cloneChildrenTo(childCloneNode, filterPath, filter, removeOthers);
                    continue;
                }
                catch (StackOverflowError err) {
                    throw new IllegalStateException("YAML tree too deep or infinite recursion", err);
                }
            }
            if (isNewNode) {
                childClone.value = child.value;
                childClone.callChangeListeners();
                continue;
            }
            childClone.setValue(child.value);
        }
        if (parentChanged |= removalOp.remove(clone, filterRoot, filter)) {
            clone._entry.callChangeListeners();
        }
    }

    protected YamlEntry cloneChildEntry(int index) {
        YamlEntry oldEntry = this._children.get(index);
        oldEntry.checkNotDisposed();
        YamlEntry newEntry = new YamlEntry(this, oldEntry.getYamlPath(), oldEntry.yaml);
        newEntry.assignProperties(oldEntry);
        newEntry.yaml_check_children = oldEntry.yaml_check_children;
        newEntry.yaml_needs_generating = oldEntry.yaml_needs_generating;
        newEntry.listeners = oldEntry.listeners;
        newEntry.all_listeners = oldEntry.all_listeners;
        oldEntry.disposed = true;
        this._children.set(index, newEntry);
        this._root.putEntry(newEntry);
        if (oldEntry.isAbstractNode()) {
            YamlNodeAbstract<?> oldNode = oldEntry.getAbstractNode();
            Object newNode = oldNode.createNode(newEntry);
            ((YamlNodeAbstract)newNode)._children.addAll(oldNode._children);
            for (int i = 0; i < ((YamlNodeAbstract)newNode)._children.size(); ++i) {
                ((YamlNodeAbstract)newNode).cloneChildEntry(i);
            }
            newEntry.value = newNode;
        } else {
            newEntry.value = oldEntry.value;
        }
        return newEntry;
    }

    public CharBuffer toCharBuffer() {
        if (this.getYamlPath().depth() == 0) {
            return this._entry.getYaml().toCharBuffer();
        }
        return CharBuffer.wrap(this.toString());
    }

    public String toString() {
        int depth = this.getYamlPath().depth();
        if (depth == 0) {
            return this._entry.getYaml().toString();
        }
        StringBuilder yaml = new StringBuilder();
        int indent = 2 * depth;
        for (YamlEntry child : this._children) {
            String childYaml = child.getYaml().toString();
            int lineStart = 0;
            for (int i = 0; i < childYaml.length(); ++i) {
                char c = childYaml.charAt(i);
                if (c == '\n') {
                    yaml.append(c);
                    lineStart = i + 1;
                    continue;
                }
                if (i - lineStart < indent) continue;
                yaml.append(c);
            }
        }
        return yaml.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof YamlNodeAbstract) {
            return YamlNodeAbstract.isSameConfig(this, (YamlNodeAbstract)o);
        }
        return false;
    }

    private static boolean isSameConfig(YamlNodeAbstract<?> a, YamlNodeAbstract<?> b) {
        block7: {
            Map<String, Object> a_entries = a.getValues();
            Map<String, Object> b_entries = b.getValues();
            if (a_entries.size() != b_entries.size()) {
                return false;
            }
            Iterator<Map.Entry<String, Object>> a_iter = a_entries.entrySet().iterator();
            Iterator<Map.Entry<String, Object>> b_iter = b_entries.entrySet().iterator();
            while (true) {
                boolean has;
                if ((has = a_iter.hasNext()) != b_iter.hasNext()) {
                    return false;
                }
                if (!has) break block7;
                Map.Entry<String, Object> a_entry = a_iter.next();
                Map.Entry<String, Object> b_entry = b_iter.next();
                if (!a_entry.getKey().equals(b_entry.getKey())) {
                    return false;
                }
                Object a_value = a_entry.getValue();
                Object b_value = b_entry.getValue();
                if (a_value == null && b_value == null) continue;
                if (a_value == null || b_value == null) {
                    return false;
                }
                if (a_value instanceof YamlNodeAbstract && b_value instanceof YamlNodeAbstract) {
                    YamlNodeAbstract a_cfg = (YamlNodeAbstract)a_value;
                    YamlNodeAbstract b_cfg = (YamlNodeAbstract)b_value;
                    if (YamlNodeAbstract.isSameConfig(a_cfg, b_cfg)) continue;
                    return false;
                }
                if (a_value instanceof YamlNodeAbstract || b_value instanceof YamlNodeAbstract) {
                    return false;
                }
                if (!a_value.equals(b_value)) break;
            }
            return false;
        }
        return true;
    }

    protected YamlEntry createChildEntry(int index, YamlPath path) {
        this._entry.checkNotDisposed();
        if (!path.parent().equals(this._entry.getYamlPath())) {
            throw new IllegalArgumentException("Path " + path + " is not a child of " + this._entry.getYamlPath());
        }
        if (this._children.isEmpty()) {
            this._entry.markYamlChanged();
        }
        YamlEntry entry = new YamlEntry(this, path, this._entry.yaml.insert(index));
        this._children.add(index, entry);
        this._root.putEntry(entry);
        return entry;
    }

    protected int indexOfYamlPath(YamlPath path) {
        if (path != null && !this._children.isEmpty()) {
            for (int i = 0; i < this._children.size(); ++i) {
                if (!this._children.get(i).getYamlPath().equals(path)) continue;
                return i;
            }
        }
        return -1;
    }

    protected int indexOfKey(Object key) {
        if (key != null) {
            YamlPath path = this.getYamlPath().child(key.toString());
            return this.indexOfYamlPath(path);
        }
        return -1;
    }

    protected int indexOfValue(Object value) {
        if (value == null) {
            for (int i = 0; i < this._children.size(); ++i) {
                if (this._children.get(i).getValue() != null) continue;
                return i;
            }
        } else {
            for (int i = 0; i < this._children.size(); ++i) {
                if (!value.equals(this._children.get(i).getValue())) continue;
                return i;
            }
        }
        return -1;
    }

    protected int lastIndexOfValue(Object value) {
        if (value == null) {
            for (int i = this._children.size() - 1; i >= 0; --i) {
                if (this._children.get(i).getValue() != null) continue;
                return i;
            }
        } else {
            for (int i = this._children.size() - 1; i >= 0; --i) {
                if (!value.equals(this._children.get(i).getValue())) continue;
                return i;
            }
        }
        return -1;
    }

    protected YamlEntry createEntryIfAbsent(String path) {
        return this._root.createEntryIfAbsent(this._entry.getYamlPath(), path);
    }

    protected YamlEntry createEntryIfAbsent(YamlPath relativePath) {
        return this._root.createEntryIfAbsent(this._entry.getYamlPath(), relativePath);
    }

    protected YamlEntry getEntryIfExists(String path) {
        return this._root.getEntryIfExists(this._entry.getYamlPath(), path);
    }

    protected YamlEntry getEntryIfExists(YamlPath relativePath) {
        return this._root.getEntryIfExists(this._entry.getYamlPath(), relativePath);
    }

    protected YamlEntry getEntry(String path) {
        return this._root.getEntry(this._entry.getYamlPath(), path);
    }

    protected YamlEntry getEntry(YamlPath relativePath) {
        return this._root.getEntry(this._entry.getYamlPath(), relativePath);
    }

    protected void removeChildEntry(YamlEntry entry) {
        int index = this._children.indexOf(entry);
        if (index == -1) {
            throw new IllegalArgumentException("The entry is not a child of this node");
        }
        this.removeChildEntryAtAndGetValue(index);
    }

    protected Object removeChildEntryAtAndGetValue(int index) {
        Object removedValue = this.removeChildEntryAtWithoutEventAndGetValue(index);
        this._entry.callChangeListeners();
        return removedValue;
    }

    protected Object removeChildEntryAtWithoutEventAndGetValue(int index) {
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
        }
        YamlEntry entry = this._children.remove(index);
        this._root.detach(entry);
        if (this._children.isEmpty()) {
            this._entry.markYamlChanged();
        }
        return entry.getValue();
    }

    private static abstract class DelayedRemovalOperation {
        public static final DelayedRemovalOperation NONE = new DelayedRemovalOperation(){

            @Override
            public void childOverwritten(YamlEntry entry) {
            }

            @Override
            public boolean remove(YamlNodeAbstract<?> node, YamlPath filterRoot, Predicate<YamlPath> filter) {
                return false;
            }
        };

        private DelayedRemovalOperation() {
        }

        public abstract boolean remove(YamlNodeAbstract<?> var1, YamlPath var2, Predicate<YamlPath> var3);

        public abstract void childOverwritten(YamlEntry var1);
    }

    private static class DelayedRemovalOperationList
    extends DelayedRemovalOperation {
        private int startIndexToRemove = 0;

        @Override
        public void childOverwritten(YamlEntry entry) {
            ++this.startIndexToRemove;
        }

        @Override
        public boolean remove(YamlNodeAbstract<?> node, YamlPath filterRoot, Predicate<YamlPath> filter) {
            int startIdx = this.startIndexToRemove;
            boolean changed = false;
            while (node._children.size() > startIdx) {
                if (filterRoot != null && !filter.test(filterRoot.listChild(startIdx))) {
                    ++startIdx;
                    continue;
                }
                node.removeChildEntryAtWithoutEventAndGetValue(startIdx);
                changed = true;
            }
            return changed;
        }
    }

    private static class DelayedRemovalOperationNode
    extends DelayedRemovalOperation {
        private final Set<String> nodeNamesToRemove;

        public DelayedRemovalOperationNode(YamlNodeAbstract<?> node) {
            this.nodeNamesToRemove = node._children.stream().map(YamlEntry::getKey).collect(Collectors.toCollection(HashSet::new));
        }

        @Override
        public void childOverwritten(YamlEntry entry) {
            this.nodeNamesToRemove.remove(entry.getKey());
        }

        @Override
        public boolean remove(YamlNodeAbstract<?> node, YamlPath filterRoot, Predicate<YamlPath> filter) {
            boolean changed = false;
            for (String key : this.nodeNamesToRemove) {
                int index;
                YamlEntry childCloneToRemove;
                if (filterRoot != null && !filter.test(filterRoot.child(key)) || (childCloneToRemove = node.getEntryIfExists(key)) == null || (index = node._children.indexOf(childCloneToRemove)) == -1) continue;
                node.removeChildEntryAtWithoutEventAndGetValue(index);
                changed = true;
            }
            return changed;
        }
    }
}

