/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.widgets.MapWidgetAnvil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MapWidgetSubmitText
extends MapWidgetAnvil {
    private boolean _accepted = false;

    public MapWidgetSubmitText() {
        this.LEFT_BUTTON.setMaterial(Material.BARRIER);
        this.LEFT_BUTTON.setDescription(ChatColor.RED + "Cancel");
        this.RIGHT_BUTTON.setMaterial(Material.EMERALD);
        this.RIGHT_BUTTON.setDescription(ChatColor.GREEN + "Accept");
    }

    public void onAccept(String text) {
    }

    public void onCancel() {
    }

    public MapWidgetSubmitText setDescription(String text) {
        if (text == null || text.isEmpty()) {
            this.MIDDLE_BUTTON.setMaterial(null);
        } else {
            this.MIDDLE_BUTTON.setMaterial(Material.PAPER);
            this.MIDDLE_BUTTON.setTitle(ChatColor.YELLOW + "About");
            this.MIDDLE_BUTTON.setDescription(text);
        }
        return this;
    }

    @Override
    public ChatText getTitle() {
        String desc = this.MIDDLE_BUTTON.getDescription();
        if (desc != null && !desc.isEmpty()) {
            return ChatText.fromMessage(desc);
        }
        return super.getTitle();
    }

    @Override
    public void onClick(MapWidgetAnvil.Button button) {
        if (button == this.RIGHT_BUTTON) {
            if (this.getText() != null && !this.getText().isEmpty()) {
                this._accepted = true;
                this.deactivate();
            }
        } else if (button == this.LEFT_BUTTON) {
            this._accepted = false;
            this.deactivate();
        }
    }

    @Override
    public void onClose() {
        if (this._accepted) {
            this.onAccept(this.getText());
        } else {
            this.onCancel();
        }
    }

    @Override
    public void onActivate() {
        super.onActivate();
        this._accepted = false;
    }
}

