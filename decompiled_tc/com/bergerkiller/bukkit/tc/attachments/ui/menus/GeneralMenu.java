/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.general.ConfirmAttachmentDeleteDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.general.ModelStorageTypeSelectionDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.general.NameAttachmentDialog;
import java.util.Collections;
import org.bukkit.inventory.ItemStack;

public class GeneralMenu
extends MapWidgetMenu {
    public GeneralMenu() {
        this.setBounds(5, 15, 118, 104);
        this.setBackgroundColor((byte)122);
    }

    private void addAndSelectAttachment(ConfigurationNode newAttachmentConfig) {
        MapWidgetAttachmentNode added = this.attachment.addAttachment(newAttachmentConfig);
        this.close();
        this.attachment.getTree().setSelectedNode(added);
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "reset");
    }

    @Override
    public void onAttached() {
        super.onAttached();
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                ConfigurationNode config = new ConfigurationNode();
                AttachmentTypeRegistry.instance().toConfig(config, CartAttachmentItem.TYPE);
                config.set("item", (Object)new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_WOOD")));
                GeneralMenu.this.addAndSelectAttachment(config);
            }
        })).setText("Add Attachment").setBounds(10, 8, 85, 13);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                GeneralMenu.this.addWidget((MapWidget)new ModelStorageTypeSelectionDialog.LoadDialog(){

                    @Override
                    public void onConfigLoaded(ConfigurationNode attachmentConfig) {
                        GeneralMenu.this.addAndSelectAttachment(attachmentConfig);
                    }
                });
            }
        })).setText("V").setBounds(96, 8, 12, 13);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                int index = GeneralMenu.this.attachment.getParentAttachment().getChildAttachmentNodes().indexOf(GeneralMenu.this.attachment);
                MapWidgetAttachmentNode addedNode = GeneralMenu.this.attachment.getParentAttachment().addAttachment(index + 1, GeneralMenu.this.attachment.getConfig().clone());
                GeneralMenu.this.attachment.getTree().setSelectedNode(addedNode);
                this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "reset");
                GeneralMenu.this.close();
            }
        })).setText("Duplicate").setBounds(10, 23, 98, 13).setEnabled(this.attachment.getParentAttachment() != null);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                GeneralMenu.this.addWidget((MapWidget)new NameAttachmentDialog(GeneralMenu.this.attachment));
            }
        })).setText("Name").setBounds(10, 38, 98, 13);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                GeneralMenu.this.attachment.setChangingOrder(true);
                GeneralMenu.this.close();
            }
        })).setText("Change Order").setBounds(10, 53, 98, 13);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                GeneralMenu.this.addWidget((MapWidget)new ConfirmAttachmentDeleteDialog(){

                    @Override
                    public void onConfirmDelete() {
                        GeneralMenu.this.attachment.remove();
                        GeneralMenu.this.close();
                    }
                });
            }
        })).setText("Delete").setBounds(10, 68, 98, 13).setEnabled(this.attachment.getParentAttachment() != null);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                GeneralMenu.this.addWidget((MapWidget)new ModelStorageTypeSelectionDialog.SaveDialog(GeneralMenu.this.attachment.getConfig()){

                    @Override
                    public void onExported() {
                        GeneralMenu.this.close();
                    }
                });
            }
        })).setText("Save").setBounds(10, 83, 48, 13);
        (this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                GeneralMenu.this.addWidget((MapWidget)new ModelStorageTypeSelectionDialog.LoadDialog(){

                    @Override
                    public void onConfigLoaded(ConfigurationNode attachmentConfig) {
                        GeneralMenu.this.attachment.getConfig().setToExcept((YamlNodeAbstract)attachmentConfig, Collections.singleton("savedName"));
                        GeneralMenu.this.close();
                    }
                });
            }
        })).setText("Load").setBounds(60, 83, 48, 13);
    }

    public MapWidgetAttachmentNode getAttachment() {
        return this.attachment;
    }
}

