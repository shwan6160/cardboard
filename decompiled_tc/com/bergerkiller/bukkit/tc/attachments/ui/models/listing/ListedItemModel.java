/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  org.bukkit.ChatColor
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedEntry;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedNamespace;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRoot;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.ChatColor;

public final class ListedItemModel
extends ListedEntry {
    private static final boolean CAN_SHOW_ITEM_MODEL_LORE = Common.hasCapability((String)"Common:CommonItemStack:ItemModel");
    private final String fullPath;
    private final String path;
    private final String name;
    private final String nameLowerCase;
    private final String credit;
    private final CommonItemStack bareItem;
    private final CommonItemStack item;

    public ListedItemModel(String fullPath, String path, String name, String credit, CommonItemStack item) {
        this.nestedItemCount = 1;
        this.fullPath = fullPath;
        this.path = path;
        this.name = name;
        this.nameLowerCase = name.toLowerCase(Locale.ENGLISH);
        this.credit = credit;
        this.bareItem = item;
        this.item = item.clone();
        this.initializeItem();
    }

    private ListedItemModel(ListedItemModel itemModel) {
        this.fullPath = itemModel.fullPath;
        this.path = itemModel.path;
        this.name = itemModel.name;
        this.nameLowerCase = itemModel.nameLowerCase;
        this.credit = itemModel.credit;
        this.bareItem = itemModel.bareItem;
        this.item = itemModel.item;
    }

    private void initializeItem() {
        this.item.hideAllAttributes();
        String origItemName = this.item.getDisplayNameMessage();
        this.item.setCustomNameMessage(ChatColor.AQUA + this.name);
        this.item.addLoreMessage(ChatColor.WHITE.toString() + ChatColor.ITALIC + this.fullPath);
        this.item.addLoreLine();
        ListedItemModel.addLoreProperty(this.item, "Item", origItemName);
        if (CAN_SHOW_ITEM_MODEL_LORE) {
            ListedItemModel.showItemModelInfo(this.item);
        }
        if (this.item.hasCustomModelData()) {
            ListedItemModel.addLoreProperty(this.item, "Custom model data", this.item.getCustomModelData());
        }
        if (this.item.isDamageSupported() && this.item.getDamage() != 0) {
            ListedItemModel.addLoreProperty(this.item, "Damage", this.item.getDamage());
        }
        if (this.item.isUnbreakable()) {
            ListedItemModel.addLoreProperty(this.item, "Unbreakable", true);
        }
        if (!this.credit.isEmpty()) {
            this.item.addLoreLine();
            this.item.addLoreMessage(ChatColor.DARK_BLUE + this.credit);
        }
    }

    private static void addLoreProperty(CommonItemStack item, String name, Object value) {
        item.addLoreMessage(ChatColor.DARK_GRAY + name + ": " + ChatColor.GRAY + value);
    }

    private static void showItemModelInfo(CommonItemStack item) {
        if (item.hasItemModel()) {
            ListedItemModel.addLoreProperty(item, "Item Model", item.getItemModel().toString());
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String nameLowerCase() {
        return this.nameLowerCase;
    }

    public String path() {
        return this.path;
    }

    @Override
    public String fullPath() {
        return this.fullPath;
    }

    public String credit() {
        return this.credit;
    }

    @Override
    public int sortPriority() {
        return 3;
    }

    @Override
    public ListedNamespace namespace() {
        return this.parent().namespace();
    }

    public CommonItemStack item() {
        return this.item;
    }

    public CommonItemStack bareItem() {
        return this.bareItem;
    }

    @Override
    public CommonItemStack createIconItem(DialogBuilder options) {
        return this.item.clone();
    }

    @Override
    public List<ListedItemModel> explode() {
        return Collections.singletonList(this);
    }

    @Override
    protected void fillItems(List<ListedItemModel> items) {
        items.add(this);
    }

    public String toString() {
        return "Item name=" + this.name + " path=" + this.fullPath + " item=" + this.item;
    }

    @Override
    protected ListedEntry cloneSelf(ListedNamespace namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("Namespace is required");
        }
        return new ListedItemModel(this);
    }

    @Override
    protected ListedItemModel findOrCreateInRoot(ListedRoot root) {
        ListedEntry newParent = this.parent().findOrCreateInRoot(root);
        ListedItemModel entry = new ListedItemModel(this);
        entry.setParent(newParent);
        root.allListedItems.add(entry);
        return entry;
    }
}

