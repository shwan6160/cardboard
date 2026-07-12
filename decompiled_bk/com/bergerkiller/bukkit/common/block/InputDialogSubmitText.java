/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.block.InputDialogAnvil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class InputDialogSubmitText
extends InputDialogAnvil {
    private boolean _accepted = false;
    private boolean _acceptEmptyText = false;

    public InputDialogSubmitText(Plugin plugin, Player player) {
        super(plugin, player);
        this.LEFT_BUTTON.setMaterial(Material.BARRIER);
        this.LEFT_BUTTON.setDescription(ChatColor.RED + "Cancel");
        this.RIGHT_BUTTON.setMaterial(Material.EMERALD);
        this.RIGHT_BUTTON.setDescription(ChatColor.GREEN + "Accept");
    }

    public void onAccept(String text) {
    }

    public void onCancel() {
    }

    public InputDialogSubmitText setDescription(String text) {
        if (text == null || text.isEmpty()) {
            this.MIDDLE_BUTTON.setMaterial(null);
        } else {
            this.MIDDLE_BUTTON.setMaterial(Material.PAPER);
            this.setMiddleButtonTitle(ChatColor.YELLOW + "About");
            this.MIDDLE_BUTTON.setDescription(text);
        }
        return this;
    }

    @Override
    public InputDialogSubmitText setInitialText(String text) {
        super.setInitialText(text);
        return this;
    }

    public InputDialogSubmitText setAcceptEmptyText(boolean accept) {
        this._acceptEmptyText = accept;
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
    public void onClick(InputDialogAnvil.Button button) {
        if (button == this.RIGHT_BUTTON) {
            if (this.getText() != null && (this._acceptEmptyText || !this.getText().isEmpty())) {
                this._accepted = true;
                this.close();
            }
        } else if (button == this.LEFT_BUTTON) {
            this._accepted = false;
            this.close();
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
    public void open() {
        super.open();
        this._accepted = false;
    }
}

