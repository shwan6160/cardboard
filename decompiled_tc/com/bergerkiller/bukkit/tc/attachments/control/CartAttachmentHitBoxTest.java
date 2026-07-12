/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.ChatColor
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentHitBox;
import org.bukkit.ChatColor;

public class CartAttachmentHitBoxTest
extends CartAttachmentHitBox {
    public static final AttachmentType TYPE = new CartAttachmentHitBox.BaseHitBoxType(){

        @Override
        public String getID() {
            return "HITBOX_TEST";
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentHitBoxTest();
        }
    };
    private Mode mode = Mode.IDLE;

    @Override
    public void onFocus() {
        this.setBoxColor(ChatColor.BLACK);
    }

    @Override
    public void onBlur() {
        this.setBoxColor(this.mode.getColor());
    }

    @Override
    public void onTick() {
        if (!this.isFocused()) {
            this.mode = Mode.IDLE;
            for (CartAttachmentHitBoxTest other : this.getController().getNameLookup().allOfType(CartAttachmentHitBoxTest.class)) {
                if (this == other) continue;
                if (this.getBoundingBox().isInside(other.getBoundingBox())) {
                    this.mode = Mode.INSIDE;
                    break;
                }
                if (!this.getBoundingBox().hasOverlap(other.getBoundingBox())) continue;
                this.mode = Mode.OVERLAP;
            }
            this.setBoxColor(this.mode.getColor());
        }
    }

    private static enum Mode {
        IDLE(ChatColor.RED),
        OVERLAP(ChatColor.YELLOW),
        INSIDE(ChatColor.GREEN);

        private final ChatColor color;

        private Mode(ChatColor color) {
            this.color = color;
        }

        public ChatColor getColor() {
            return this.color;
        }
    }
}

