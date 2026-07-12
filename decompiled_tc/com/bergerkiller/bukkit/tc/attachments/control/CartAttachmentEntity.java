/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.wrappers.BoatWoodType
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.vehicle.boat.BoatHandle
 *  org.bukkit.Material
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BoatWoodType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetToggleButton;
import com.bergerkiller.bukkit.tc.attachments.ui.entity.MapWidgetEntityTypeList;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.boat.BoatHandle;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentEntity
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "ENTITY";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            EntityType type = (EntityType)config.get("entityType", (Object)EntityType.MINECART);
            if (type == EntityType.BOAT) {
                Material itemMaterial;
                BoatWoodType boatWoodType;
                BoatWoodType boatWoodType2 = boatWoodType = config.contains("boatWoodType") ? (BoatWoodType)config.get("boatWoodType", (Object)BoatWoodType.OAK) : BoatWoodType.OAK;
                if (boatWoodType == BoatWoodType.OAK) {
                    itemMaterial = MaterialUtil.getMaterial((String)"LEGACY_BOAT");
                } else {
                    itemMaterial = MaterialUtil.getMaterial((String)("LEGACY_BOAT_" + boatWoodType.name()));
                    if (itemMaterial == null) {
                        itemMaterial = MaterialUtil.getMaterial((String)"LEGACY_BOAT");
                    }
                }
                return TCConfig.resourcePack.getItemTexture(new ItemStack(itemMaterial), 16, 16);
            }
            if (type == EntityType.MINECART) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_MINECART")), 16, 16);
            }
            if (type == EntityType.MINECART_CHEST) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_STORAGE_MINECART")), 16, 16);
            }
            if (type == EntityType.MINECART_COMMAND) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_COMMAND_MINECART")), 16, 16);
            }
            if (type == EntityType.MINECART_FURNACE) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_POWERED_MINECART")), 16, 16);
            }
            if (type == EntityType.MINECART_HOPPER) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_HOPPER_MINECART")), 16, 16);
            }
            if (type == EntityType.MINECART_MOB_SPAWNER) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_MOB_SPAWNER")), 16, 16);
            }
            if (type == EntityType.MINECART_TNT) {
                return TCConfig.resourcePack.getItemTexture(new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_EXPLOSIVE_MINECART")), 16, 16);
            }
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/mob.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentEntity();
        }

        @Override
        public void getDefaultConfig(ConfigurationNode config) {
            config.set("entityType", (Object)EntityType.MINECART);
        }

        @Override
        public void createAppearanceTab(MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            final MapWidget boatTypeSelector = (tab.addWidget((MapWidget)new MapWidgetSelectionBox(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    for (BoatWoodType type : BoatWoodType.values()) {
                        this.addItem(type.name());
                    }
                    if (attachment.getConfig().contains("boatWoodType")) {
                        this.setSelectedItem((String)attachment.getConfig().get("boatWoodType", (Object)"OAK"));
                    } else {
                        this.setSelectedItem("OAK");
                    }
                }

                @Override
                public void onSelectedItemChanged() {
                    if (this.isVisible()) {
                        attachment.getConfig().set("boatWoodType", (Object)this.getSelectedItem());
                        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                        attachment.resetIcon();
                    }
                }
            })).setBounds(0, 15, 100, 12).setVisible(false);
            (tab.addWidget((MapWidget)new MapWidgetEntityTypeList(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setEntityType((EntityType)attachment.getConfig().get("entityType", (Object)EntityType.MINECART));
                    boatTypeSelector.setVisible(this.getEntityType() == EntityType.BOAT);
                }

                @Override
                public void onEntityTypeChanged() {
                    attachment.getConfig().set("entityType", (Object)this.getEntityType());
                    boatTypeSelector.setVisible(this.getEntityType() == EntityType.BOAT);
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    attachment.resetIcon();
                }
            })).setBounds(0, 1, 100, 12);
            (tab.addWidget((MapWidget)new MapWidgetToggleButton<Boolean>(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onSelectionChanged() {
                    attachment.getConfig().set("sitting", this.getSelectedOption());
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    attachment.resetIcon();
                    this.display.playSound(SoundEffect.CLICK);
                }
            })).addOptions(b -> "Sitting: " + (b != false ? "YES" : "NO"), Boolean.TRUE, Boolean.FALSE).setSelectedOption((Boolean)attachment.getConfig().getOrDefault("sitting", (Object)false)).setBounds(0, 56, 102, 12);
            (tab.addWidget((MapWidget)new MapWidgetButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                private void refreshText() {
                    ConfigurationNode nametag;
                    if (attachment.getConfig().contains("nametag") && ((Boolean)(nametag = attachment.getConfig().getNode("nametag")).get("used", (Object)true)).booleanValue()) {
                        if (((Boolean)nametag.get("visible", (Object)true)).booleanValue()) {
                            this.setText("Nametag (vis.)");
                        } else {
                            this.setText("Nametag (invis.)");
                        }
                        return;
                    }
                    this.setText("No Nametag");
                }

                public void onAttached() {
                    this.refreshText();
                }

                public void onActivate() {
                    ConfigurationNode nametag = attachment.getConfig().getNode("nametag");
                    if (((Boolean)nametag.get("used", (Object)true)).booleanValue()) {
                        if (((Boolean)nametag.get("visible", (Object)true)).booleanValue()) {
                            nametag.set("visible", (Object)false);
                        } else {
                            nametag.set("used", (Object)false);
                        }
                    } else {
                        nametag.set("used", (Object)true);
                        nametag.set("visible", (Object)true);
                        if (!nametag.contains("text")) {
                            nametag.set("text", (Object)"Nametag");
                        }
                    }
                    this.refreshText();
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                }
            })).setBounds(0, 69, 79, 12);
            final MapWidgetSubmitText nameTagTextBox = (MapWidgetSubmitText)tab.addWidget((MapWidget)new MapWidgetSubmitText(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setDescription("Enter nametag title");
                }

                public void onAccept(String text) {
                    ConfigurationNode nametag = attachment.getConfig().getNode("nametag");
                    nametag.set("used", (Object)true);
                    nametag.set("text", (Object)text);
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                }
            });
            (tab.addWidget((MapWidget)new MapWidgetButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onActivate() {
                    nameTagTextBox.activate();
                }
            })).setText("Edit").setBounds(80, 69, 22, 12);
        }
    };
    private VirtualEntity actual;
    private VirtualEntity entity;

    private VirtualEntity actualEntity() {
        return this.actual != null ? this.actual : this.entity;
    }

    @Override
    public void onDetached() {
        super.onDetached();
        this.entity = null;
        this.actual = null;
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        if (!super.checkCanReload(config)) {
            return false;
        }
        VirtualEntity displayed = this.actualEntity();
        EntityType entityType = (EntityType)config.getOrDefault("entityType", (Object)EntityType.MINECART);
        if (displayed.getEntityType() != entityType) {
            return false;
        }
        boolean currSitting = this.actual != null;
        boolean newSitting = (Boolean)config.getOrDefault("sitting", (Object)false);
        return newSitting == currSitting || entityType.name().equals("SHULKER");
    }

    @Override
    public void onAttached() {
        super.onAttached();
        EntityType entityType = (EntityType)this.getConfig().getOrDefault("entityType", (Object)EntityType.MINECART);
        boolean sitting = (Boolean)this.getConfig().getOrDefault("sitting", (Object)false);
        if (!CartAttachmentEntity.isEntityTypeSupported(entityType)) {
            entityType = EntityType.MINECART;
        }
        if (this.getParent() != null || !VirtualEntity.isMinecart(entityType) || !this.hasController()) {
            this.entity = new VirtualEntity(this.getManager());
        } else {
            CommonEntity entity = this.getController().getMember().getEntity();
            this.entity = new VirtualEntity(this.getManager(), entity.getEntityId(), entity.getUniqueId());
            this.entity.setUseParentMetadata(true);
            this.entity.setRespawnOnPitchFlip(true);
        }
        this.entity.setEntityType(entityType);
        if (this.entity.isMinecart() && !this.entity.isExperimentalMinecart()) {
            double MINECART_CENTER_Y = 0.3765;
            this.entity.setPosition(new Vector(0.0, 0.3765, 0.0));
            this.entity.setRelativeOffset(0.0, -0.3765, 0.0);
        }
        if (sitting || entityType.name().equals("SHULKER")) {
            this.actual = this.entity;
            this.entity = new VirtualEntity(this.getManager());
            this.entity.setEntityType(EntityType.ARMOR_STAND);
            this.entity.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            this.entity.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
            this.entity.getMetaData().set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
        }
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        VirtualEntity displayed = this.actualEntity();
        if (config.isNode("nametag") && ((Boolean)config.get("nametag.used", (Object)true)).booleanValue()) {
            ConfigurationNode nametag = config.getNode("nametag");
            boolean visible = (Boolean)nametag.get("visible", (Object)true);
            String text = (String)nametag.get("text", (Object)"");
            displayed.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME, (Object)ChatText.fromMessage((String)text));
            displayed.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, (Object)visible);
        } else {
            displayed.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME, (Object)(Common.evaluateMCVersion((String)">=", (String)"1.13") ? null : ChatText.empty()));
        }
        if (displayed.getEntityType() == EntityType.BOAT) {
            displayed.getMetaData().set(BoatHandle.DATA_WOOD_TYPE, (Object)((BoatWoodType)config.get("boatWoodType", (Object)BoatWoodType.OAK)));
        }
    }

    @Override
    public void onFocus() {
        this.actualEntity().setGlowColor(HelperMethods.getFocusGlowColor(this));
    }

    @Override
    public void onBlur() {
        this.actualEntity().setGlowColor(null);
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return this.entity != null && this.entity.getEntityId() == entityId || this.actual != null && this.actual.getEntityId() == entityId;
    }

    @Override
    public int getMountEntityId() {
        if (this.entity.isMountable()) {
            return this.entity.getEntityId();
        }
        return -1;
    }

    @Override
    public void applyPassengerSeatTransform(Matrix4x4 transform) {
        VirtualEntity displayed = this.actualEntity();
        if (displayed.isMinecart()) {
            transform.translate(0.0, displayed.getMountOffset(), 0.0);
            return;
        }
        Matrix4x4 relativeMatrix = new Matrix4x4();
        relativeMatrix.translate(0.0, displayed.getMountOffset(), 0.0);
        Matrix4x4.multiply((Matrix4x4)relativeMatrix, (Matrix4x4)transform, (Matrix4x4)transform);
    }

    public boolean isMinecartInterpolation() {
        return this.actual == null && this.entity.isMinecart();
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
        if (this.actual != null) {
            this.actual.spawn(viewer, new Vector());
        }
        this.entity.spawn(viewer, new Vector());
        if (this.actual != null) {
            viewer.getVehicleMountController().mount(this.entity.getEntityId(), this.actual.getEntityId());
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.actual != null) {
            this.actual.destroy(viewer);
        }
        this.entity.destroy(viewer);
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.entity.updatePosition(transform);
        if (this.actual != null) {
            this.actual.updatePosition(transform);
        }
    }

    @Override
    public void onMove(boolean absolute) {
        this.entity.syncPosition(absolute);
        if (this.actual != null) {
            if (this.actual.syncPositionIfMounted()) {
                this.actual.syncPosition(absolute);
            } else {
                this.actual.syncPositionSilent();
            }
        }
    }

    @Override
    public void onTick() {
    }

    public static boolean isEntityTypeSupported(EntityType entityType) {
        String name = entityType.name();
        if (name.equals("WEATHER") || name.equals("COMPLEX_PART")) {
            return false;
        }
        switch (entityType) {
            case PAINTING: 
            case FISHING_HOOK: 
            case LIGHTNING: 
            case PLAYER: 
            case EXPERIENCE_ORB: 
            case UNKNOWN: {
                return false;
            }
        }
        if (VirtualEntity.isLivingEntity(entityType)) {
            return ClientboundAddMobPacketHandle.isEntityTypeSupported((EntityType)entityType);
        }
        return ClientboundAddEntityPacketHandle.isEntityTypeSupported((EntityType)entityType);
    }
}

