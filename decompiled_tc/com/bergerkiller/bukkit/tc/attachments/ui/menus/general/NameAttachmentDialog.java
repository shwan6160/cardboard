/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus.general;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNameSet;
import java.util.List;

public class NameAttachmentDialog
extends MapWidgetMenu {
    public NameAttachmentDialog(MapWidgetAttachmentNode attachment) {
        this.attachment = attachment;
        this.setBounds(5, 8, 108, 89);
        this.setBackgroundColor(MapColorPalette.getColor((int)53, (int)33, (int)167));
    }

    @Override
    public void onAttached() {
        List setNames = this.attachment.getConfig().getList("names", String.class);
        (this.addWidget(new MapWidgetNameSet(){

            @Override
            public void onItemAdded(String item) {
                NameAttachmentDialog.this.attachment.getConfig().getList("names", String.class).add(item);
            }

            @Override
            public void onItemRemoved(String item) {
                List names = NameAttachmentDialog.this.attachment.getConfig().getList("names", String.class);
                if (names.remove(item) && names.isEmpty()) {
                    NameAttachmentDialog.this.attachment.getConfig().remove("names");
                }
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.BACK && this.isActivated()) {
                    NameAttachmentDialog.this.close();
                } else {
                    super.onKeyPressed(event);
                }
            }
        })).setNewItemText("+++ New Name +++").setNewItemDescription("Add a new name").setItems(this.attachment.getConfig().getList("names", String.class)).setBounds(5, 5, this.getWidth() - 10, this.getHeight() - 10).activate();
    }
}

