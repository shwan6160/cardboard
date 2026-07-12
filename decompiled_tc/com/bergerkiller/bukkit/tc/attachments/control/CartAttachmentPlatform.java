/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentPlatformPlane;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentPlatformShulker;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSizeBox;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CartAttachmentPlatform
extends CartAttachment {
    private static final boolean ENABLE_PLANE_MODE = false;
    protected static final Vector3 DEFAULT_SIZE = new Vector3(1.0, 1.0, 1.0);
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "PLATFORM";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/platform.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            if (CartAttachmentPlatform.readPlatformMode(config) == PlatformMode.PLANE) {
                return new CartAttachmentPlatformPlane();
            }
            return new CartAttachmentPlatformShulker();
        }

        @Override
        public void createAppearanceTab(MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            ((MapWidgetText)tab.addWidget((MapWidget)new MapWidgetText())).setText("Shulker Color").setFont(MapFont.MINECRAFT).setColor((byte)18).setBounds(15, 6, 50, 11);
            MapWidget boatTypeSelector = (tab.addWidget((MapWidget)new MapWidgetSelectionBox(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.addItem(Color.DEFAULT.name());
                    for (Color color : Color.values()) {
                        if (color == Color.DEFAULT) continue;
                        this.addItem(color.name());
                    }
                    this.setSelectedItem(((Color)((Object)attachment.getConfig().getOrDefault("shulkerColor", (Object)Color.DEFAULT))).name());
                }

                @Override
                public void onSelectedItemChanged() {
                    attachment.getConfig().set("shulkerColor", (Object)this.getSelectedItem());
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                }
            })).setBounds(0, 15, 100, 12);
        }

        @Override
        public void createPositionMenu(PositionMenu.Builder builder) {
        }

        private /* synthetic */ MapWidget lambda$createPositionMenu$0(final PositionMenu menu) {
            return new MapWidgetSizeBox(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    super.onAttached();
                    ConfigurationNode positionConfig = menu.getPositionConfig();
                    this.setInitialSize((Double)positionConfig.getOrDefault("sizeX", (Object)CartAttachmentPlatform.DEFAULT_SIZE.x), (Double)positionConfig.getOrDefault("sizeY", (Object)CartAttachmentPlatform.DEFAULT_SIZE.y), (Double)positionConfig.getOrDefault("sizeZ", (Object)CartAttachmentPlatform.DEFAULT_SIZE.z));
                    if (CartAttachmentPlatform.readPlatformMode(menu.getConfig()) != PlatformMode.SHULKER) {
                        this.setTextOverride(null);
                    } else {
                        this.setTextOverride("Shulker");
                    }
                }

                @Override
                public void onSizeChanged() {
                    this.setTextOverride(null);
                    menu.updateConfig(config -> config.set("platformMode", (Object)PlatformMode.PLANE));
                    menu.updatePositionConfig(config -> {
                        config.set("sizeX", (Object)this.x.getValue());
                        config.set("sizeY", (Object)this.y.getValue());
                        config.set("sizeZ", (Object)this.z.getValue());
                    });
                }

                @Override
                public void onUniformResetValue() {
                    this.setTextOverride("Shulker");
                    menu.updateConfig(config -> config.remove("platformMode"));
                    menu.updatePositionConfig(config -> {
                        config.remove("sizeX");
                        config.remove("sizeY");
                        config.remove("sizeZ");
                    });
                }
            }.setYAxisEnabled(false).setBounds(25, 0, menu.getSliderWidth(), 24);
        }
    };

    protected static PlatformMode readPlatformMode(ConfigurationNode config) {
        return PlatformMode.SHULKER;
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
    public void onTick() {
    }

    public static enum PlatformMode {
        SHULKER,
        PLANE;

    }

    public static enum Color {
        WHITE,
        ORANGE,
        MAGENTA,
        LIGHT_BLUE,
        YELLOW,
        LIME,
        PINK,
        GRAY,
        LIGHT_GRAY,
        CYAN,
        PURPLE,
        BLUE,
        BROWN,
        GREEN,
        RED,
        BLACK,
        DEFAULT;

    }
}

