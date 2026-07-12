/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TCListener;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeNormalA;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public enum CollisionMode {
    DEFAULT("is stopped by"),
    PUSH("pushes"),
    CANCEL("ignores"),
    KILL("kills"),
    KILLNODROPS("kills without drops"),
    ENTER("takes in"),
    LINK("forms a group with"),
    DAMAGE("damages"),
    DAMAGENODROPS("damages without drops");

    private final String operationName;

    private CollisionMode(String operationName) {
        this.operationName = operationName;
    }

    public static CollisionMode parse(String text) {
        if (text.equalsIgnoreCase("skip")) {
            return CANCEL;
        }
        CollisionMode tf = ParseUtil.isBool((String)text) ? (ParseUtil.parseBool((String)text) ? DEFAULT : CANCEL) : null;
        return (CollisionMode)((Object)ParseUtil.parseEnum(CollisionMode.class, (String)text, (Object)((Object)tf)));
    }

    public static CollisionMode fromLinking(boolean state) {
        return state ? LINK : DEFAULT;
    }

    public static CollisionMode fromPushing(boolean state) {
        return state ? PUSH : DEFAULT;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean execute(MinecartMember<?> member, Entity entity) {
        CommonMinecart minecart = (CommonMinecart)member.getEntity();
        MinecartMember<?> other = MinecartMemberStore.getFromEntity(entity);
        if (!member.isInteractable()) return false;
        if (entity.isDead()) return false;
        if (member.isCollisionIgnored(entity)) {
            return false;
        }
        if (entity.isInsideVehicle() && entity.getVehicle() instanceof Minecart) {
            return false;
        }
        if (other != null) {
            Block b2;
            Block b1;
            RailLogic logic2;
            if (!other.isInteractable()) {
                return false;
            }
            if (member.getGroup() == other.getGroup()) {
                return false;
            }
            RailLogic logic1 = member.getRailLogic();
            if (logic1 instanceof RailLogicVerticalSlopeNormalA && (logic2 = other.getRailLogic()) instanceof RailLogicVerticalSlopeNormalA && BlockUtil.equals((Block)(b1 = member.getBlock(logic1.getDirection())), (Block)(b2 = other.getBlock(logic2.getDirection())))) {
                return false;
            }
        }
        if (entity instanceof Player && this.isHitCollision()) {
            double trainX = member.getLimitedVelocity().getX();
            double trainZ = member.getLimitedVelocity().getZ();
            double playerSpeed = ((Player)entity).getWalkSpeed();
            Vector playerVelocity = ((Player)entity).getEyeLocation().getDirection();
            playerVelocity.multiply(playerSpeed);
            double playerX = playerVelocity.getX();
            double playerZ = playerVelocity.getZ();
            if (Math.abs(playerX) + Math.abs(playerZ) > 0.03) {
                if (Math.abs(trainX) + Math.abs(trainZ) < 0.03) {
                    return TCConfig.allowPlayerCollisionFromBehind;
                }
                if (Math.abs(trainX) > Math.abs(trainZ) && playerX * trainX > 0.0 && Math.abs(playerX) > Math.abs(trainX)) {
                    return TCConfig.allowPlayerCollisionFromBehind;
                }
                if (Math.abs(trainX) <= Math.abs(trainZ) && playerZ * trainZ >= 0.0 && Math.abs(playerZ) > Math.abs(trainZ)) {
                    return TCConfig.allowPlayerCollisionFromBehind;
                }
            }
        }
        switch (this.ordinal()) {
            case 5: {
                if (member.getAvailableSeatCount(entity) <= 0) return false;
                if (!Util.canBePassenger(entity)) return false;
                if (!member.canCollisionEnter()) return false;
                minecart.addPassenger(entity);
                return false;
            }
            case 1: {
                this.push(member, entity);
                return false;
            }
            case 2: {
                return false;
            }
            case 7: 
            case 8: {
                if (!member.isMoving()) return false;
                if (!member.isHeadingTo(entity)) return false;
                if (this == DAMAGENODROPS) {
                    TCListener.cancelNextDrops = true;
                }
                double minecartEnergy = ((CommonMinecart)member.getEntity()).vel.lengthSquared() * member.getProperties().getTrainProperties().getCollisionDamage();
                this.damage(member, entity, minecartEnergy);
                this.push(member, entity);
                if (this != DAMAGENODROPS) return false;
                TCListener.cancelNextDrops = false;
                return false;
            }
            case 3: 
            case 4: {
                if (!member.isMoving()) return false;
                if (!member.isHeadingTo(entity)) return false;
                if (this == KILLNODROPS) {
                    TCListener.cancelNextDrops = true;
                }
                MinecartMember<?> oldKilledByMember = TCListener.killedByMember;
                try {
                    TCListener.killedByMember = member;
                    this.damage(member, entity, 32767.0);
                }
                finally {
                    TCListener.killedByMember = oldKilledByMember;
                }
                if (this != KILLNODROPS) return false;
                TCListener.cancelNextDrops = false;
                return false;
            }
            case 6: {
                if (other == null) return true;
                if (MinecartGroupStore.link(member, other).isCancelCollision()) return false;
                return true;
            }
        }
        if (member.isMovementControlled()) {
            return false;
        }
        if (other == null) return true;
        if (!member.isHeadingTo(entity)) return false;
        member.getGroup().stop();
        return false;
    }

    private void push(MinecartMember<?> member, Entity entity) {
        if (entity instanceof Minecart) {
            if (member.isHeadingTo(entity)) {
                double gap = member.getCartCouplerLength();
                MinecartMember<?> otherMember = MinecartMemberStore.getFromEntity(entity);
                gap = otherMember != null ? (gap += otherMember.getCartCouplerLength()) : (gap += 0.5 * TCConfig.cartDistanceGap);
                double force = gap + 1.0 - ((CommonMinecart)member.getEntity()).loc.distanceSquared(entity);
                force *= TCConfig.cartDistanceForcer;
                force += member.getRealSpeed() - entity.getVelocity().length();
                if (force > 0.0) {
                    member.push(entity, force);
                }
            }
        } else {
            member.pushSideways(entity);
        }
    }

    private void damage(MinecartMember<?> member, Entity entity, double damageAmount) {
        if (entity instanceof LivingEntity) {
            boolean old = EntityUtil.isInvulnerable((Entity)entity);
            EntityUtil.setInvulnerable((Entity)entity, (boolean)false);
            ((LivingEntity)entity).damage(damageAmount, ((CommonMinecart)member.getEntity()).getEntity());
            EntityUtil.setInvulnerable((Entity)entity, (boolean)old);
        } else {
            EntityUtil.damage((Entity)entity, (EntityDamageEvent.DamageCause)EntityDamageEvent.DamageCause.CUSTOM, (double)32767.0);
            entity.remove();
        }
    }

    public String getOperationName() {
        return this.operationName;
    }

    public boolean permitsKnockback() {
        return this == DEFAULT;
    }

    public boolean isHitCollision() {
        switch (this.ordinal()) {
            case 1: 
            case 3: 
            case 4: 
            case 7: 
            case 8: {
                return true;
            }
        }
        return false;
    }
}

