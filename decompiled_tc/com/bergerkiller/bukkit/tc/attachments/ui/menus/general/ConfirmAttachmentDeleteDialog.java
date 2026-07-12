/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus.general;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;

public class ConfirmAttachmentDeleteDialog
extends MapWidgetMenu {
    public ConfirmAttachmentDeleteDialog() {
        this.setBounds(10, 22, 98, 58);
        this.setBackgroundColor(MapColorPalette.getColor((int)135, (int)33, (int)33));
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.addWidget(new MapWidgetText().setText("Are you sure you\nwant to delete\nthis attachment?").setBounds(5, 5, 80, 30));
        this.addWidget(new MapWidgetButton(){

            public void onActivate() {
                ConfirmAttachmentDeleteDialog.this.close();
            }
        }.setText("No").setBounds(10, 40, 36, 13));
        this.addWidget(new MapWidgetButton(){

            public void onActivate() {
                ConfirmAttachmentDeleteDialog.this.close();
                ConfirmAttachmentDeleteDialog.this.onConfirmDelete();
            }
        }.setText("Yes").setBounds(52, 40, 36, 13));
    }

    public void onConfirmDelete() {
    }
}

