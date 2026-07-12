/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.server.level.EntityTrackerEntryState")
public abstract class EntityTrackerEntryStateHandle
extends Template.Handle {
    public static final EntityTrackerEntryStateClass T = Template.Class.create(EntityTrackerEntryStateClass.class, Common.TEMPLATE_RESOLVER);
    public static final double POSITION_STEP = CommonBootstrap.evaluateMCVersion(">=", "1.9") ? 2.44140625E-4 : 0.03125;
    public static final float ROTATION_STEP = 1.40625f;
    public static final float ROTATION_STEP_INV = 0.7111111f;

    public static EntityTrackerEntryStateHandle createHandle(Object handleInstance) {
        return (EntityTrackerEntryStateHandle)T.createHandle(handleInstance);
    }

    public abstract Vector getLoc();

    public abstract double getLocX();

    public abstract double getLocY();

    public abstract double getLocZ();

    public abstract void setLoc(double var1, double var3, double var5);

    public abstract void setLocX(double var1);

    public abstract void setLocY(double var1);

    public abstract void setLocZ(double var1);

    public abstract double getXVel();

    public abstract double getYVel();

    public abstract double getZVel();

    public abstract void setXVel(double var1);

    public abstract void setYVel(double var1);

    public abstract void setZVel(double var1);

    public abstract Vector getVelocity();

    public abstract void setVelocity(double var1, double var3, double var5);

    public abstract boolean checkTrackNeeded();

    public abstract int getEncodedPitch();

    public abstract void setEncodedPitch(int var1);

    public abstract int getEncodedYaw();

    public abstract void setEncodedYaw(int var1);

    public abstract int getEncodedHeadYaw();

    public abstract void setEncodedHeadYaw(int var1);

    public abstract CommonPacket getSpawnPacket();

    public abstract void onTick();

    public void setVelocity(Vector velocity) {
        this.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    public static final boolean hasProtocolRotationChanged(float angle1, float angle2) {
        int prot2;
        if (angle1 == angle2) {
            return false;
        }
        int prot_diff = MathUtil.floor((angle2 - angle1) * ROTATION_STEP_INV) & 0xFF;
        if (prot_diff > 0 && prot_diff < 255) {
            return true;
        }
        int prot1 = MathUtil.floor(angle1 * ROTATION_STEP_INV);
        return (prot1 - (prot2 = MathUtil.floor(angle2 * ROTATION_STEP_INV)) & 0xFF) != 0;
    }

    public static final int getProtocolRotation(float angle) {
        int protAngle = MathUtil.floor(angle * ROTATION_STEP_INV) & 0xFF;
        if (protAngle >= 128) {
            protAngle -= 256;
        }
        return protAngle;
    }

    public static final float getRotationFromProtocol(int protocol) {
        int protAngle = protocol & 0xFF;
        if (protAngle >= 128) {
            protAngle -= 256;
        }
        return (float)protAngle * ROTATION_STEP;
    }

    public void setYaw(float yaw) {
        this.setEncodedYaw(EntityTrackerEntryStateHandle.getProtocolRotation(yaw));
    }

    public void setPitch(float pitch) {
        this.setEncodedPitch(EntityTrackerEntryStateHandle.getProtocolRotation(pitch));
    }

    public void setHeadYaw(float headYaw) {
        this.setEncodedHeadYaw(EntityTrackerEntryStateHandle.getProtocolRotation(headYaw));
    }

    public float getYaw() {
        return EntityTrackerEntryStateHandle.getRotationFromProtocol(this.getEncodedYaw());
    }

    public float getPitch() {
        return EntityTrackerEntryStateHandle.getRotationFromProtocol(this.getEncodedPitch());
    }

    public float getHeadYaw() {
        return EntityTrackerEntryStateHandle.getRotationFromProtocol(this.getEncodedHeadYaw());
    }

    public abstract EntityHandle getEntity();

    public abstract void setEntity(EntityHandle var1);

    public abstract int getUpdateInterval();

    public abstract void setUpdateInterval(int var1);

    public abstract boolean isMobile();

    public abstract void setIsMobile(boolean var1);

    public abstract int getTickCounter();

    public abstract void setTickCounter(int var1);

    public abstract int getTimeSinceLocationSync();

    public abstract void setTimeSinceLocationSync(int var1);

    public static final class EntityTrackerEntryStateClass
    extends Template.Class<EntityTrackerEntryStateHandle> {
        public final Template.Field.Converted<EntityHandle> entity = new Template.Field.Converted();
        public final Template.Field.Integer updateInterval = new Template.Field.Integer();
        public final Template.Field.Boolean isMobile = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field<Consumer> broadcastMethod = new Template.Field();
        public final Template.Field.Integer tickCounter = new Template.Field.Integer();
        public final Template.Field.Integer timeSinceLocationSync = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<List<Entity>> opt_passengers = new Template.Field.Converted();
        @Template.Optional
        public final Template.Field.Converted<Entity> opt_vehicle = new Template.Field.Converted();
        public final Template.Method<Vector> getLoc = new Template.Method();
        public final Template.Method<Double> getLocX = new Template.Method();
        public final Template.Method<Double> getLocY = new Template.Method();
        public final Template.Method<Double> getLocZ = new Template.Method();
        public final Template.Method<Void> setLoc = new Template.Method();
        public final Template.Method<Void> setLocX = new Template.Method();
        public final Template.Method<Void> setLocY = new Template.Method();
        public final Template.Method<Void> setLocZ = new Template.Method();
        public final Template.Method<Double> getXVel = new Template.Method();
        public final Template.Method<Double> getYVel = new Template.Method();
        public final Template.Method<Double> getZVel = new Template.Method();
        public final Template.Method<Void> setXVel = new Template.Method();
        public final Template.Method<Void> setYVel = new Template.Method();
        public final Template.Method<Void> setZVel = new Template.Method();
        public final Template.Method<Vector> getVelocity = new Template.Method();
        public final Template.Method<Void> setVelocity = new Template.Method();
        public final Template.Method<Boolean> checkTrackNeeded = new Template.Method();
        public final Template.Method<Integer> getEncodedPitch = new Template.Method();
        public final Template.Method<Void> setEncodedPitch = new Template.Method();
        public final Template.Method<Integer> getEncodedYaw = new Template.Method();
        public final Template.Method<Void> setEncodedYaw = new Template.Method();
        public final Template.Method<Integer> getEncodedHeadYaw = new Template.Method();
        public final Template.Method<Void> setEncodedHeadYaw = new Template.Method();
        public final Template.Method.Converted<CommonPacket> getSpawnPacket = new Template.Method.Converted();
        public final Template.Method<Void> onTick = new Template.Method();
    }
}

