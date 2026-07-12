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
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedDirectory;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedEntry;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRoot;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public final class ListedNamespace
extends ListedEntry {
    private final String name;
    private final String nameLowerCase;
    final Map<String, ListedDirectory> directories;

    public ListedNamespace(String namespace) {
        this.name = namespace;
        this.nameLowerCase = namespace.toLowerCase(Locale.ENGLISH);
        this.directories = new HashMap<String, ListedDirectory>();
    }

    private ListedNamespace(ListedNamespace namespace) {
        this.name = namespace.name;
        this.nameLowerCase = namespace.nameLowerCase;
        this.directories = new HashMap<String, ListedDirectory>(namespace.directories.size());
    }

    protected ListedDirectory findOrCreateDirectory(String path) {
        ListedDirectory entry = this.initDirectory(path);
        if (entry.parent() == null) {
            ListedDirectory d = entry;
            while (true) {
                int d_path_end;
                if ((d_path_end = d.path().lastIndexOf(47)) == -1) {
                    d.setParent(this);
                    break;
                }
                String parent_dir_path = d.path().substring(0, d_path_end);
                ListedDirectory dp = this.initDirectory(parent_dir_path);
                d.setParent(dp);
                if (dp.parent() != null) break;
                d = dp;
            }
        }
        return entry;
    }

    private ListedDirectory initDirectory(String path) {
        return this.directories.computeIfAbsent(path, p -> new ListedDirectory(this, (String)p));
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String nameLowerCase() {
        return this.nameLowerCase;
    }

    @Override
    public String fullPath() {
        return this.name;
    }

    @Override
    public int sortPriority() {
        return 1;
    }

    @Override
    public ListedNamespace namespace() {
        return this;
    }

    @Override
    public CommonItemStack createIconItem(DialogBuilder options) {
        return CommonItemStack.copyOf((ItemStack)options.getNamespaceIconItem()).setCustomNameMessage(ChatColor.YELLOW + this.name).addLoreLine().addLoreMessage(ChatColor.DARK_GRAY + "Namespace").addLoreMessage(ChatColor.DARK_GRAY + "< " + ChatColor.GRAY + this.nestedItemCount + ChatColor.DARK_GRAY + " Item models >");
    }

    public String toString() {
        return "Namespace: " + this.name;
    }

    @Override
    protected ListedNamespace cloneSelf(ListedNamespace namespace) {
        if (namespace != null) {
            throw new IllegalArgumentException("Namespace entries cannot be in a namespace");
        }
        return new ListedNamespace(this);
    }

    @Override
    protected ListedEntry findOrCreateInRoot(ListedRoot root) {
        return root.findOrCreateNamespace(this.name);
    }
}

