/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.ObjectPosition;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewLockMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public abstract class FirstPersonView {
    protected final CartAttachmentSeat seat;
    protected final AttachmentViewer player;
    private FirstPersonViewMode _liveMode = FirstPersonViewMode.DEFAULT;
    private FirstPersonViewMode _mode = FirstPersonViewMode.DYNAMIC;
    private FirstPersonViewLockMode _lock = FirstPersonViewLockMode.MOVE;
    protected ObjectPosition _eyePosition = new ObjectPosition();
    public static final float BODY_LOCK_FOV_LIMIT = 70.0f;
    private static final boolean HAS_EQUIPMENT_SEND_METHOD = Common.evaluateMCVersion((String)">=", (String)"1.18");

    public FirstPersonView(CartAttachmentSeat seat, AttachmentViewer player) {
        this.seat = seat;
        this.player = player;
    }

    public ObjectPosition getEyePosition() {
        return this._eyePosition;
    }

    public AttachmentViewer getViewer() {
        return this.player;
    }

    public boolean doesViewModeChangeRequireReset(FirstPersonViewMode newViewMode) {
        return newViewMode != this.getLiveMode() || newViewMode.hasFakePlayer();
    }

    protected Matrix4x4 getEyeTransform() {
        if (!this._eyePosition.isDefault()) {
            return Matrix4x4.multiply((Matrix4x4)this.seat.getTransform(), (Matrix4x4)this._eyePosition.transform);
        }
        if (this.getLiveMode() == FirstPersonViewMode.THIRD_P) {
            Matrix4x4 transform = this.seat.getTransform().clone();
            transform.translate(this.seat.seated.getThirdPersonCameraOffset());
            return transform;
        }
        if (this.seat.useSmoothCoasters()) {
            Matrix4x4 transform = this.seat.getTransform().clone();
            transform.translate(this.seat.seated.getFirstPersonCameraOffset());
            return transform;
        }
        Matrix4x4 eye = new Matrix4x4();
        eye.translate(this.seat.seated.getFirstPersonCameraOffset());
        eye.multiply(this.seat.getTransform());
        return eye;
    }

    public Location getPlayerEyeLocation() {
        if (this.player != null) {
            Vector pos = this.getEyeTransform().toVector();
            Location eye = this.player.getPlayer().getEyeLocation();
            eye.setX(pos.getX());
            eye.setY(pos.getY());
            eye.setZ(pos.getZ());
            return eye;
        }
        return null;
    }

    public abstract void makeVisible(AttachmentViewer var1, boolean var2);

    public abstract void makeHidden(AttachmentViewer var1, boolean var2);

    public abstract void onTick();

    public abstract void onMove(boolean var1);

    public FirstPersonViewMode getLiveMode() {
        return this._liveMode;
    }

    public void setLiveMode(FirstPersonViewMode liveMode) {
        this._liveMode = liveMode;
    }

    public FirstPersonViewMode getMode() {
        return this._mode;
    }

    public void setMode(FirstPersonViewMode mode) {
        this._mode = mode;
    }

    public FirstPersonViewLockMode getLockMode() {
        return this._lock;
    }

    public void setLockMode(FirstPersonViewLockMode lock) {
        this._lock = lock;
    }

    protected static void setPlayerVisible(AttachmentViewer player, boolean visible) {
        DataWatcher metaTmp = new DataWatcher();
        metaTmp.set(EntityHandle.DATA_FLAGS, (Object)((Byte)EntityUtil.getDataWatcher((Entity)player.getPlayer()).get(EntityHandle.DATA_FLAGS)));
        metaTmp.setFlag(EntityHandle.DATA_FLAGS, 32, !visible);
        ClientboundSetEntityDataPacketHandle metaPacket = ClientboundSetEntityDataPacketHandle.createNew((int)player.getEntityId(), (DataWatcher)metaTmp, (boolean)true);
        player.send((PacketHandle)metaPacket);
        if (visible) {
            PlayerInventory inv = player.getPlayer().getInventory();
            FirstPersonView.sendEquipment(player, EquipmentSlot.HEAD, inv.getHelmet());
            FirstPersonView.sendEquipment(player, EquipmentSlot.CHEST, inv.getChestplate());
            FirstPersonView.sendEquipment(player, EquipmentSlot.FEET, inv.getBoots());
            FirstPersonView.sendEquipment(player, EquipmentSlot.LEGS, inv.getLeggings());
        } else {
            FirstPersonView.sendEquipment(player, EquipmentSlot.HEAD, null);
            FirstPersonView.sendEquipment(player, EquipmentSlot.CHEST, null);
            FirstPersonView.sendEquipment(player, EquipmentSlot.FEET, null);
            FirstPersonView.sendEquipment(player, EquipmentSlot.LEGS, null);
        }
    }

    protected static void sendEquipment(AttachmentViewer player, EquipmentSlot slot, ItemStack item) {
        if (HAS_EQUIPMENT_SEND_METHOD) {
            FirstPersonView.sendEquipmentUsingBukkit(player.getPlayer(), slot, item);
        } else {
            player.sendSilent((PacketHandle)Util.createPlayerEquipmentPacket(player.getEntityId(), slot, item));
        }
    }

    private static void sendEquipmentUsingBukkit(Player player, EquipmentSlot slot, ItemStack item) {
        if (item == null) {
            item = ItemUtil.emptyItem();
        }
        player.sendEquipmentChange((LivingEntity)player, slot, item);
    }

    public static final class HeadRotation {
        public final float pitch;
        public final float yaw;
        public final float roll;
        public final Vector pyr;

        private HeadRotation(float pitch, float yaw, float roll) {
            this.pitch = pitch;
            this.yaw = yaw;
            this.roll = roll;
            this.pyr = new Vector(pitch, yaw, roll);
        }

        public HeadRotation flipVertical() {
            return new HeadRotation(180.0f - this.pitch, 180.0f + this.yaw, this.roll);
        }

        public HeadRotation ensureLevel() {
            if (Math.abs(this.pitch) > 90.0f) {
                return this.flipVertical();
            }
            return this;
        }

        public static HeadRotation compute(Matrix4x4 eyeTransform) {
            return HeadRotation.compute(eyeTransform.getRotation());
        }

        public static HeadRotation compute(Quaternion eyeOrientation) {
            float roll;
            float yaw;
            float pitch;
            Vector forward = eyeOrientation.forwardVector();
            Vector up = eyeOrientation.upVector();
            if (Math.abs(forward.getY()) < 0.999) {
                HeadRotation rot = new HeadRotation(MathUtil.getLookAtPitch((double)forward.getX(), (double)forward.getY(), (double)forward.getZ()), MathUtil.getLookAtYaw((Vector)forward) + 90.0f, (float)eyeOrientation.getRoll());
                if (up.getY() < 0.0) {
                    rot = rot.flipVertical();
                }
                return rot;
            }
            if (forward.getY() > 0.0) {
                pitch = -90.0f;
                yaw = MathUtil.getLookAtYaw((Vector)up) - 90.0f;
                roll = (float)eyeOrientation.getRoll();
            } else {
                pitch = 90.0f;
                yaw = MathUtil.getLookAtYaw((Vector)up) + 90.0f;
                roll = (float)eyeOrientation.getRoll();
            }
            return new HeadRotation(pitch, yaw, roll);
        }

        public static HeadRotation of(float pitch, float yaw) {
            return new HeadRotation(pitch, yaw, 0.0f);
        }
    }
}

