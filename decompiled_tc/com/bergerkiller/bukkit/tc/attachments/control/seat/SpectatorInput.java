/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle;
import org.bukkit.util.Vector;

class SpectatorInput {
    private AttachmentViewer player;
    private int blindTicks = 0;
    private float yawLimit = 360.0f;
    private boolean enabled = false;
    private final Object deltaRotationLock = new Object();
    private YawPitch deltaRotation = YawPitch.ZERO;
    private final Quaternion absOrientation = new Quaternion();

    SpectatorInput() {
    }

    public void start(AttachmentViewer player, float yawLimit) {
        this.player = player;
        this.blindTicks = CommonUtil.getServerTicks() + 5;
        this.yawLimit = yawLimit;
        this.deltaRotation = YawPitch.ZERO;
        this.absOrientation.setIdentity();
        this.enabled = true;
        this.sendRotation(0.0f, 0.0f);
    }

    public void startLocked() {
        this.enabled = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addInputRotation(YawPitch rotation) {
        Object object = this.deltaRotationLock;
        synchronized (object) {
            this.deltaRotation = YawPitch.add(this.deltaRotation, rotation);
        }
    }

    public void stop(Matrix4x4 currentEyeTransform) {
        if (this.player != null) {
            FirstPersonView.HeadRotation headRot = FirstPersonView.HeadRotation.compute(currentEyeTransform);
            headRot = headRot.ensureLevel();
            this.sendRotation(headRot.pitch, headRot.yaw);
        }
        this.player = null;
        this.blindTicks = 0;
        this.deltaRotation = YawPitch.ZERO;
        this.enabled = false;
    }

    public boolean isStarted() {
        return this.player != null;
    }

    public void applyTo(Matrix4x4 eyeTransform) {
        Vector pos = eyeTransform.toVector();
        Quaternion rot = eyeTransform.getRotation();
        this.applyTo(rot);
        eyeTransform.setIdentity();
        eyeTransform.translate(pos);
        eyeTransform.rotate(rot);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyTo(Quaternion eyeRotation) {
        if (this.enabled) {
            YawPitch accumulatedRotation;
            Object object = this.deltaRotationLock;
            synchronized (object) {
                accumulatedRotation = this.deltaRotation;
                this.deltaRotation = YawPitch.ZERO;
            }
            if (accumulatedRotation.yaw != 0.0f || accumulatedRotation.pitch != 0.0f) {
                RelativeOrientationCalc calc = new RelativeOrientationCalc(this, accumulatedRotation, eyeRotation);
                this.absOrientation.setTo(calc.calculate());
                if (SpectatorInput.isUpsideDown(this.absOrientation)) {
                    this.absOrientation.rotateZFlip();
                }
            }
        }
        eyeRotation.multiply(this.absOrientation);
    }

    public void update() {
        if (this.player == null) {
            return;
        }
        if (this.blindTicks != 0) {
            if (CommonUtil.getServerTicks() >= this.blindTicks) {
                this.blindTicks = 0;
            } else {
                return;
            }
        }
    }

    private void sendRotation(float pitch, float yaw) {
        this.player.send((PacketHandle)ClientboundPlayerRotationPacketHandle.createAbsolute((float)yaw, (float)pitch));
    }

    private static boolean isUpsideDown(Quaternion q) {
        return 1.0 + 2.0 * (-q.getX() * q.getX() - q.getZ() * q.getZ()) < 0.0;
    }

    public static final class YawPitch {
        public static final YawPitch ZERO = new YawPitch(0.0f, 0.0f);
        public final float yaw;
        public final float pitch;

        public YawPitch(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public static YawPitch add(YawPitch a, YawPitch b) {
            return new YawPitch(a.yaw + b.yaw, a.pitch + b.pitch);
        }

        public static YawPitch subtract(YawPitch a, YawPitch b) {
            return new YawPitch(a.yaw - b.yaw, a.pitch - b.pitch);
        }
    }

    private static class RelativeOrientationCalc {
        private static final int MAX_INTERPOLATION_ROUNDS = 20;
        public final Quaternion base;
        public final double basePitch;
        public final double baseYaw;
        public final double deltaPitch;
        public final double deltaYaw;
        public final double maxForwardZ;

        public RelativeOrientationCalc(SpectatorInput input, YawPitch inputRotation, Quaternion eyeRotation) {
            Quaternion current = Quaternion.multiply((Quaternion)eyeRotation, (Quaternion)input.absOrientation);
            Vector eyePYR = current.getYawPitchRoll();
            this.basePitch = eyePYR.getX();
            this.baseYaw = -eyePYR.getY();
            this.deltaPitch = inputRotation.pitch;
            this.deltaYaw = SpectatorInput.isUpsideDown(current) ? (double)inputRotation.yaw : (double)(-inputRotation.yaw);
            this.base = Quaternion.divide((Quaternion)input.absOrientation, (Quaternion)current);
            this.maxForwardZ = input.yawLimit >= 180.0f ? 1.0 : Math.cos(Math.toRadians(input.yawLimit));
        }

        public Quaternion calculate() {
            Quaternion result;
            boolean canDoFullYaw;
            Quaternion fullRotation = this.createRotation(this.deltaPitch, this.deltaYaw);
            if (this.isValidRotation(fullRotation)) {
                return fullRotation;
            }
            Quaternion tmp = new Quaternion();
            boolean canDoFullPitch = this.testRotation(tmp, this.deltaPitch, 0.0);
            if (canDoFullPitch == (canDoFullYaw = this.testRotation(tmp, 0.0, this.deltaYaw))) {
                return this.calcUsingSlerp(fullRotation);
            }
            double t0 = 0.0;
            double t1 = 1.0;
            if (canDoFullYaw) {
                result = this.createRotation(0.0, this.deltaYaw);
                for (int n = 0; n < 20; ++n) {
                    double th = 0.5 * (t0 + t1);
                    if (this.testRotation(tmp, th * this.deltaPitch, this.deltaYaw)) {
                        t0 = th;
                        result.setTo(tmp);
                        continue;
                    }
                    t1 = th;
                }
            } else {
                result = this.createRotation(this.deltaPitch, 0.0);
                for (int n = 0; n < 20; ++n) {
                    double th = 0.5 * (t0 + t1);
                    if (this.testRotation(tmp, this.deltaPitch, th * this.deltaYaw)) {
                        t0 = th;
                        result.setTo(tmp);
                        continue;
                    }
                    t1 = th;
                }
            }
            return result;
        }

        private Quaternion calcUsingSlerp(Quaternion fullRotation) {
            Quaternion startRotation = this.createRotation(0.0, 0.0);
            double t0 = 0.0;
            double t1 = 1.0;
            Quaternion result = startRotation;
            for (int n = 0; n < 20; ++n) {
                double th = 0.5 * (t0 + t1);
                Quaternion qh = Quaternion.slerp((Quaternion)startRotation, (Quaternion)fullRotation, (double)th);
                if (this.isValidRotation(qh)) {
                    t0 = th;
                    result = qh;
                    continue;
                }
                t1 = th;
            }
            return result;
        }

        private boolean testRotation(Quaternion tmp, double deltaPitch, double deltaYaw) {
            tmp.setTo(this.base);
            tmp.rotateY(this.baseYaw + deltaYaw);
            tmp.rotateX(this.basePitch + deltaPitch);
            return this.isValidRotation(tmp);
        }

        private Quaternion createRotation(double deltaPitch, double deltaYaw) {
            Quaternion result = new Quaternion();
            this.testRotation(result, deltaPitch, deltaYaw);
            return result;
        }

        private boolean isValidRotation(Quaternion rotation) {
            Vector forward;
            if (SpectatorInput.isUpsideDown(rotation)) {
                return false;
            }
            return this.maxForwardZ == 1.0 || !((forward = rotation.forwardVector().setY(0.0).normalize()).getZ() < this.maxForwardZ);
        }
    }
}

