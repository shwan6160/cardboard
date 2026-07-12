/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedItemModel;
import org.bukkit.inventory.ItemStack;

public final class DialogResult {
    private final ListedItemModel result;
    private final DialogBuilder dialog;
    private final boolean closedWithRootRightClick;

    public DialogResult(DialogBuilder dialog, boolean closedWithRootRightClick) {
        this.result = null;
        this.dialog = dialog;
        this.closedWithRootRightClick = closedWithRootRightClick;
    }

    public DialogResult(DialogBuilder dialog, ListedItemModel result) {
        this.result = result;
        this.dialog = dialog;
        this.closedWithRootRightClick = false;
    }

    public DialogBuilder dialog() {
        return this.dialog;
    }

    public boolean success() {
        return this.result != null;
    }

    public boolean cancelled() {
        return this.result == null;
    }

    public boolean cancelledWithRootRightClick() {
        return this.closedWithRootRightClick;
    }

    public ListedItemModel selected() {
        return this.result;
    }

    public ItemStack selectedItem() {
        return this.result == null ? null : this.result.item().toBukkit();
    }

    public ItemStack selectedBareItem() {
        return this.result == null ? null : this.result.bareItem().toBukkit();
    }
}

