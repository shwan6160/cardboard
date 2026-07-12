/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.attachments.ui.models.ResourcePackModelListing;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogResult;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class DialogBuilder
implements Cloneable {
    private static final ItemStack DEFAULT_NAMESPACE_ITEM = DialogBuilder.createDefaultNamespaceItem();
    private static final ItemStack DEFAULT_DIRECTORY_ITEM = DialogBuilder.createDefaultDirectoryItem();
    private final Plugin plugin;
    private final Player player;
    private final ResourcePackModelListing listing;
    private boolean creativeMenu = false;
    private boolean compactingEnabled = TCConfig.modelSearchCompactFolders;
    private String title = "Resource Pack Models";
    private String query = "";
    private String browsedLocation = "";
    private int browsedPage = 0;
    private boolean cancelOnRootRightClick = false;
    private ItemStack namespaceItem = DEFAULT_NAMESPACE_ITEM;
    private ItemStack directoryItem = DEFAULT_DIRECTORY_ITEM;

    public DialogBuilder(Plugin plugin, Player player, ResourcePackModelListing listing) {
        this.plugin = plugin;
        this.player = player;
        this.listing = listing;
    }

    public Plugin plugin() {
        return this.plugin;
    }

    public Player player() {
        return this.player;
    }

    public ResourcePackModelListing listing() {
        return this.listing;
    }

    public DialogBuilder navigate(String path, int page) {
        this.browsedLocation = path;
        this.browsedPage = page;
        return this;
    }

    public String getBrowsedPath() {
        return this.browsedLocation;
    }

    public int getBrowsedPage() {
        return this.browsedPage;
    }

    public DialogBuilder asCreativeMenu() {
        this.creativeMenu = true;
        return this;
    }

    public boolean isCreativeMenu() {
        return this.creativeMenu;
    }

    public DialogBuilder setCompactingEnabled(boolean compact) {
        this.compactingEnabled = compact;
        return this;
    }

    public boolean isCompactingEnabled() {
        return this.compactingEnabled;
    }

    public DialogBuilder title(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public DialogBuilder namespaceIconItem(ItemStack item) {
        if (item == null) {
            throw new IllegalArgumentException("Item may not be null");
        }
        this.namespaceItem = item;
        return this;
    }

    public ItemStack getNamespaceIconItem() {
        return this.namespaceItem;
    }

    public DialogBuilder directoryIconItem(ItemStack item) {
        if (item == null) {
            throw new IllegalArgumentException("Item may not be null");
        }
        this.directoryItem = item;
        return this;
    }

    public ItemStack getDirectoryIconItem() {
        return this.directoryItem;
    }

    public DialogBuilder cancelOnRootRightClick() {
        return this.cancelOnRootRightClick(true);
    }

    public DialogBuilder cancelOnRootRightClick(boolean cancel) {
        this.cancelOnRootRightClick = cancel;
        return this;
    }

    public boolean isCancelOnRootRightClick() {
        return this.cancelOnRootRightClick;
    }

    public DialogBuilder query(String query) {
        this.query = query;
        return this;
    }

    public String getQuery() {
        return this.query;
    }

    public CompletableFuture<DialogResult> show() {
        return ResourcePackModelListing.showDialog(this);
    }

    public DialogBuilder clone() {
        DialogBuilder clone = new DialogBuilder(this.plugin, this.player, this.listing);
        clone.browsedLocation = this.browsedLocation;
        clone.browsedPage = this.browsedPage;
        clone.creativeMenu = this.creativeMenu;
        clone.title = this.title;
        clone.query = this.query;
        clone.cancelOnRootRightClick = this.cancelOnRootRightClick;
        clone.namespaceItem = this.namespaceItem;
        clone.directoryItem = this.directoryItem;
        clone.compactingEnabled = this.compactingEnabled;
        return clone;
    }

    private static ItemStack createDefaultNamespaceItem() {
        return CommonItemStack.create((Material)MaterialUtil.getFirst((String[])new String[]{"NAME_TAG", "LEGACY_NAME_TAG"}), (int)1).hideAllAttributes().toBukkit();
    }

    private static ItemStack createDefaultDirectoryItem() {
        return CommonItemStack.create((Material)MaterialUtil.getFirst((String[])new String[]{"CHEST", "LEGACY_CHEST"}), (int)1).hideAllAttributes().toBukkit();
    }
}

