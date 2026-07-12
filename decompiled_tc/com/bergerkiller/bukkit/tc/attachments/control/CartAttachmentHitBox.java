/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.AgeableMobHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.InteractionHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob.SlimeHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSizeBox;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.AgeableMobHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.InteractionHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob.SlimeHandle;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentHitBox
extends CartAttachment {
    private static final Vector3 DEFAULT_SCALE = new Vector3(1.0, 1.0, 1.0);
    public static final AttachmentType TYPE = new BaseHitBoxType(){

        @Override
        public String getID() {
            return "HITBOX";
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentHitBox();
        }
    };
    private final OrientedBoundingBox bbox = new OrientedBoundingBox();
    private final Set<Player> nearbyViewers = new HashSet<Player>();
    private int hitboxEntityId = EntityUtil.getUniqueEntityId();
    private final UUID hitboxEntityUUID = UUID.randomUUID();
    private Box box = null;
    private double heightOffset = 0.0;
    private double minSize;
    private SizeMode sizeMode = SizeMode.SMALLEST;

    @Override
    public void onLoad(ConfigurationNode config) {
        Vector3 size = (Vector3)LogicUtil.fixNull((Object)this.getConfiguredPosition().size, (Object)DEFAULT_SCALE);
        this.bbox.setSize(new Vector(size.x, size.y, size.z));
        this.heightOffset = 0.5 * size.y;
        this.minSize = Math.min(Math.min(size.x, size.y), size.z);
        SizeMode newSizeMode = SizeMode.fromSize(this.minSize);
        if (newSizeMode != this.sizeMode && !this.nearbyViewers.isEmpty()) {
            for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                this.despawnHitBoxForViewer(viewer);
            }
            this.sizeMode = newSizeMode;
            for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                this.updateHitBoxForViewer(viewer);
            }
        } else {
            this.sizeMode = newSizeMode;
            for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                if (!viewer.supportsDisplayEntities()) continue;
                this.updateInteractionMeta(viewer);
                this.updateHitBoxForViewer(viewer);
            }
        }
    }

    @Override
    public void makeVisible(Player viewer) {
        this.makeVisible(AttachmentViewer.fallback(viewer));
    }

    @Override
    public void makeHidden(Player viewer) {
        this.makeHidden(AttachmentViewer.fallback(viewer));
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        if (this.box != null) {
            this.box.makeVisible(viewer);
        }
        this.updateHitBoxForViewer(viewer);
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.box != null) {
            this.box.makeHidden(viewer);
        }
        this.despawnHitBoxForViewer(viewer);
    }

    @Override
    public boolean containsEntityId(int id) {
        return id == this.hitboxEntityId;
    }

    private Vector getPOVLocationBottom(Player player) {
        Vector eyeDirection;
        Location eyeLoc = player.getEyeLocation();
        Vector eyePosition = eyeLoc.toVector();
        double distanceToBox = this.bbox.hitTest(eyePosition, eyeDirection = eyeLoc.getDirection());
        if (distanceToBox == Double.MAX_VALUE) {
            eyeDirection = this.bbox.getPosition().clone().subtract(eyePosition).normalize();
            if (Double.isNaN(eyeDirection.getX())) {
                return this.bbox.getPosition();
            }
            distanceToBox = this.bbox.hitTest(eyePosition, eyeDirection);
            if (distanceToBox == Double.MAX_VALUE) {
                return this.bbox.getPosition();
            }
        }
        if (distanceToBox > 6.0) {
            return null;
        }
        if (distanceToBox == 0.0) {
            return eyePosition;
        }
        return eyePosition.add(eyeDirection.multiply(distanceToBox + 0.5 * this.sizeMode.size));
    }

    public OrientedBoundingBox getBoundingBox() {
        return this.bbox;
    }

    public void setBoxColor(ChatColor color) {
        if (color != null) {
            if (this.box == null) {
                this.box = new Box(this.getManager(), this.bbox);
                this.box.entity.setGlowColor(color);
                for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                    this.box.makeVisible(viewer);
                }
            } else {
                this.box.entity.setGlowColor(color);
            }
        } else if (this.box != null) {
            this.box.entity.setGlowColor(null);
            this.box.tickLastHidden = CommonUtil.getServerTicks();
        }
    }

    @Override
    public void onFocus() {
        this.setBoxColor(HelperMethods.getFocusGlowColor(this));
    }

    @Override
    public void onBlur() {
        this.setBoxColor(null);
    }

    @Override
    public void onTick() {
        if (this.box != null && !this.isFocused() && CommonUtil.getServerTicks() - this.box.tickLastHidden > 40) {
            this.box.entity.destroyForAll();
            this.box = null;
        }
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        Quaternion orientation = transform.getRotation();
        this.bbox.setPosition(transform.toVector().add(orientation.upVector().multiply(this.heightOffset)));
        this.bbox.setOrientation(orientation);
        if (this.box != null) {
            this.box.update(this.bbox);
        }
    }

    @Override
    public void onMove(boolean absolute) {
        if (this.box != null) {
            this.box.sync();
        }
        for (AttachmentViewer viewer : this.getAttachmentViewers()) {
            this.updateHitBoxForViewer(viewer);
        }
    }

    private void updateHitBoxForViewer(AttachmentViewer viewer) {
        Vector pos = this.getPOVLocationBottom(viewer.getPlayer());
        if (pos == null) {
            this.despawnHitBoxForViewer(viewer);
            return;
        }
        boolean usesInteractionEntity = viewer.supportsDisplayEntities();
        if (usesInteractionEntity) {
            pos.setY(pos.getY() - 0.5 * this.minSize);
        } else {
            pos.setY(pos.getY() - 0.5 * this.sizeMode.size);
        }
        if (this.nearbyViewers.add(viewer.getPlayer())) {
            if (usesInteractionEntity) {
                ClientboundAddEntityPacketHandle packet = ClientboundAddEntityPacketHandle.createNew();
                packet.setEntityId(this.hitboxEntityId);
                packet.setEntityUUID(this.hitboxEntityUUID);
                packet.setEntityType(VirtualDisplayEntity.INTERACTION_ENTITY_TYPE);
                packet.setPosX(pos.getX());
                packet.setPosY(pos.getY());
                packet.setPosZ(pos.getZ());
                viewer.send((PacketHandle)packet);
                this.updateInteractionMeta(viewer);
            } else {
                ClientboundAddMobPacketHandle packet = ClientboundAddMobPacketHandle.createNew();
                packet.setEntityId(this.hitboxEntityId);
                packet.setEntityUUID(this.hitboxEntityUUID);
                packet.setEntityType(this.sizeMode.type);
                packet.setPosX(pos.getX());
                packet.setPosY(pos.getY());
                packet.setPosZ(pos.getZ());
                DataWatcher meta = new DataWatcher();
                meta.set(EntityHandle.DATA_FLAGS, (Object)-96);
                meta.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
                this.sizeMode.apply(meta);
                viewer.sendEntityLivingSpawnPacket(packet, meta);
                viewer.sendDisableCollision(this.hitboxEntityUUID);
            }
        } else {
            ClientboundEntityPositionSyncPacketHandle packet = ClientboundEntityPositionSyncPacketHandle.createNew((int)this.hitboxEntityId, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)0.0f, (float)0.0f, (boolean)false);
            viewer.send((PacketHandle)packet);
        }
    }

    private void updateInteractionMeta(AttachmentViewer viewer) {
        DataWatcher meta = new DataWatcher();
        meta.set(InteractionHandle.DATA_WIDTH, (Object)Float.valueOf((float)this.minSize));
        meta.set(InteractionHandle.DATA_HEIGHT, (Object)Float.valueOf((float)this.minSize));
        viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.hitboxEntityId, (DataWatcher)meta, (boolean)true).toCommonPacket());
    }

    private void despawnHitBoxForViewer(AttachmentViewer viewer) {
        if (this.nearbyViewers.remove(viewer.getPlayer())) {
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this.hitboxEntityId));
        }
    }

    private static class Box {
        public final VirtualBoundingBox entity;
        private int tickLastHidden = 0;

        public Box(AttachmentManager manager, OrientedBoundingBox bbox) {
            this.entity = VirtualBoundingBox.create(manager);
            this.entity.update(bbox);
        }

        public void update(OrientedBoundingBox bbox) {
            this.entity.update(bbox);
        }

        public void sync() {
            this.entity.syncPosition(true);
        }

        public void makeVisible(AttachmentViewer viewer) {
            this.entity.spawn(viewer, new Vector(0.0, 0.0, 0.0));
        }

        public void makeHidden(AttachmentViewer viewer) {
            this.entity.destroy(viewer);
        }
    }

    private static enum SizeMode {
        SLIME_SZ8(8),
        SLIME_SZ7(7),
        SLIME_SZ6(6),
        SLIME_SZ5(5),
        SLIME_SZ4(4),
        SLIME_SZ3(3),
        SLIME_SZ2(2),
        PIG(0.9, EntityType.PIG, false),
        SLIME_SZ1(1),
        BABY_PIG(0.45, EntityType.PIG, true),
        RABBIT(0.4, EntityType.RABBIT, false),
        BABY_RABBIT(0.2, EntityType.RABBIT, true);

        public final double size;
        public final EntityType type;
        public final boolean baby;
        public final int slimeSize;
        public static final SizeMode SMALLEST;

        private SizeMode(int slimeSize) {
            this.size = 2.04f * (0.255f * (float)slimeSize);
            this.type = EntityType.SLIME;
            this.baby = false;
            this.slimeSize = slimeSize;
        }

        private SizeMode(double size, EntityType type, boolean baby) {
            this.size = size;
            this.type = type;
            this.baby = baby;
            this.slimeSize = 0;
        }

        public void apply(DataWatcher datawatcher) {
            if (this.baby) {
                datawatcher.set(AgeableMobHandle.DATA_IS_BABY, (Object)true);
            } else if (this.slimeSize != 0) {
                datawatcher.set(SlimeHandle.DATA_SIZE, (Object)this.slimeSize);
            }
        }

        public static SizeMode fromSize(double minSize) {
            for (SizeMode mode : SizeMode.values()) {
                if (!(mode.size <= minSize)) continue;
                return mode;
            }
            return SMALLEST;
        }

        static {
            SMALLEST = SizeMode.values()[SizeMode.values().length - 1];
        }
    }

    protected static abstract class BaseHitBoxType
    implements AttachmentType {
        protected BaseHitBoxType() {
        }

        @Override
        public double getSortPriority() {
            return 1.0;
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/hitbox.png");
        }

        @Override
        public void migrateConfiguration(ConfigurationNode config) {
            if (config.isNode("size")) {
                ConfigurationNode size = config.getNode("size");
                config.set("position.sizeX", size.get("x", (Object)1.0));
                config.set("position.sizeY", size.get("y", (Object)1.0));
                config.set("position.sizeZ", size.get("z", (Object)1.0));
                size.remove();
            }
        }

        @Override
        public void createPositionMenu(PositionMenu.Builder builder) {
            builder.addRow(menu -> new MapWidgetSizeBox(this, (PositionMenu)((Object)menu)){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ BaseHitBoxType this$0;
                {
                    this.val$menu = positionMenu;
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    super.onAttached();
                    this.setSize(this.val$menu.getPositionConfigValue("sizeX", DEFAULT_SCALE.x), this.val$menu.getPositionConfigValue("sizeY", DEFAULT_SCALE.y), this.val$menu.getPositionConfigValue("sizeZ", DEFAULT_SCALE.z));
                }

                @Override
                public void onSizeChanged() {
                    this.val$menu.updatePositionConfig(config -> {
                        config.set("sizeX", (Object)this.x.getValue());
                        config.set("sizeY", (Object)this.y.getValue());
                        config.set("sizeZ", (Object)this.z.getValue());
                    });
                }
            }.setBounds(25, 0, menu.getSliderWidth(), 35)).addLabel(0, 3, "Size X").addLabel(0, 15, "Size Y").addLabel(0, 27, "Size Z").setSpacingAbove(3);
        }
    }
}

