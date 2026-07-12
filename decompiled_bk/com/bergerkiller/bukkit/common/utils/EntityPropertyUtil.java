/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.utils.EntityGroupingUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityPropertyUtil
extends EntityGroupingUtil {
    private static final Material[] minecartTypes = MaterialUtil.ISMINECART.getMaterials().toArray(new Material[0]);

    public static Material[] getMinecartTypes() {
        return minecartTypes;
    }

    private static final Object h(Entity entity) {
        return HandleConversion.toEntityHandle(entity);
    }

    @Deprecated
    public static double getLocX(Entity entity) {
        return EntityHandle.T.getLocX.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setLocX(Entity entity, double value) {
        EntityHandle.T.setLocX.invoke(EntityPropertyUtil.h(entity), value);
    }

    @Deprecated
    public static double getLocY(Entity entity) {
        return EntityHandle.T.getLocY.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setLocY(Entity entity, double value) {
        EntityHandle.T.setLocY.invoke(EntityPropertyUtil.h(entity), value);
    }

    @Deprecated
    public static double getLocZ(Entity entity) {
        return EntityHandle.T.getLocZ.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setLocZ(Entity entity, double value) {
        EntityHandle.T.setLocZ.invoke(EntityPropertyUtil.h(entity), value);
    }

    @Deprecated
    public static double getMotX(Entity entity) {
        return EntityHandle.T.getMotX.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setMotX(Entity entity, double value) {
        EntityHandle.T.setMotX.invoke(EntityPropertyUtil.h(entity), value);
    }

    @Deprecated
    public static double getMotY(Entity entity) {
        return EntityHandle.T.getMotY.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setMotY(Entity entity, double value) {
        EntityHandle.T.setMotY.invoke(EntityPropertyUtil.h(entity), value);
    }

    @Deprecated
    public static double getMotZ(Entity entity) {
        return EntityHandle.T.getMotZ.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setMotZ(Entity entity, double value) {
        EntityHandle.T.setMotZ.invoke(EntityPropertyUtil.h(entity), value);
    }

    public static double getLastX(Entity entity) {
        return EntityHandle.T.lastX.getDouble(EntityPropertyUtil.h(entity));
    }

    public static void setLastX(Entity entity, double value) {
        EntityHandle.T.lastX.setDouble(EntityPropertyUtil.h(entity), value);
    }

    public static double getLastY(Entity entity) {
        return EntityHandle.T.lastY.getDouble(EntityPropertyUtil.h(entity));
    }

    public static void setLastY(Entity entity, double value) {
        EntityHandle.T.lastY.setDouble(EntityPropertyUtil.h(entity), value);
    }

    public static double getLastZ(Entity entity) {
        return EntityHandle.T.lastZ.getDouble(EntityPropertyUtil.h(entity));
    }

    public static void setLastZ(Entity entity, double value) {
        EntityHandle.T.lastZ.setDouble(EntityPropertyUtil.h(entity), value);
    }

    public static int getChunkX(Entity entity) {
        return EntityHandle.T.getChunkX.invoke(EntityPropertyUtil.h(entity));
    }

    public static int getChunkY(Entity entity) {
        return EntityHandle.T.getChunkY.invoke(EntityPropertyUtil.h(entity));
    }

    public static int getChunkZ(Entity entity) {
        return EntityHandle.T.getChunkZ.invoke(EntityPropertyUtil.h(entity));
    }

    @Deprecated
    public static void setDead(Entity entity, boolean dead) {
        EntityHandle.T.setDestroyed.invoke(EntityPropertyUtil.h(entity), dead);
    }

    public static void setDestroyed(Entity entity, boolean dead) {
        EntityHandle.T.setDestroyed.invoke(EntityPropertyUtil.h(entity), dead);
    }

    @Deprecated
    public static void damageBy(Entity entity, Entity damager, int damage) {
        EntityPropertyUtil.damageBy(entity, damager, (double)damage);
    }

    public static void damageBy(Entity entity, Entity damager, double damage) {
        CommonMethods.damageBy(entity, damager, damage);
    }

    @Deprecated
    public static void damage(Entity entity, EntityDamageEvent.DamageCause cause, int damage) {
        EntityPropertyUtil.damage(entity, cause, (double)damage);
    }

    public static void damage(Entity entity, EntityDamageEvent.DamageCause cause, double damage) {
        EntityHandle eh = EntityHandle.fromBukkit(entity);
        eh.damageEntity(CommonMethods.DamageSource_from_damagecause(entity, cause), (float)damage);
    }

    public static PlayerAbilities getAbilities(HumanEntity human) {
        return PlayerHandle.T.abilities.get(HandleConversion.toEntityHandle((Entity)human));
    }

    public static void setInvulnerable(Entity entity, boolean state) {
        if (entity instanceof HumanEntity) {
            EntityPropertyUtil.getAbilities((HumanEntity)entity).setInvulnerable(state);
        }
    }

    public static boolean isInvulnerable(Entity entity) {
        if (entity instanceof HumanEntity) {
            return EntityPropertyUtil.getAbilities((HumanEntity)entity).isInvulnerable();
        }
        return false;
    }

    public static void suppressPortalTeleportationThisTick(Entity entity) {
        EntityHandle.fromBukkit(entity).suppressPortalThisTick();
    }

    @Deprecated
    public static void setAllowTeleportation(Entity entity, boolean state) {
        if (!state) {
            EntityPropertyUtil.suppressPortalTeleportationThisTick(entity);
        }
    }

    public static boolean isInsidePortalThisTick(Entity entity) {
        return EntityHandle.fromBukkit(entity).isInsidePortalThisTick();
    }

    @Deprecated
    public static boolean getAllowTeleportation(Entity entity) {
        return EntityPropertyUtil.isInsidePortalThisTick(entity);
    }

    public static void setPortalCooldown(Entity entity, int cooldownTicks) {
        EntityHandle.T.portalCooldown.setInteger(EntityPropertyUtil.h(entity), cooldownTicks);
    }

    public static int getPortalCooldown(Entity entity) {
        return EntityHandle.T.portalCooldown.getInteger(EntityPropertyUtil.h(entity));
    }

    public static int getPortalCooldownMaximum(Entity entity) {
        return EntityHandle.T.getPortalCooldownMaximum.invoke(EntityPropertyUtil.h(entity));
    }

    public static void setPortalTime(Entity entity, int timeTicks) {
        EntityHandle.fromBukkit(entity).setPortalTime(timeTicks);
    }

    public static int getPortalTime(Entity entity) {
        return EntityHandle.fromBukkit(entity).getPortalTime();
    }

    public static int getPortalWaitTime(Entity entity) {
        return EntityHandle.fromBukkit(entity).getPortalWaitTime();
    }
}

