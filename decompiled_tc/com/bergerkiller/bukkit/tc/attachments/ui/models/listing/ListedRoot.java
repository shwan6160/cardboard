/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedEntry;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedItemModel;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedNamespace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ListedRoot
extends ListedEntry {
    final Map<String, ListedNamespace> namespacesByName;
    final List<ListedItemModel> allListedItems;
    final Map<CommonItemStack, ListedItemModel> allListedBareItemStacks;

    public ListedRoot() {
        this.namespacesByName = new HashMap<String, ListedNamespace>();
        this.allListedItems = new ArrayList<ListedItemModel>();
        this.allListedBareItemStacks = new LinkedHashMap<CommonItemStack, ListedItemModel>();
    }

    private ListedRoot(ListedRoot root) {
        this.namespacesByName = new HashMap<String, ListedNamespace>(root.namespacesByName.size());
        this.allListedItems = new ArrayList<ListedItemModel>(root.allListedItems.size());
        this.allListedBareItemStacks = new LinkedHashMap<CommonItemStack, ListedItemModel>(root.allListedBareItemStacks.size());
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public String nameLowerCase() {
        return "";
    }

    @Override
    public String fullPath() {
        return "";
    }

    @Override
    public int sortPriority() {
        return 0;
    }

    @Override
    public CommonItemStack createIconItem(DialogBuilder options) {
        return CommonItemStack.empty();
    }

    @Override
    public ListedNamespace namespace() {
        return null;
    }

    public List<ListedNamespace> namespaces() {
        return this.children();
    }

    public List<ListedItemModel> itemModels() {
        return this.allListedItems;
    }

    public Map<CommonItemStack, ListedItemModel> bareItemStacks() {
        return this.allListedBareItemStacks;
    }

    public String toString() {
        return "<ROOT>";
    }

    public ListedItemModel addListedItem(String path, CommonItemStack item, String credit) {
        String name;
        ListedEntry containingEntry;
        String fullPath;
        String pathWithoutNamespace;
        String namespaceName;
        int namespaceStart = path.indexOf(58);
        if (namespaceStart == -1) {
            namespaceName = "minecraft:";
            pathWithoutNamespace = path;
            fullPath = namespaceName + path;
        } else {
            namespaceName = path.substring(0, namespaceStart + 1);
            pathWithoutNamespace = path.substring(namespaceStart + 1);
            fullPath = path;
        }
        ListedNamespace namespace = this.findOrCreateNamespace(namespaceName);
        int directoryPathEnd = pathWithoutNamespace.lastIndexOf(47);
        if (directoryPathEnd == -1) {
            containingEntry = namespace;
            name = pathWithoutNamespace;
        } else {
            String directoryPath = pathWithoutNamespace.substring(0, directoryPathEnd);
            containingEntry = namespace.findOrCreateDirectory(directoryPath);
            name = pathWithoutNamespace.substring(directoryPathEnd + 1);
        }
        ListedItemModel entry = new ListedItemModel(fullPath, pathWithoutNamespace, name, credit, item);
        entry.setParent(containingEntry);
        this.allListedItems.add(entry);
        this.allListedBareItemStacks.put(entry.bareItem(), entry);
        return entry;
    }

    @Override
    protected ListedRoot cloneSelf(ListedNamespace namespace) {
        if (namespace != null) {
            throw new IllegalArgumentException("Root entries cannot be in a namespace");
        }
        return new ListedRoot(this);
    }

    @Override
    protected ListedEntry findOrCreateInRoot(ListedRoot root) {
        return root;
    }

    protected ListedNamespace findOrCreateNamespace(String namespace) {
        ListedNamespace entry = this.namespacesByName.computeIfAbsent(namespace, ListedNamespace::new);
        if (entry.parent() == null) {
            entry.setParent(this);
        }
        return entry;
    }
}

