/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayBlockEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.block.BlockDataTextureCache;
import com.bergerkiller.bukkit.tc.attachments.ui.block.MapWidgetBlockDataSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetBrightnessDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentBlock
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "BLOCK_DISPLAY";
        }

        @Override
        public String getName() {
            return "BLOCK";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            BlockData blockData = CartAttachmentBlock.deserializeBlockData(config);
            if (blockData != null) {
                return BlockDataTextureCache.get(16, 16).get(blockData);
            }
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/unknown_block.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentBlock();
        }

        @Override
        public void getDefaultConfig(ConfigurationNode config) {
            config.set("blockData", (Object)BlockData.fromMaterial((Material)MaterialUtil.getFirst((String[])new String[]{"COBBLESTONE", "LEGACY_COBBLESTONE"})).serializeToString());
        }

        @Override
        public void createAppearanceTab(final MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            MapWidgetBlockDataSelector selector = new MapWidgetBlockDataSelector(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setSelectedBlockData(CartAttachmentBlock.deserializeBlockData(attachment.getConfig()));
                }

                @Override
                public void onSelectedBlockDataChanged(BlockData blockData) {
                    attachment.getConfig().set("blockData", (Object)(blockData == null ? null : blockData.serializeToString()));
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", attachment);
                    attachment.resetIcon();
                }

                @Override
                public void onBrightnessClicked() {
                    ((MapWidgetBrightnessDialog.AttachmentBrightnessDialog)tab.addWidget((MapWidget)new MapWidgetBrightnessDialog.AttachmentBrightnessDialog(attachment))).setPosition(13, 3).activate();
                }
            };
            selector.showBrightnessButton();
            tab.addWidget((MapWidget)selector);
        }

        @Override
        public void createPositionMenu(PositionMenu.Builder builder) {
            builder.addSizeBox();
        }
    };
    private VirtualDisplayBlockEntity entity;

    private static BlockData deserializeBlockData(ConfigurationNode config) {
        String blockDataStr = (String)config.get("blockData", String.class);
        if (blockDataStr != null) {
            return BlockData.fromString((String)blockDataStr);
        }
        return null;
    }

    @Override
    public void onAttached() {
        this.entity = new VirtualDisplayBlockEntity(this.getManager());
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        this.entity.setBlockData(CartAttachmentBlock.deserializeBlockData(config));
        this.entity.setScale(this.getConfiguredPosition().size);
        this.entity.setBrightness(VirtualDisplayEntity.loadBrightnessFromConfig(config));
    }

    @Override
    public void onDetached() {
        this.entity = null;
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return this.entity != null && this.entity.containsEntityId(entityId);
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
}

