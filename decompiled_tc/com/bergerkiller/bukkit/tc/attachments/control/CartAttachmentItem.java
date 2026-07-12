/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetItemTransformTypeSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSizeBox;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetBrightnessDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetItemSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CartAttachmentItem
extends CartAttachment
implements Attachment.ItemDisplayAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "ITEM";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            ItemStack item = (ItemStack)config.get("item", (Object)new ItemStack(Material.MINECART));
            return TCConfig.resourcePack.getItemTexture(item, 16, 16);
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentItem();
        }

        @Override
        public void getDefaultConfig(ConfigurationNode config) {
            config.set("item", (Object)new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_WOOD")));
        }

        @Override
        public void createAppearanceTab(final MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            tab.addWidget((MapWidget)new MapWidgetItemSelector(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setSelectedItem((ItemStack)attachment.getConfig().get("item", (Object)new ItemStack(Material.PUMPKIN)));
                    ItemTransformType type = ItemTransformType.deserialize(attachment.getConfig(), "position.transform");
                    this.setShowBrightnessButton(type.category() != ItemTransformType.Category.ARMORSTAND);
                }

                @Override
                public void onSelectedItemChanged() {
                    attachment.getConfig().set("item", (Object)this.getSelectedItem());
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", attachment);
                    attachment.resetIcon();
                }

                @Override
                public void onBrightnessClicked() {
                    ((MapWidgetBrightnessDialog.AttachmentBrightnessDialog)tab.addWidget((MapWidget)new MapWidgetBrightnessDialog.AttachmentBrightnessDialog(attachment))).setPosition(13, 3).activate();
                }
            });
        }

        @Override
        public void createPositionMenu(PositionMenu.Builder builder) {
            PositionMenu.Row transformRow = builder.addRow(1, menu -> new MapWidgetItemTransformTypeSelector(this, (PositionMenu)((Object)menu)){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ 1 this$0;
                {
                    this.val$menu = positionMenu;
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    this.setSelectedType(ItemTransformType.deserialize(this.val$menu.getPositionConfig(), "transform"));
                    super.onAttached();
                }

                @Override
                public void onSelectedTypeChanged(ItemTransformType type) {
                    this.val$menu.updatePositionConfig(config -> config.set("transform", (Object)type.serializedName()));
                    for (MapWidget widget : this.getParent().getWidgets()) {
                        if (widget instanceof ScaleWidget) {
                            widget.setEnabled(type.category() == ItemTransformType.Category.DISPLAY);
                            continue;
                        }
                        if (!(widget instanceof ClipWidget)) continue;
                        widget.setEnabled(type.category() != ItemTransformType.Category.ARMORSTAND);
                    }
                }
            }.setBounds(25, 0, menu.getSliderWidth(), MapWidgetItemTransformTypeSelector.defaultHeight()));
            transformRow.addLabel(0, 3, "Mode");
            if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
                transformRow.addLabel(0, 15, "Tr.form");
            }
            if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
                builder.addRow(menu -> new ClipWidget((PositionMenu)((Object)menu)).setBounds(25, 0, menu.getSliderWidth(), 11)).addLabel(0, 3, "Clip").setSpacingAbove(3);
                builder.addRow(menu -> new ScaleWidget((PositionMenu)((Object)menu)).setBounds(25, 0, menu.getSliderWidth(), 35)).addLabel(0, 3, "Size X").addLabel(0, 15, "Size Y").addLabel(0, 27, "Size Z");
            }
        }
    };
    private VirtualSpawnableObject entity;

    @Override
    public void onAttached() {
        super.onAttached();
        ItemTransformType type = ItemTransformType.deserialize(this.getConfig(), "position.transform");
        this.entity = type.create(this.getManager(), null);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        this.entity = null;
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        if (!super.checkCanReload(config)) {
            return false;
        }
        ItemTransformType type = ItemTransformType.deserialize(config, "position.transform");
        return type.canUpdate(this.entity);
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        super.onLoad(config);
        ItemTransformType.deserialize(config, "position.transform").load(this.entity, config, this.getConfiguredPosition());
    }

    @Override
    public ItemStack getDisplayedItem() {
        if (this.entity instanceof VirtualSpawnableObject.ItemDisplay) {
            return ((VirtualSpawnableObject.ItemDisplay)((Object)this.entity)).getItem();
        }
        return null;
    }

    @Override
    public void setDisplayedItem(ItemStack item) {
        if (this.entity instanceof VirtualSpawnableObject.ItemDisplay) {
            ((VirtualSpawnableObject.ItemDisplay)((Object)this.entity)).setItem(item);
        }
        this.getConfig().set("item", (Object)item);
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return this.entity != null && this.entity.containsEntityId(entityId);
    }

    @Override
    public int getMountEntityId() {
        return -1;
    }

    @Override
    @Deprecated
    public void makeVisible(Player player) {
        this.makeVisible(this.getManager().asAttachmentViewer(player));
    }

    @Override
    @Deprecated
    public void makeHidden(Player player) {
        this.makeHidden(this.getManager().asAttachmentViewer(player));
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        this.entity.spawn(viewer, new Vector(0.0, 0.0, 0.0));
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        this.entity.destroy(viewer);
    }

    @Override
    public void onFocus() {
        this.entity.setGlowColor(HelperMethods.getFocusGlowColor(this));
    }

    @Override
    public void onBlur() {
        this.entity.setGlowColor(null);
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.entity.updatePosition(transform);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onMove(boolean absolute) {
        this.entity.syncPosition(absolute);
    }

    private static class ScaleWidget
    extends MapWidgetSizeBox {
        private final PositionMenu menu;

        public ScaleWidget(PositionMenu menu) {
            this.menu = menu;
        }

        public void onAttached() {
            super.onAttached();
            this.setInitialSize(this.menu.getPositionConfigValue("sizeX", 1.0), this.menu.getPositionConfigValue("sizeY", 1.0), this.menu.getPositionConfigValue("sizeZ", 1.0));
            this.setEnabled(ItemTransformType.deserialize(this.menu.getPositionConfig(), "transform").category() == ItemTransformType.Category.DISPLAY);
        }

        @Override
        public void onSizeChanged() {
            this.menu.updatePositionConfig(config -> {
                if (this.x.getValue() == 1.0 && this.y.getValue() == 1.0 && this.z.getValue() == 1.0) {
                    config.remove("sizeX");
                    config.remove("sizeY");
                    config.remove("sizeZ");
                } else {
                    config.set("sizeX", (Object)this.x.getValue());
                    config.set("sizeY", (Object)this.y.getValue());
                    config.set("sizeZ", (Object)this.z.getValue());
                }
            });
        }
    }

    private static class ClipWidget
    extends MapWidgetNumberBox {
        private final PositionMenu menu;

        public ClipWidget(PositionMenu menu) {
            this.menu = menu;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.setRange(0.0, 1000.0);
            this.setInitialValue(this.menu.getPositionConfigValue("clip", 0.0));
            if (this.getValue() == 0.0) {
                this.setTextOverride("<Disabled>");
            }
            this.setEnabled(ItemTransformType.deserialize(this.menu.getPositionConfig(), "transform").category() != ItemTransformType.Category.ARMORSTAND);
        }

        @Override
        public void onValueChanged() {
            this.setTextOverride(this.getValue() == 0.0 ? "<Disabled>" : null);
            this.menu.updatePositionConfigValue("clip", this.getValue() == 0.0 ? null : Double.valueOf(this.getValue()));
        }
    }
}

