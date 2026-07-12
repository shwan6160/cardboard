/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 */
package com.bergerkiller.bukkit.tc.attachments.ui.animation;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;

public class ConfirmAnimationDeleteDialog
extends MapWidgetMenu {
    public ConfirmAnimationDeleteDialog() {
        this.setBounds(10, 40, 95, 58);
        this.setBackgroundColor(MapColorPalette.getColor((int)135, (int)33, (int)33));
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.addWidget(new MapWidgetText().setText("Are you sure you\nwant to delete\nthis animation?").setBounds(5, 5, 80, 30));
        this.addWidget(new MapWidgetButton(){

            public void onActivate() {
                ConfirmAnimationDeleteDialog.this.close();
            }
        }.setText("No").setBounds(10, 40, 35, 13));
        this.addWidget(new MapWidgetButton(){

            public void onActivate() {
                ConfirmAnimationDeleteDialog.this.close();
                ConfirmAnimationDeleteDialog.this.onConfirmDelete();
            }
        }.setText("Yes").setBounds(50, 40, 35, 13));
    }

    public void onConfirmDelete() {
    }
}

