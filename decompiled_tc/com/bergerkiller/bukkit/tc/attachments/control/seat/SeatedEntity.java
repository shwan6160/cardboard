/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentAnchor;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatOrientation;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityElytra;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityHead;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityInvisible;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityNormal;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityStanding;
import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHider;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import java.util.function.Function;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class SeatedEntity {
    protected Entity entity = null;
    protected int tickEntered = -1;
    protected boolean showDummy = false;
    protected DisplayMode displayMode = DisplayMode.DEFAULT;
    protected final CartAttachmentSeat seat;
    public final SeatOrientation orientation = new SeatOrientation();
    private VirtualEntity fakeMount = null;
    public int parentMountId = -1;
    private boolean madeVisibleInFirstPerson = false;
    private TabNameTagHider.TabPlayerNameTagHider tabNameTagHider = null;

    public SeatedEntity(CartAttachmentSeat seat) {
        this.seat = seat;
    }

    public boolean isEmpty() {
        return this.entity == null;
    }

    public boolean isDisplayed() {
        return this.entity != null || this.showDummy;
    }

    public boolean isPlayer() {
        return this.entity instanceof Player;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public int getTicksInSeat() {
        return this.tickEntered == -1 ? 0 : CommonUtil.getServerTicks() - this.tickEntered;
    }

    public boolean isDummyPlayer() {
        return this.showDummy;
    }

    public boolean isDummyPlayerDisplayed() {
        return this.showDummy && this.entity == null;
    }

    public final void setShowDummyPlayer(boolean show) {
        if (this.showDummy != show) {
            this.showDummy = show;
            if (this.entity == null) {
                this.updateMode(true);
            }
        }
    }

    public final void setEntity(Entity entity) {
        if (this.tabNameTagHider != null) {
            this.tabNameTagHider.show();
            this.tabNameTagHider = null;
        }
        if (this.entity != entity) {
            this.tickEntered = entity == null ? -1 : CommonUtil.getServerTicks();
        }
        this.entity = entity;
        if (entity instanceof Player && this.getDisplayMode() == DisplayMode.NO_NAMETAG) {
            this.tabNameTagHider = this.seat.getPlugin().getTabNameHider((Player)entity);
            if (this.tabNameTagHider != null) {
                this.tabNameTagHider.hide();
            }
        }
        this.updateMode(true);
    }

    public DisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    protected void hideRealPlayer(AttachmentViewer viewer) {
        if (this.entity == viewer.getPlayer()) {
            FirstPersonView.setPlayerVisible(viewer, false);
        } else {
            viewer.getVehicleMountController().despawn(this.entity.getEntityId());
        }
    }

    protected void showRealPlayer(AttachmentViewer viewer) {
        if (viewer.getPlayer() == this.entity) {
            FirstPersonView.setPlayerVisible(viewer, true);
        } else {
            VehicleMountController vmc = viewer.getVehicleMountController();
            vmc.respawn((Entity)((Player)this.entity), (theViewer, thePlayer) -> FakePlayerSpawner.NORMAL.spawnPlayer(theViewer, (Player)thePlayer, thePlayer.getEntityId(), FakePlayerSpawner.FakePlayerPosition.ofPlayer(thePlayer), meta -> {}));
        }
    }

    public void resetMetadata(AttachmentViewer viewer) {
        DataWatcher metaTmp = EntityHandle.fromBukkit((Entity)this.entity).getDataWatcher();
        viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entity.getEntityId(), (DataWatcher)metaTmp, (boolean)true).toCommonPacket());
    }

    protected PassengerPose getCurrentHeadRotation(Matrix4x4 transform) {
        if (this.isEmpty()) {
            return new PassengerPose(transform, transform.getRotation());
        }
        if (this.seat.firstPerson instanceof FirstPersonViewSpectator && this.seat.firstPerson.player != null) {
            return new PassengerPose(transform, ((FirstPersonViewSpectator)this.seat.firstPerson).getCurrentHeadRotation(transform));
        }
        if (this.seat.useSmoothCoasters()) {
            return new PassengerPose(transform, this.getCurrentHeadRotationQuat(transform));
        }
        EntityHandle entityHandle = EntityHandle.fromBukkit((Entity)this.entity);
        if (this.seat.isRotationLocked()) {
            return new PassengerPose(transform, entityHandle.getPitch(), entityHandle.getHeadRotation());
        }
        return new PassengerPose(entityHandle.getYaw(), entityHandle.getPitch(), entityHandle.getHeadRotation());
    }

    protected Quaternion getCurrentHeadRotationQuat(Matrix4x4 transform) {
        if (this.isEmpty()) {
            return transform.getRotation();
        }
        if (this.seat.firstPerson instanceof FirstPersonViewSpectator && this.seat.firstPerson.player != null) {
            return ((FirstPersonViewSpectator)this.seat.firstPerson).getCurrentHeadRotation(transform);
        }
        if (this.seat.isRotationLocked()) {
            EntityHandle entityHandle = EntityHandle.fromBukkit((Entity)this.entity);
            PassengerPose pose = new PassengerPose(transform, entityHandle.getPitch(), entityHandle.getHeadRotation());
            pose = pose.limitHeadYaw(70.0f);
            Quaternion rotation = new Quaternion();
            rotation.rotateY((double)(-pose.headYaw));
            rotation.rotateX((double)pose.headPitch);
            return rotation;
        }
        EntityHandle entityHandle = EntityHandle.fromBukkit((Entity)this.entity);
        Quaternion rotation = new Quaternion();
        rotation.rotateY((double)(-entityHandle.getHeadRotation()));
        rotation.rotateX((double)entityHandle.getPitch());
        return rotation;
    }

    public int spawnVehicleMount(AttachmentViewer viewer) {
        if (this.parentMountId == -1) {
            if (this.seat.getConfiguredPosition().anchor == AttachmentAnchor.SEAT_PARENT && this.seat.getConfiguredPosition().isIdentity() && this.seat.getParent() != null && !this.seat.isRotationLocked()) {
                this.parentMountId = ((CartAttachment)this.seat.getParent()).getMountEntityId();
            }
            if (this.parentMountId == -1) {
                if (this.fakeMount == null) {
                    this.fakeMount = this.createPassengerVehicle();
                    this.fakeMount.updatePosition(this.seat.getTransform(), new Vector(0.0, (double)this.orientation.getMountYaw(), 0.0));
                    this.fakeMount.syncPosition(true);
                }
                this.parentMountId = this.fakeMount.getEntityId();
            }
        }
        if (this.fakeMount != null) {
            this.fakeMount.spawn(viewer, this.seat.calcMotion());
            if (this.entity == viewer.getPlayer()) {
                viewer.send((PacketHandle)ClientboundUpdateAttributesPacketHandle.createZeroMaxHealth((int)this.fakeMount.getEntityId()));
            }
        }
        return this.parentMountId;
    }

    public void despawnVehicleMount(AttachmentViewer viewer) {
        if (this.fakeMount != null) {
            this.fakeMount.destroy(viewer);
            if (!this.fakeMount.hasViewers()) {
                this.fakeMount = null;
                this.parentMountId = -1;
            }
        }
    }

    protected void updateVehicleMountPosition(Matrix4x4 transform) {
        if (this.fakeMount != null) {
            this.fakeMount.updatePosition(transform, new Vector(0.0, (double)this.orientation.getMountYaw(), 0.0));
        }
    }

    protected void syncVehicleMountPosition(boolean absolute) {
        if (this.fakeMount != null) {
            this.fakeMount.syncPosition(absolute);
        }
    }

    public final void makeVisibleFirstPerson(AttachmentViewer viewer) {
        this.madeVisibleInFirstPerson = true;
        this.makeVisible(viewer);
    }

    public final void makeHiddenFirstPerson(AttachmentViewer viewer) {
        this.makeHidden(viewer);
        this.madeVisibleInFirstPerson = false;
    }

    public final boolean isMadeVisibleInFirstPerson() {
        return this.madeVisibleInFirstPerson;
    }

    public abstract Vector getThirdPersonCameraOffset();

    public abstract Vector getFirstPersonCameraOffset();

    public boolean isFirstPersonCameraFake() {
        return this.getFirstPersonCameraOffset().getY() != 1.0;
    }

    public abstract void makeVisible(AttachmentViewer var1);

    public abstract void makeHidden(AttachmentViewer var1);

    public void updateMode(boolean silent) {
        FirstPersonViewMode new_firstPersonMode = this.seat.firstPerson.getMode();
        if (new_firstPersonMode == FirstPersonViewMode.DYNAMIC) {
            new_firstPersonMode = FirstPersonViewMode.THIRD_P;
        }
        if (new_firstPersonMode == this.seat.firstPerson.getLiveMode()) {
            return;
        }
        if (!silent && this.seat.firstPerson.doesViewModeChangeRequireReset(new_firstPersonMode) && this.isPlayer()) {
            AttachmentViewer viewer = this.seat.getManager().asAttachmentViewer((Player)this.getEntity());
            if (this.seat.getAttachmentViewersSynced().contains(viewer)) {
                this.seat.makeHiddenImpl(viewer, true);
                this.seat.firstPerson.setLiveMode(new_firstPersonMode);
                this.seat.makeVisibleImpl(viewer, true);
                return;
            }
        }
        this.seat.firstPerson.setLiveMode(new_firstPersonMode);
    }

    public abstract void updatePosition(Matrix4x4 var1);

    public abstract void syncPosition(boolean var1);

    public abstract void updateFocus(boolean var1);

    protected VirtualEntity createPassengerVehicle() {
        VirtualEntity mount = new VirtualEntity(this.seat.getManager());
        mount.setEntityType(EntityType.ARMOR_STAND);
        mount.setSyncMode(VirtualEntity.SyncMode.SEAT);
        mount.setUseMinecartInterpolation(this.seat.isMinecartInterpolation());
        mount.setByViewerPositionAdjustment((viewer, pos) -> pos.setY(pos.getY() - viewer.getArmorStandButtOffset()));
        mount.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
        mount.getMetaData().set(LivingEntityHandle.DATA_HEALTH, (Object)Float.valueOf(10.0f));
        mount.getMetaData().set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
        return mount;
    }

    public abstract boolean containsEntityId(int var1);

    public static enum DisplayMode {
        DEFAULT(SeatedEntityNormal::new),
        ELYTRA_SIT(SeatedEntityElytra::new),
        STANDING(SeatedEntityStanding::new),
        HEAD(SeatedEntityHead::new),
        NO_NAMETAG(SeatedEntityNormal::new),
        INVISIBLE(SeatedEntityInvisible::new);

        private final Function<CartAttachmentSeat, SeatedEntity> _constructor;

        private DisplayMode(Function<CartAttachmentSeat, SeatedEntity> constructor) {
            this._constructor = constructor;
        }

        public SeatedEntity create(CartAttachmentSeat seat) {
            SeatedEntity seated = this._constructor.apply(seat);
            seated.setDisplayMode(this);
            return seated;
        }
    }

    public static class PassengerPose {
        public final float bodyYaw;
        public final float headPitch;
        public final float headYaw;

        public PassengerPose(Matrix4x4 bodyTransform, Quaternion headRotation) {
            this.bodyYaw = PassengerPose.getMountYaw(bodyTransform);
            Vector ypr = headRotation.getYawPitchRoll();
            this.headPitch = (float)ypr.getX();
            this.headYaw = (float)ypr.getY();
        }

        public PassengerPose(Matrix4x4 bodyTransform, float headPitch, float headYaw) {
            this.bodyYaw = PassengerPose.getMountYaw(bodyTransform);
            this.headPitch = headPitch;
            this.headYaw = headYaw;
        }

        public PassengerPose(float bodyYaw, float headPitch, float headYaw) {
            this.bodyYaw = bodyYaw;
            this.headPitch = headPitch;
            this.headYaw = headYaw;
        }

        public PassengerPose upsideDownFix_Pre_1_17() {
            return new PassengerPose(this.bodyYaw, -this.headPitch, -this.headYaw + 2.0f * this.bodyYaw);
        }

        public PassengerPose limitHeadYaw(float limit) {
            if (MathUtil.getAngleDifference((float)this.headYaw, (float)this.bodyYaw) > limit) {
                if (MathUtil.getAngleDifference((float)this.headYaw, (float)(this.bodyYaw + limit)) < MathUtil.getAngleDifference((float)this.headYaw, (float)(this.bodyYaw - limit))) {
                    return new PassengerPose(this.bodyYaw, this.headPitch, this.bodyYaw + limit);
                }
                return new PassengerPose(this.bodyYaw, this.headPitch, this.bodyYaw - limit);
            }
            return this;
        }

        private static float getMountYaw(Matrix4x4 transform) {
            Vector f = transform.getRotation().forwardVector();
            return MathUtil.getLookAtYaw((double)(-f.getZ()), (double)f.getX());
        }
    }
}

