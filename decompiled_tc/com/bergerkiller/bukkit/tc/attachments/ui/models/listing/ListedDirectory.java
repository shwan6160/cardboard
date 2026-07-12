/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  org.bukkit.ChatColor
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedEntry;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedNamespace;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRoot;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public final class ListedDirectory
extends ListedEntry {
    private final ListedNamespace namespace;
    private final String path;
    private final String name;
    private final String nameLowerCase;

    public ListedDirectory(ListedNamespace namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        int lastIdx = path.lastIndexOf(47);
        this.name = lastIdx == -1 ? path : path.substring(lastIdx + 1);
        this.nameLowerCase = this.name.toLowerCase(Locale.ENGLISH);
    }

    private ListedDirectory(ListedNamespace namespace, ListedDirectory directory) {
        this.namespace = namespace;
        this.path = directory.path;
        this.name = directory.name;
        this.nameLowerCase = directory.nameLowerCase;
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
        return this.namespace.fullPath() + this.path;
    }

    @Override
    public int sortPriority() {
        return 2;
    }

    @Override
    public ListedNamespace namespace() {
        return this.namespace;
    }

    @Override
    public CommonItemStack createIconItem(DialogBuilder options) {
        return CommonItemStack.copyOf((ItemStack)options.getDirectoryIconItem()).setCustomNameMessage(ChatColor.YELLOW + this.name).addLoreMessage(ChatColor.WHITE.toString() + ChatColor.ITALIC + this.fullPath()).addLoreLine().addLoreMessage(ChatColor.DARK_GRAY + "Directory").addLoreMessage(ChatColor.DARK_GRAY + "< " + ChatColor.GRAY + this.nestedItemCount + ChatColor.DARK_GRAY + " Item models >");
    }

    public String toString() {
        return "Directory: " + this.path;
    }

    @Override
    protected ListedDirectory cloneSelf(ListedNamespace namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("Namespace is required");
        }
        ListedDirectory clone = new ListedDirectory(namespace, this);
        clone.namespace.directories.put(clone.path, clone);
        return clone;
    }

    @Override
    protected ListedDirectory findOrCreateInRoot(ListedRoot root) {
        ListedEntry newParent = this.parent().findOrCreateInRoot(root);
        return newParent.namespace().findOrCreateDirectory(this.path);
    }
}

