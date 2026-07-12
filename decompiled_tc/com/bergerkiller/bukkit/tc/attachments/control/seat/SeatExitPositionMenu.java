/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentAnchor;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SeatExitPositionMenu
extends MapWidgetMenu {
    private SeatMapWidgetNumberBox _positionX;
    private SeatMapWidgetNumberBox _positionY;
    private SeatMapWidgetNumberBox _positionZ;
    private SeatMapWidgetNumberBox _rotationX;
    private SeatMapWidgetNumberBox _rotationY;
    private SeatMapWidgetNumberBox _rotationZ;

    public SeatExitPositionMenu() {
        this.setBounds(5, 3, 108, 98);
        this.setBackgroundColor((byte)30);
        this.getTitle().setText("Seat Exit Position");
        this.getTitle().setColor(MapColorPalette.getSpecular((byte)30, (float)1.7f));
    }

    @Override
    public void onAttached() {
        super.onAttached();
        int slider_width = 74;
        int y_offset = 12;
        int y_step = 12;
        (this.addWidget(new MapWidgetSelectionBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                for (AttachmentAnchor type : AttachmentAnchor.values()) {
                    this.addItem(type.toString());
                }
                this.setSelectedItem((String)SeatExitPositionMenu.this.getConfig().get("anchor", (Object)AttachmentAnchor.DEFAULT.getName()));
            }

            @Override
            public void onSelectedItemChanged() {
                SeatExitPositionMenu.this.getConfig().set("anchor", (Object)this.getSelectedItem());
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", SeatExitPositionMenu.this.attachment);
                SeatExitPositionMenu.this.previewEjectPosition();
            }
        })).setBounds(30, y_offset, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Anchor");
        this._positionX = (SeatMapWidgetNumberBox)this.addWidget(new SeatMapWidgetNumberBox(this, "posX"));
        this._positionX.setBounds(30, y_offset += y_step, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Pos.X");
        this._positionY = (SeatMapWidgetNumberBox)this.addWidget(new SeatMapWidgetNumberBox(this, "posY"));
        this._positionY.setBounds(30, y_offset += y_step, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Pos.Y");
        this._positionZ = (SeatMapWidgetNumberBox)this.addWidget(new SeatMapWidgetNumberBox(this, "posZ"));
        this._positionZ.setBounds(30, y_offset += y_step, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Pos.Z");
        this._rotationX = (SeatMapWidgetNumberBox)this.addWidget(new SeatMapWidgetNumberBox(this, "rotX"){

            @Override
            public void onActivate() {
                SeatExitPositionMenu.this.setRotationLocked(!SeatExitPositionMenu.this.isRotationLocked());
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", SeatExitPositionMenu.this.attachment);
            }

            @Override
            public void onValueChangeStart() {
                SeatExitPositionMenu.this.setRotationLocked(true);
            }
        });
        this._rotationX.setIncrement(0.1);
        this._rotationX.setBounds(30, y_offset += y_step, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Pitch");
        this._rotationY = (SeatMapWidgetNumberBox)this.addWidget(new SeatMapWidgetNumberBox(this, "rotY"){

            @Override
            public void onActivate() {
                SeatExitPositionMenu.this.setRotationLocked(!SeatExitPositionMenu.this.isRotationLocked());
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", SeatExitPositionMenu.this.attachment);
            }

            @Override
            public void onValueChangeStart() {
                SeatExitPositionMenu.this.setRotationLocked(true);
            }
        });
        this._rotationY.setIncrement(0.1);
        this._rotationY.setBounds(30, y_offset += y_step, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Yaw");
        this._rotationZ = (SeatMapWidgetNumberBox)this.addWidget(new SeatMapWidgetNumberBox(this, "rotZ"){

            @Override
            public void onActivate() {
                SeatExitPositionMenu.this.setRotationLocked(!SeatExitPositionMenu.this.isRotationLocked());
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", SeatExitPositionMenu.this.attachment);
            }

            @Override
            public void onValueChangeStart() {
                SeatExitPositionMenu.this.setRotationLocked(true);
            }
        });
        this._rotationZ.setIncrement(0.1);
        this._rotationZ.setBounds(30, y_offset += y_step, slider_width, 11);
        this.addLabel(5, y_offset + 3, "Roll");
        y_offset += y_step;
        this.refreshRotationLocked();
    }

    public boolean isRotationLocked() {
        return (Boolean)this.getConfig().get("lockRotation", (Object)false);
    }

    public void setRotationLocked(boolean locked) {
        this.getConfig().set("lockRotation", (Object)locked);
        this.refreshRotationLocked();
    }

    public void refreshRotationLocked() {
        String overrideStr = this.isRotationLocked() ? null : "FREE";
        this._rotationX.setTextOverride(overrideStr);
        this._rotationY.setTextOverride(overrideStr);
        this._rotationZ.setTextOverride(overrideStr);
    }

    public ConfigurationNode getConfig() {
        return this.attachment.getConfig().getNode("ejectPosition");
    }

    private void previewEjectPosition() {
        for (Attachment attachment : this.attachment.getAttachments()) {
            if (!(attachment instanceof CartAttachmentSeat)) continue;
            CartAttachmentSeat seat = (CartAttachmentSeat)attachment;
            for (Player viewer : ((AttachmentEditor)this.display).getViewers()) {
                Location ejectPos = ((CartAttachmentSeat)attachment).getEjectPosition((Entity)viewer);
                PlayerUtil.spawnDustParticles((Player)viewer, (Vector)ejectPos.toVector(), (Color)Color.BLUE);
            }
        }
    }

    private class SeatMapWidgetNumberBox
    extends MapWidgetNumberBox {
        private final String field;
        private boolean ignoreValueChange = true;

        public SeatMapWidgetNumberBox(SeatExitPositionMenu menu, String field) {
            this.field = field;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.setValue((Double)SeatExitPositionMenu.this.getConfig().get(this.field, (Object)0.0));
            this.ignoreValueChange = false;
        }

        @Override
        public void onValueChanged() {
            if (this.ignoreValueChange) {
                return;
            }
            SeatExitPositionMenu.this.getConfig().set(this.field, (Object)this.getValue());
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", SeatExitPositionMenu.this.attachment);
            if (this.getChangeRepeat() <= 1) {
                this.onValueChangeStart();
            }
            SeatExitPositionMenu.this.previewEjectPosition();
        }

        public void onValueChangeStart() {
        }

        @Override
        public void onValueChangeEnd() {
        }
    }
}

