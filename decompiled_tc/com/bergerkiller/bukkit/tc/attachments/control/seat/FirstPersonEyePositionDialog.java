/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FirstPersonEyePositionDialog
extends MapWidgetMenu {
    private final MapWidgetAttachmentNode attachment;
    private boolean isLoadingWidgets;

    public FirstPersonEyePositionDialog(MapWidgetAttachmentNode attachment) {
        this.attachment = attachment;
        this.setBounds(0, -10, 103, 95);
        this.setBackgroundColor((byte)30);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.isLoadingWidgets = true;
        int slider_width = 75;
        int y_offset = 5;
        int y_step = 12;
        ((SeatEyeNumberBox)this.addWidget(new SeatEyeNumberBox("posX", "Position X-Coordinate", 0.01))).setBounds(26, y_offset, slider_width, 11);
        this.addLabel(4, y_offset + 3, "Pos.X");
        ((SeatEyeNumberBox)this.addWidget(new SeatEyeNumberBox("posY", "Position Y-Coordinate", 0.01))).setBounds(26, y_offset += y_step, slider_width, 11);
        this.addLabel(4, y_offset + 3, "Pos.Y");
        ((SeatEyeNumberBox)this.addWidget(new SeatEyeNumberBox("posZ", "Position Z-Coordinate", 0.01))).setBounds(26, y_offset += y_step, slider_width, 11);
        this.addLabel(4, y_offset + 3, "Pos.Z");
        ((SeatEyeNumberBox)this.addWidget(new SeatEyeNumberBox("rotX", "Rotation Pitch", 0.1))).setBounds(26, y_offset += y_step, slider_width, 11);
        this.addLabel(4, y_offset + 3, "Pitch");
        ((SeatEyeNumberBox)this.addWidget(new SeatEyeNumberBox("rotY", "Rotation Yaw", 0.1))).setBounds(26, y_offset += y_step, slider_width, 11);
        this.addLabel(4, y_offset + 3, "Yaw");
        ((SeatEyeNumberBox)this.addWidget(new SeatEyeNumberBox("rotZ", "Rotation Roll", 0.1))).setBounds(26, y_offset += y_step, slider_width, 11);
        this.addLabel(4, y_offset + 3, "Roll");
        (this.addWidget((MapWidget)new MapWidgetButton(){
            private final MapWidgetTooltip tooltip = new MapWidgetTooltip();

            public void onAttached() {
                super.onAttached();
                this.tooltip.setText("Sets eye position based\non seat display mode");
            }

            public void onFocus() {
                this.addWidget(this.tooltip);
            }

            public void onBlur() {
                this.removeWidget(this.tooltip);
            }

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                ConfigurationNode config = FirstPersonEyePositionDialog.this.attachment.getConfig();
                if (config.isNode("firstPersonViewPosition")) {
                    config.remove("firstPersonViewPosition");
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                }
                FirstPersonEyePositionDialog.this.setAutomaticDisplayed(true);
                FirstPersonEyePositionDialog.this.showArrowPreview(true);
            }
        })).setText("Automatic").setBounds(33, y_offset += y_step, 61, 13);
        (this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                FirstPersonEyePositionDialog.this.previewEye(20);
            }

            @Override
            public void onRepeatClick() {
                FirstPersonEyePositionDialog.this.previewEye(2);
            }

            @Override
            public void onClickHoldRelease() {
                FirstPersonEyePositionDialog.this.previewEye(0);
            }
        })).setRepeatClickEnabled(true).setTooltip("Preview").setIcon("attachments/view_camera_preview.png").setPosition(17, y_offset);
        this.isLoadingWidgets = false;
    }

    public void onDetached() {
        super.onDetached();
        this.previewEye(0);
        this.showArrowPreview(false);
    }

    private void previewEye(int numTicks) {
        List<Attachment> attachments = this.attachment.getAttachments();
        if (attachments.isEmpty()) {
            return;
        }
        for (Player player : this.display.getOwners()) {
            if (!this.display.isControlling(player)) continue;
            Vector pos = player.getEyeLocation().toVector();
            Attachment closestSeat = null;
            for (Attachment liveAttachment : attachments) {
                if (!(liveAttachment instanceof CartAttachmentSeat)) continue;
                if (closestSeat == null) {
                    closestSeat = (CartAttachmentSeat)liveAttachment;
                    continue;
                }
                double d1 = closestSeat.getTransform().toVector().distanceSquared(pos);
                double d2 = liveAttachment.getTransform().toVector().distanceSquared(pos);
                if (!(d2 < d1)) continue;
                closestSeat = (CartAttachmentSeat)liveAttachment;
            }
            if (closestSeat == null) continue;
            ((CartAttachmentSeat)closestSeat).debug.previewEye(player, numTicks);
        }
    }

    public boolean isAutomatic() {
        return !this.attachment.getConfig().isNode("firstPersonViewPosition");
    }

    private void setAutomaticDisplayed(boolean automatic) {
        this.isLoadingWidgets = true;
        for (MapWidget widget : this.getWidgets()) {
            if (!(widget instanceof SeatEyeNumberBox)) continue;
            ((SeatEyeNumberBox)widget).setAutomatic(automatic);
        }
        this.isLoadingWidgets = false;
    }

    public <T> T getConfigValue(String key, T def) {
        ConfigurationNode config = this.attachment.getConfig();
        if (config.isNode("firstPersonViewPosition") && (config = config.getNode("firstPersonViewPosition")).contains(key)) {
            return (T)config.get(key, def);
        }
        return def;
    }

    public void updateConfigValue(String key, Object value) {
        if (this.isLoadingWidgets) {
            return;
        }
        ConfigurationNode config = this.attachment.getConfig();
        if (config.isNode("firstPersonViewPosition")) {
            config = config.getNode("firstPersonViewPosition");
            config.set(key, value);
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", this.attachment);
        } else {
            config = config.getNode("firstPersonViewPosition");
            config.set("posX", (Object)0.0);
            config.set("posY", (Object)0.0);
            config.set("posZ", (Object)0.0);
            config.set("rotX", (Object)0.0);
            config.set("rotY", (Object)0.0);
            config.set("rotZ", (Object)0.0);
            config.set(key, value);
            this.setAutomaticDisplayed(false);
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
        }
        this.showArrowPreview(true);
    }

    private void showArrowPreview(boolean show) {
        int numTicks = show ? 100 : 0;
        for (Attachment liveAttachment : this.attachment.getAttachments()) {
            if (!(liveAttachment instanceof CartAttachmentSeat)) continue;
            for (Player player : this.display.getOwners()) {
                if (!this.display.isControlling(player)) continue;
                ((CartAttachmentSeat)liveAttachment).debug.showEyeArrow(player, numTicks);
            }
        }
    }

    private class SeatEyeNumberBox
    extends MapWidgetNumberBox {
        private final String configField;
        private final String acceptedPropertyName;

        public SeatEyeNumberBox(String configField, String acceptedPropertyName, double increment) {
            this.configField = configField;
            this.acceptedPropertyName = acceptedPropertyName;
            this.setIncrement(increment);
        }

        @Override
        public String getAcceptedPropertyName() {
            return this.acceptedPropertyName;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.setAutomatic(FirstPersonEyePositionDialog.this.isAutomatic());
        }

        @Override
        public void onValueChanged() {
            FirstPersonEyePositionDialog.this.updateConfigValue(this.configField, this.getValue());
        }

        public void setAutomatic(boolean automatic) {
            this.setTextOverride(automatic ? "Auto" : null);
            if (automatic) {
                this.setValue(0.0);
            } else {
                this.setValue(FirstPersonEyePositionDialog.this.getConfigValue(this.configField, 0.0));
            }
        }
    }
}

