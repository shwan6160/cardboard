/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayBlockEntity;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.schematic.MovingSchematic;
import com.bergerkiller.bukkit.tc.attachments.control.schematic.WorldEditSchematicLoader;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualDisplayBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSizeBox;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentSchematic
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "WE_SCHEMATIC";
        }

        @Override
        public String getName() {
            return "SCHEMATIC";
        }

        @Override
        public boolean isListed(Player player) {
            return TrainCarts.plugin.getWorldEditSchematicLoader().isEnabled() && this.hasPermission(player);
        }

        @Override
        public boolean hasPermission(Player player) {
            return Permission.USE_SCHEMATIC_ATTACHMENTS.has((CommandSender)player);
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/schematic.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentSchematic();
        }

        @Override
        public void createAppearanceTab(final MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            final MapWidgetSubmitText textBox = new MapWidgetSubmitText(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setDescription("Enter schematic");
                }

                public void onAccept(String text) {
                    attachment.getConfig().set("schematic", (Object)text.trim());
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", attachment);
                    attachment.resetIcon();
                    for (MapWidget widget : tab.getWidgets()) {
                        class SchematicButton
                        extends MapWidgetButton {
                            final /* synthetic */ MapWidgetAttachmentNode val$attachment;
                            final /* synthetic */ 1 this$0;

                            SchematicButton() {
                                this.val$attachment = mapWidgetAttachmentNode;
                                this.this$0 = this$0;
                            }

                            public void onAttached() {
                                this.updateText();
                            }

                            public void updateText() {
                                String schematicName = (String)this.val$attachment.getConfig().get("schematic", (Object)"");
                                if (schematicName.isEmpty()) {
                                    this.setText("<No Schematic>");
                                } else {
                                    this.setText(schematicName);
                                }
                            }
                        }
                        if (!(widget instanceof SchematicButton)) continue;
                        ((SchematicButton)widget).updateText();
                        break;
                    }
                }
            };
            tab.addWidget((MapWidget)textBox);
            (tab.addWidget((MapWidget)new SchematicButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                    super(this$0, mapWidgetAttachmentNode);
                }

                public void onActivate() {
                    textBox.activate();
                }
            })).setBounds(0, 5, 100, 13);
        }

        @Override
        public void createPositionMenu(PositionMenu.Builder builder) {
            builder.addRow(menu -> new MapWidgetButton(this, (PositionMenu)((Object)menu)){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ 1 this$0;
                {
                    this.val$menu = positionMenu;
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    super.onAttached();
                    this.updateText(this.val$menu.getPositionConfigValue("clipEnabled", true));
                }

                public void onActivate() {
                    boolean enabled = this.val$menu.getPositionConfigValue("clipEnabled", true) == false;
                    this.val$menu.updatePositionConfig(config -> {
                        if (enabled) {
                            config.remove("clipEnabled");
                        } else {
                            config.set("clipEnabled", (Object)false);
                        }
                    });
                    this.updateText(enabled);
                }

                private void updateText(boolean enabled) {
                    this.setText(enabled ? "Enabled" : "Disabled");
                }
            }.setBounds(32, 0, 72, 11)).addLabel(0, 3, "Clipping").setSpacingAbove(3);
            builder.addPositionSlider("originX", "Origin X", "Schematic Origin X-Coordinate", 0.0).setSpacingAbove(3);
            builder.addPositionSlider("originY", "Origin Y", "Schematic Origin Y-Coordinate", 0.0);
            builder.addPositionSlider("originZ", "Origin Z", "Schematic Origin Z-Coordinate", 0.0);
            builder.addRow(menu -> new MapWidgetSizeBox(this, (PositionMenu)((Object)menu)){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ 1 this$0;
                {
                    this.val$menu = positionMenu;
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    super.onAttached();
                    this.setInitialSize(this.val$menu.getPositionConfigValue("spacingX", 0.0), this.val$menu.getPositionConfigValue("spacingY", 0.0), this.val$menu.getPositionConfigValue("spacingZ", 0.0));
                }

                @Override
                public void onSizeChanged() {
                    this.val$menu.updatePositionConfig(config -> {
                        if (this.x.getValue() == 0.0 && this.y.getValue() == 0.0 && this.z.getValue() == 0.0) {
                            config.remove("spacingX");
                            config.remove("spacingY");
                            config.remove("spacingZ");
                        } else {
                            config.set("spacingX", (Object)this.x.getValue());
                            config.set("spacingY", (Object)this.y.getValue());
                            config.set("spacingZ", (Object)this.z.getValue());
                        }
                    });
                }
            }.setRangeAndDefault(true, 0.0).setBounds(25, 0, menu.getSliderWidth(), 35)).addLabel(0, 3, "Gap X").addLabel(0, 15, "Gap Y").addLabel(0, 27, "Gap Z").setSpacingAbove(3);
            builder.addSizeBox();
        }
    };
    private WorldEditSchematicLoader.SchematicReader schematicReader;
    private MovingSchematic schematic;
    private DebugDisplay debug;

    @Override
    public void onAttached() {
        this.schematic = new MovingSchematic(this.getManager());
        this.schematicReader = TrainCarts.plugin.getWorldEditSchematicLoader().startReading((String)this.getConfig().get("schematic", (Object)""));
        this.loadNextBlocks();
    }

    @Override
    public void onDetached() {
        this.schematic = null;
        this.schematicReader.abort();
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        if (!super.checkCanReload(config)) {
            return false;
        }
        return this.schematicReader.fileName().equals(config.get("schematic", (Object)""));
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        this.schematic.setScale(this.getConfiguredPosition().size);
        this.schematic.setHasClipping((Boolean)config.getOrDefault("position.clipEnabled", (Object)true));
        this.schematic.setSpacing(new Vector(((Double)config.getOrDefault("position.spacingX", (Object)0.0)).doubleValue(), ((Double)config.getOrDefault("position.spacingY", (Object)0.0)).doubleValue(), ((Double)config.getOrDefault("position.spacingZ", (Object)0.0)).doubleValue()));
        this.schematic.setOrigin(new Vector(((Double)config.getOrDefault("position.originX", (Object)0.0)).doubleValue(), ((Double)config.getOrDefault("position.originY", (Object)0.0)).doubleValue(), ((Double)config.getOrDefault("position.originZ", (Object)0.0)).doubleValue()));
    }

    private void loadNextBlocks() {
        if (this.schematicReader.isDone()) {
            return;
        }
        WorldEditSchematicLoader.SchematicBlock block = this.schematicReader.next();
        if (block != null) {
            this.schematic.setBlockBounds(block.schematic.dimensions);
            double originX = 0.5 * (double)block.schematic.dimensions.x;
            double originY = 0.0;
            double originZ = 0.5 * (double)block.schematic.dimensions.z;
            do {
                this.schematic.addBlock((double)block.x - originX, (double)block.y - originY, (double)block.z - originZ, block.blockData);
            } while ((block = this.schematicReader.next()) != null);
            this.schematic.resendMounts();
        }
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
        this.schematic.spawn(viewer, new Vector(0.0, 0.0, 0.0));
        if (this.debug != null) {
            this.debug.makeVisible(viewer);
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        this.schematic.destroy(viewer);
        if (this.debug != null) {
            this.debug.makeHidden(viewer);
        }
    }

    @Override
    public void onFocus() {
        if (this.debug == null) {
            this.debug = new DebugDisplay();
            for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                this.debug.makeVisible(viewer);
            }
        } else {
            this.debug.focus();
        }
    }

    @Override
    public void onBlur() {
        if (this.debug != null) {
            this.debug.blur();
        }
    }

    @Override
    public void onTick() {
        this.loadNextBlocks();
        if (this.debug != null) {
            this.debug.ticksShown++;
            if (this.debug.ticksShown == 2) {
                this.debug.setGlowColor(HelperMethods.getFocusGlowColor(this));
            } else if (this.debug.ticksShown >= 40) {
                this.debug.hideForAll();
                this.debug = null;
            }
        }
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.schematic.updatePosition(transform);
        if (this.debug != null) {
            this.debug.updatePosition();
        }
    }

    @Override
    public void onMove(boolean absolute) {
        this.schematic.syncPosition(absolute);
        if (this.debug != null) {
            this.debug.syncPosition(absolute);
        }
    }

    private class DebugDisplay {
        private VirtualDisplayBoundingBox bbox;
        private VirtualDisplayBlockEntity originPoint;
        private int ticksShown = 0;

        public DebugDisplay() {
            this.bbox = new VirtualDisplayBoundingBox(CartAttachmentSchematic.this.getManager());
            this.bbox.update(CartAttachmentSchematic.this.schematic.createBBOX());
            this.bbox.setGlowColor(null);
            if (CartAttachmentSchematic.this.schematic.hasOrigin()) {
                this.initOriginPoint();
            }
        }

        public void makeVisible(AttachmentViewer viewer) {
            this.bbox.spawn(viewer, new Vector(0.0, 0.0, 0.0));
            if (this.originPoint != null) {
                this.originPoint.spawn(viewer, new Vector(0.0, 0.0, 0.0));
            }
        }

        public void makeHidden(AttachmentViewer viewer) {
            this.bbox.destroy(viewer);
            if (this.originPoint != null) {
                this.originPoint.destroy(viewer);
            }
        }

        public void hideForAll() {
            this.bbox.destroyForAll();
            if (this.originPoint != null) {
                this.originPoint.destroyForAll();
            }
        }

        public void updatePosition() {
            this.bbox.update(CartAttachmentSchematic.this.schematic.createBBOX());
            if (CartAttachmentSchematic.this.schematic.hasOrigin()) {
                if (this.originPoint == null) {
                    this.initOriginPoint();
                    for (AttachmentViewer viewer : CartAttachmentSchematic.this.getAttachmentViewers()) {
                        this.originPoint.spawn(viewer, new Vector(0.0, 0.0, 0.0));
                    }
                } else {
                    this.originPoint.updatePosition(CartAttachmentSchematic.this.schematic.createOriginPointTransform());
                }
            } else if (this.originPoint != null) {
                this.originPoint.destroyForAll();
                this.originPoint = null;
            }
        }

        private void initOriginPoint() {
            this.originPoint = new VirtualDisplayBlockEntity(CartAttachmentSchematic.this.getManager());
            this.originPoint.setBlockData(BlockData.fromMaterial((Material)MaterialUtil.getMaterial((String)"REDSTONE_BLOCK")));
            this.originPoint.setScale(new Vector(0.1, 0.1, 0.1));
            this.originPoint.setGlowColor(ChatColor.RED);
            this.originPoint.updatePosition(CartAttachmentSchematic.this.schematic.createOriginPointTransform());
            this.originPoint.syncPosition(true);
        }

        public void syncPosition(boolean absolute) {
            this.bbox.syncPosition(absolute);
            if (this.originPoint != null) {
                this.originPoint.syncPosition(absolute);
            }
        }

        public void focus() {
            this.setGlowColor(null);
            this.ticksShown = 0;
        }

        public void blur() {
            this.setGlowColor(null);
            this.ticksShown = 20;
        }

        public void setGlowColor(ChatColor color) {
            this.bbox.setGlowColor(color);
        }
    }
}

