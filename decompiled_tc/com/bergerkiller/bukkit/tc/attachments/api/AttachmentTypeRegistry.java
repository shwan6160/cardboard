/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AttachmentTypeRegistry {
    private final Map<String, AttachmentType> _types = new StringMapCaseInsensitive();
    private static final AttachmentTypeRegistry _instance = new AttachmentTypeRegistry();

    public static AttachmentTypeRegistry instance() {
        return _instance;
    }

    public synchronized List<AttachmentType> all() {
        ArrayList<AttachmentType> result = new ArrayList<AttachmentType>(this._types.values());
        Collections.sort(result, (t1, t2) -> {
            int comp = Double.compare(t1.getSortPriority(), t2.getSortPriority());
            if (comp != 0) {
                return comp;
            }
            return t1.getName().compareTo(t2.getName());
        });
        return result;
    }

    public synchronized AttachmentType fromConfig(ConfigurationNode config) {
        return this.find((String)config.get("type", (Object)"EMPTY"));
    }

    public void toConfig(ConfigurationNode config, AttachmentType type) {
        config.set("type", (Object)type.getID());
    }

    public void toDefaultConfig(ConfigurationNode config, AttachmentType type) {
        this.toConfig(config, type);
        type.getDefaultConfig(config);
    }

    public synchronized AttachmentType find(String id) {
        return this._types.get(id);
    }

    public synchronized AttachmentType findOrEmpty(String id) {
        return this._types.getOrDefault(id, CartAttachmentEmpty.TYPE);
    }

    public synchronized void register(AttachmentType type) {
        this._types.put(type.getID(), type);
        type.onRegister(this);
    }

    public synchronized void unregister(AttachmentType type) {
        AttachmentType removed = this._types.remove(type.getID());
        if (removed != null) {
            if (removed != type) {
                this._types.put(removed.getID(), removed);
            } else {
                removed.onUnregister(this);
            }
        }
    }

    public synchronized void unregisterAll() {
        ArrayList<AttachmentType> removed = new ArrayList<AttachmentType>(this._types.values());
        this._types.clear();
        for (AttachmentType removedType : removed) {
            removedType.onUnregister(this);
        }
    }
}

