/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.EntityEquipment
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.block.BlockRayTrace;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributesHandle;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.Vector;

public class CommonLivingEntity<T extends LivingEntity>
extends CommonEntity<T> {
    public CommonLivingEntity(T entity) {
        super(entity);
    }

    public double getEyeHeight() {
        return ((LivingEntity)this.entity).getEyeHeight();
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        return ((LivingEntity)this.entity).getEyeHeight(ignoreSneaking);
    }

    public Location getEyeLocation() {
        return ((LivingEntity)this.entity).getEyeLocation();
    }

    public Block getTargetBlock() {
        BlockRayTrace.HitResult hit = BlockRayTrace.fromEyeOf((LivingEntity)this.getEntity()).rayTrace();
        return hit == null ? null : hit.getHitBlock();
    }

    public Block getTargetBlock(double maxDistance) {
        BlockRayTrace.HitResult hit = BlockRayTrace.fromEye(this.getEyeLocation(), maxDistance).rayTrace();
        return hit == null ? null : hit.getHitBlock();
    }

    public EntityEquipment getEquipment() {
        return ((LivingEntity)this.entity).getEquipment();
    }

    public double getMaxHealth() {
        return this.handle.cast(LivingEntityHandle.T).getMaxHealth();
    }

    public double getHealth() {
        LivingEntityHandle handle = this.handle.cast(LivingEntityHandle.T);
        return MathUtil.clamp(handle.getHealth(), 0.0f, handle.getMaxHealth());
    }

    public void setHealth(double health) {
        ((LivingEntity)this.entity).setHealth(health);
    }

    public void damage(double damage) {
        ((LivingEntity)this.entity).damage(damage);
    }

    public void damage(double damage, Entity damager) {
        ((LivingEntity)this.entity).damage(damage, damager);
    }

    public Vector getMovement() {
        return MathUtil.getDirection(this.loc.getYaw(), this.loc.getPitch()).multiply(this.getForwardMovement());
    }

    public double getForwardMovement() {
        return LivingEntityHandle.T.getMoveIntent.invoke(this.getHandle()).getZ();
    }

    public void setPathfindingRange(double range) {
        LivingEntityHandle nmsEntity = this.handle.cast(LivingEntityHandle.T);
        nmsEntity.getAttribute(AttributesHandle.FOLLOW_RANGE).setBaseValue(range);
    }

    public double getPathfindingRange() {
        LivingEntityHandle nmsEntity = this.handle.cast(LivingEntityHandle.T);
        return nmsEntity.getAttribute(AttributesHandle.FOLLOW_RANGE).getBaseValue();
    }
}

