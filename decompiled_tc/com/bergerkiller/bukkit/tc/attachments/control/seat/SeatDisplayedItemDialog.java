/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.ui.ItemDropTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetItemSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SeatDisplayedItemDialog
extends MapWidgetMenu {
    MapWidget setItemButton;
    MapWidget positionButton;
    MapWidget showFPVButton;
    MapWidget disableButton;

    public SeatDisplayedItemDialog() {
        this.setBounds(17, 16, 84, 79);
        this.setBackgroundColor(MapColorPalette.getColor((int)16, (int)16, (int)128));
    }

    @Override
    public void onAttached() {
        this.setItemButton = (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                (this.getParent().addWidget((MapWidget)new SelectItemDialog(){

                    @Override
                    public void onDetached() {
                        super.onDetached();
                        if (((Boolean)this.attachment.getConfig().get("displayItem.enabled", (Object)false)).booleanValue()) {
                            SeatDisplayedItemDialog.this.positionButton.setEnabled(true);
                            SeatDisplayedItemDialog.this.showFPVButton.setEnabled(true);
                            SeatDisplayedItemDialog.this.disableButton.setEnabled(true);
                        }
                    }
                })).setAttachment(SeatDisplayedItemDialog.this.attachment);
            }
        })).setText("Set Item").setBounds(5, 5, 74, 15);
        this.positionButton = (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                ((PositionItemDialog)this.getParent().addWidget((MapWidget)new PositionItemDialog())).setAttachment(SeatDisplayedItemDialog.this.attachment);
            }
        })).setText("Position").setBounds(5, 23, 74, 15);
        this.showFPVButton = (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onAttached() {
                this.updateText();
            }

            public void onActivate() {
                SeatDisplayedItemDialog.this.attachment.getConfig().set("displayItem.showFirstPerson", (Object)((Boolean)SeatDisplayedItemDialog.this.attachment.getConfig().get("displayItem.showFirstPerson", (Object)false) == false ? 1 : 0));
                this.updateText();
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
            }

            private void updateText() {
                this.setText((Boolean)SeatDisplayedItemDialog.this.attachment.getConfig().get("displayItem.showFirstPerson", (Object)false) != false ? "FPV: Visible" : "FPV: Hidden");
            }
        })).setBounds(5, 41, 74, 15);
        this.disableButton = (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                SeatDisplayedItemDialog.this.attachment.getConfig().set("displayItem.enabled", (Object)false);
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                SeatDisplayedItemDialog.this.positionButton.setEnabled(false);
                SeatDisplayedItemDialog.this.showFPVButton.setEnabled(false);
                SeatDisplayedItemDialog.this.disableButton.setEnabled(false);
                SeatDisplayedItemDialog.this.setItemButton.focus();
                this.display.playSound(SoundEffect.EXTINGUISH);
            }
        })).setText("Disable").setBounds(5, 59, 74, 15);
        boolean isEnabled = (Boolean)this.attachment.getConfig().get("displayItem.enabled", (Object)false);
        this.positionButton.setEnabled(isEnabled);
        this.showFPVButton.setEnabled(isEnabled);
        this.disableButton.setEnabled(isEnabled);
        super.onAttached();
    }

    private static class SelectItemDialog
    extends MapWidgetMenu
    implements ItemDropTarget {
        private MapWidgetItemSelector selector;

        public SelectItemDialog() {
            this.setBounds(-13, -12, 111, 97);
            this.setBackgroundColor(MapColorPalette.getColor((int)0, (int)128, (int)200));
            this.setDepthOffset(1);
        }

        @Override
        public void onAttached() {
            this.selector = (MapWidgetItemSelector)this.addWidget(new MapWidgetItemSelector(){

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setSelectedItem((ItemStack)attachment.getConfig().get("displayItem.item", (Object)new ItemStack(Material.PUMPKIN)));
                }

                @Override
                public void onSelectedItemChanged() {
                    boolean wasEnabled = (Boolean)attachment.getConfig().get("displayItem.enabled", (Object)false);
                    attachment.getConfig().set("displayItem.item", (Object)this.getSelectedItem());
                    attachment.getConfig().set("displayItem.enabled", (Object)true);
                    if (wasEnabled) {
                        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", attachment);
                    } else {
                        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    }
                }
            });
            this.selector.setPosition(5, 5);
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            super.onAttached();
        }

        public void onDetached() {
            super.onDetached();
            this.display.playSound(SoundEffect.PISTON_CONTRACT);
        }

        @Override
        public boolean acceptItem(ItemStack item) {
            this.selector.setSelectedItem(item);
            this.display.playSound(SoundEffect.CLICK_WOOD);
            return true;
        }
    }

    private static class PositionItemDialog
    extends PositionMenu {
        private PositionItemDialog() {
        }

        @Override
        public ConfigurationNode getConfig() {
            return super.getConfig().getNode("displayItem");
        }

        @Override
        protected AttachmentType getMenuAttachmentType() {
            return CartAttachmentItem.TYPE;
        }
    }
}

