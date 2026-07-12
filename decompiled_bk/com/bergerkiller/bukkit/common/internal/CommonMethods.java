/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.generated.net.minecraft.world.damagesource.DamageSourceHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class CommonMethods {
    public static BlockState CraftBlockState_new(Block block) {
        if (block == null) {
            throw new IllegalArgumentException("Input block is null");
        }
        return CraftBlockStateHandle.createNew(block);
    }

    public static void damageBy(Entity entity, Entity damager, double damage) {
        DamageSourceHandle source = damager instanceof Player ? DamageSourceHandle.playerAttack((HumanEntity)damager) : (damager instanceof LivingEntity ? DamageSourceHandle.mobAttack((LivingEntity)damager) : DamageSourceHandle.genericForEntity(entity));
        CommonNMS.getHandle(entity).damageEntity(source, (float)damage);
    }

    public static DamageSourceHandle DamageSource_from_damagecause(Entity entity, EntityDamageEvent.DamageCause cause) {
        DamageSourceHandle result = DamageSourceHandle.byNameForEntity(entity, CommonMethods.getSourceName(cause));
        if (result == null) {
            result = DamageSourceHandle.genericForEntity(entity);
        }
        return result;
    }

    public static DamageSourceHandle DamageSource_from_damagecause(World world, EntityDamageEvent.DamageCause cause) {
        DamageSourceHandle result = DamageSourceHandle.byName(world, CommonMethods.getSourceName(cause));
        if (result == null) {
            result = DamageSourceHandle.generic(world);
        }
        return result;
    }

    private static String getSourceName(EntityDamageEvent.DamageCause cause) {
        if (cause.name().equals("CRAMMING")) {
            return "cramming";
        }
        if (cause.name().equals("HOT_FLOOR")) {
            return "hotFloor";
        }
        if (cause.name().equals("FLY_INTO_WALL")) {
            return "flyIntoWall";
        }
        if (cause.name().equals("DRAGON_BREATH")) {
            return "dragonBreath";
        }
        switch (cause) {
            case FIRE: {
                return "inFire";
            }
            case LIGHTNING: {
                return "lightningBolt";
            }
            case FIRE_TICK: {
                return "onFire";
            }
            case LAVA: {
                return "lava";
            }
            case SUFFOCATION: {
                return "inWall";
            }
            case DROWNING: {
                return "drown";
            }
            case STARVATION: {
                return "starve";
            }
            case CONTACT: {
                return "cactus";
            }
            case FALL: {
                return "fall";
            }
            case VOID: {
                return "outOfWorld";
            }
            case MAGIC: {
                return "magic";
            }
            case WITHER: {
                return "wither";
            }
            case FALLING_BLOCK: {
                return "fallingBlock";
            }
        }
        return "generic";
    }

    public static int parseVersionNumber(String version) {
        int versionNumber = 0;
        int numDigits = 0;
        for (String part : version.split("\\.")) {
            int part_end;
            int part_start;
            for (part_start = 0; part_start < part.length() && !Character.isDigit(part.charAt(part_start)); ++part_start) {
            }
            if (part_start >= part.length()) continue;
            for (part_end = part_start; part_end < part.length() && Character.isDigit(part.charAt(part_end)); ++part_end) {
            }
            try {
                versionNumber *= 100;
                versionNumber += Integer.parseInt(part.substring(part_start, part_end));
                ++numDigits;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        while (numDigits < 3) {
            ++numDigits;
            versionNumber *= 100;
        }
        return versionNumber;
    }
}

