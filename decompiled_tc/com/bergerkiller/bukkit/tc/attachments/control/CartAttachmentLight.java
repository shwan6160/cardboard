/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIController;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentLight
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){
        private int numRegistries = 0;

        @Override
        public String getID() {
            return "LIGHT";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/light.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentLight();
        }

        @Override
        public void getDefaultConfig(ConfigurationNode config) {
            config.set("lightType", (Object)"BLOCK");
            config.set("lightLevel", (Object)15);
        }

        @Override
        public void onRegister(AttachmentTypeRegistry registry) {
            ++this.numRegistries;
        }

        @Override
        public void onUnregister(AttachmentTypeRegistry registry) {
            if (--this.numRegistries <= 0) {
                LightAPIController.disable();
            }
        }

        @Override
        public void createAppearanceTab(MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            (tab.addWidget((MapWidget)new MapWidgetButton(this){
                private boolean skylight = false;
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    super.onAttached();
                    this.skylight = ((String)attachment.getConfig().get("lightType", (Object)"BLOCK")).equalsIgnoreCase("SKY");
                    this.updateText();
                }

                private void updateText() {
                    this.setText("Type: " + (this.skylight ? "SKY" : "BLOCK"));
                }

                public void onActivate() {
                    this.skylight = !this.skylight;
                    this.updateText();
                    attachment.getConfig().set("lightType", (Object)(this.skylight ? "SKY" : "BLOCK"));
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    attachment.resetIcon();
                    this.display.playSound(SoundEffect.CLICK);
                }
            })).setBounds(7, 10, 86, 16);
            (tab.addWidget((MapWidget)new MapWidgetNumberBox(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setRange(1.0, 15.0);
                    this.setIncrement(1.0);
                    this.setValue(((Integer)attachment.getConfig().get("lightLevel", (Object)15)).intValue());
                }

                @Override
                public String getValueText() {
                    return "Level: " + Integer.toString((int)this.getValue());
                }

                @Override
                public void onValueChanged() {
                    attachment.getConfig().set("lightLevel", (Object)((int)this.getValue()));
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                }
            })).setBounds(0, 30, 100, 16);
            (tab.addWidget((MapWidget)new MapWidgetText(){

                public void onAttached() {
                    super.onAttached();
                    this.setText("Powered by LightAPI");
                    this.setColor(MapColorPalette.getColor((int)0, (int)1, (int)79));
                    this.setShadowColor(MapColorPalette.getColor((int)0, (int)0, (int)220));
                }
            })).setBounds(0, 74, 100, 10);
        }
    };
    private IntVector3 prev_block = null;
    private LightAPIController controller = null;
    private int lightLevel = 15;
    private boolean lightVisible = true;

    @Override
    public void onAttached() {
        boolean isSky = ((String)this.getConfig().get("lightType", (Object)"BLOCK")).equalsIgnoreCase("SKY");
        this.controller = LightAPIController.get(this.getManager().getWorld(), isSky);
        this.lightLevel = (Integer)this.getConfig().get("lightLevel", (Object)15);
        this.lightVisible = !HelperMethods.hasInactiveParent(this);
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        boolean isSky = ((String)this.getConfig().get("lightType", (Object)"BLOCK")).equalsIgnoreCase("SKY");
        return this.controller == LightAPIController.get(this.getManager().getWorld(), isSky);
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        super.onLoad(config);
        int newLightLevel = (Integer)this.getConfig().get("lightLevel", (Object)15);
        if (newLightLevel != this.lightLevel) {
            if (this.lightVisible && this.prev_block != null) {
                this.controller.update(this.prev_block, this.lightLevel, newLightLevel);
            }
            this.lightLevel = newLightLevel;
        }
    }

    @Override
    public void onDetached() {
        if (this.prev_block != null) {
            this.controller.remove(this.prev_block, this.lightLevel);
            this.prev_block = null;
        }
    }

    @Override
    public void makeVisible(Player viewer) {
    }

    @Override
    public void makeHidden(Player viewer) {
    }

    @Override
    public void onActiveChanged(boolean active) {
        this.lightVisible = active;
        if (!active && this.prev_block != null) {
            this.controller.remove(this.prev_block, this.lightLevel);
            this.prev_block = null;
        }
    }

    @Override
    public void onTick() {
        Vector pos_d = this.getTransform().toVector();
        IntVector3 pos = new IntVector3(pos_d.getX(), pos_d.getY(), pos_d.getZ());
        if (this.lightVisible) {
            if (this.prev_block == null) {
                this.controller.add(pos, this.lightLevel);
                this.prev_block = pos;
            } else if (!pos.equals((Object)this.prev_block)) {
                this.controller.move(this.prev_block, pos, this.lightLevel);
                this.prev_block = pos;
            }
        } else if (this.prev_block != null) {
            this.controller.remove(this.prev_block, this.lightLevel);
            this.prev_block = null;
        }
    }

    @Override
    public void onMove(boolean absolute) {
    }
}

