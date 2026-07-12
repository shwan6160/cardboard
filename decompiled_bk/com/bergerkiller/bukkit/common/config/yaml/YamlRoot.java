/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.StringTreeNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import java.util.HashMap;
import java.util.Map;

public class YamlRoot {
    private final Map<YamlPath, YamlEntry> _entries = new HashMap<YamlPath, YamlEntry>();

    public void clear() {
        this._entries.clear();
    }

    public void detach(YamlEntry entry) {
        this.removeEntry(entry);
        if (entry.isAbstractNode()) {
            entry.copyToParent(null, new YamlRoot(), YamlPath.ROOT, new StringTreeNode(), true);
        }
    }

    public void updateEntryPath(YamlEntry entry, YamlPath newPath) {
        YamlEntry removed = this._entries.remove(entry.getYamlPath());
        if (removed != entry && removed != null) {
            this._entries.put(entry.getYamlPath(), removed);
        }
        this._entries.put(newPath, entry);
    }

    protected void removeEntry(YamlEntry entry) {
        if (entry.disposed) {
            return;
        }
        entry.yaml.remove();
        this._entries.remove(entry.getYamlPath());
        entry.disposed = true;
    }

    public void putEntry(YamlEntry entry) {
        this._entries.put(entry.getYamlPath(), entry);
    }

    public YamlEntry createEntryIfAbsent(YamlPath parent, String path) {
        return this.createEntryIfAbsent(parent.child(path));
    }

    public YamlEntry createEntryIfAbsent(YamlPath parent, YamlPath relativePath) {
        return this.createEntryIfAbsent(parent.child(relativePath));
    }

    public YamlEntry createEntryIfAbsent(YamlPath path) {
        return this._entries.containsKey(path) ? null : this.createEntry(path, false);
    }

    public YamlEntry getEntryIfExists(YamlPath parent, String path) {
        return this._entries.get(parent.child(path));
    }

    public YamlEntry getEntryIfExists(YamlPath parent, YamlPath relativePath) {
        return this._entries.get(YamlPath.join(parent, relativePath));
    }

    public YamlEntry getEntryIfExists(YamlPath path) {
        return this._entries.get(path);
    }

    public YamlEntry getEntry(YamlPath parent, String path) {
        return this.getEntry(parent.child(path));
    }

    public YamlEntry getEntry(YamlPath parent, YamlPath relativePath) {
        return this.getEntry(YamlPath.join(parent, relativePath));
    }

    public YamlEntry getEntry(YamlPath path) {
        return this.getEntry(path, false);
    }

    private YamlEntry getEntry(YamlPath path, boolean isListNode) {
        YamlEntry entry = this._entries.get(path);
        return entry != null ? entry : this.createEntry(path, isListNode);
    }

    private YamlEntry createEntry(YamlPath path, boolean isListNode) {
        YamlEntry parentEntry = this.getEntry(path.parent(), path.isListElement());
        YamlNodeAbstract parentNode = isListNode ? parentEntry.createListNodeValue() : parentEntry.createNodeValue();
        YamlEntry newEntry = parentNode.createChildEntry(parentNode._children.size(), path);
        parentEntry.callChangeListeners();
        return newEntry;
    }
}

