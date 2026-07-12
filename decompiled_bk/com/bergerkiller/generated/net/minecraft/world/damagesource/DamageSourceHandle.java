/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleDestroyEvent
 */
package com.bergerkiller.generated.net.minecraft.world.damagesource;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

@Template.InstanceType(value="net.minecraft.world.damagesource.DamageSource")
public abstract class DamageSourceHandle
extends Template.Handle {
    public static final DamageSourceClass T = Template.Class.create(DamageSourceClass.class, Common.TEMPLATE_RESOLVER);
    public static final Map<String, Object> INTERNAL_NAME_TO_KEY = new HashMap<String, Object>();

    public static DamageSourceHandle createHandle(Object handleInstance) {
        return (DamageSourceHandle)T.createHandle(handleInstance);
    }

    public static DamageSourceHandle generic(World world) {
        return DamageSourceHandle.T.generic.invoke(world);
    }

    public static DamageSourceHandle genericForEntity(Entity entity) {
        return DamageSourceHandle.T.genericForEntity.invoke(entity);
    }

    public static DamageSourceHandle mobAttack(LivingEntity livingEntity) {
        return DamageSourceHandle.T.mobAttack.invoke(livingEntity);
    }

    public static DamageSourceHandle playerAttack(HumanEntity humanEntity) {
        return DamageSourceHandle.T.playerAttack.invoke(humanEntity);
    }

    public static DamageSourceHandle arrowHit(Arrow arrow, Entity damager) {
        return DamageSourceHandle.T.arrowHit.invoke(arrow, damager);
    }

    public static DamageSourceHandle thrownHit(Entity projectile, Entity damager) {
        return DamageSourceHandle.T.thrownHit.invoke(projectile, damager);
    }

    public static DamageSourceHandle magicHit(Entity magicEntity, Entity damager) {
        return DamageSourceHandle.T.magicHit.invoke(magicEntity, damager);
    }

    public static DamageSourceHandle thorns(Entity entity) {
        return DamageSourceHandle.T.thorns.invoke(entity);
    }

    public static DamageSourceHandle byName(World world, String name) {
        return DamageSourceHandle.T.byName.invoke(world, name);
    }

    public static DamageSourceHandle byNameForEntity(Entity entity, String name) {
        return DamageSourceHandle.T.byNameForEntity.invoke(entity, name);
    }

    public static void initNameLookup(Map<String, Object> lookup) {
        DamageSourceHandle.T.initNameLookup.invoker.invoke(null, lookup);
    }

    public abstract String getTranslationIndex();

    public abstract Entity getEntity();

    public abstract Object toBukkit();

    public abstract VehicleDestroyEvent createVehicleDestroyEvent(Vehicle var1, Entity var2);

    public abstract VehicleDamageEvent createVehicleDamageEvent(Vehicle var1, Entity var2, double var3);

    public abstract boolean isExplosion();

    public abstract boolean isFireDamage();

    public VehicleDestroyEvent createVehicleDestroyEvent(Vehicle vehicle) {
        return this.createVehicleDestroyEvent(vehicle, this.getEntity());
    }

    public VehicleDamageEvent createVehicleDamageEvent(Vehicle vehicle, double damage) {
        return this.createVehicleDamageEvent(vehicle, this.getEntity(), damage);
    }

    private static void translateLegacyName(String legacyName, String registryName) {
        Object byLegacy = INTERNAL_NAME_TO_KEY.get(legacyName);
        Object byRegistyName = INTERNAL_NAME_TO_KEY.get(registryName);
        if (byLegacy != null && byRegistyName == null) {
            INTERNAL_NAME_TO_KEY.put(registryName, byLegacy);
        } else if (byLegacy == null && byRegistyName != null) {
            INTERNAL_NAME_TO_KEY.put(legacyName, byRegistyName);
        }
    }

    static {
        try {
            DamageSourceHandle.initNameLookup(INTERNAL_NAME_TO_KEY);
        }
        catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to initialize damage sources by name", t);
        }
        DamageSourceHandle.translateLegacyName("inFire", "in_fire");
        DamageSourceHandle.translateLegacyName("lightningBolt", "lightning_bolt");
        DamageSourceHandle.translateLegacyName("onFire", "on_fire");
        DamageSourceHandle.translateLegacyName("hotFloor", "hot_floor");
        DamageSourceHandle.translateLegacyName("inWall", "in_wall");
        DamageSourceHandle.translateLegacyName("flyIntoWall", "fly_into_wall");
        DamageSourceHandle.translateLegacyName("outOfWorld", "out_of_world");
        DamageSourceHandle.translateLegacyName("dragonBreath", "dragon_breath");
        DamageSourceHandle.translateLegacyName("dryout", "dry_out");
        DamageSourceHandle.translateLegacyName("sweetBerryBush", "sweet_berry_bush");
        DamageSourceHandle.translateLegacyName("fallingBlock", "falling_block");
        DamageSourceHandle.translateLegacyName("anvil", "falling_anvil");
        DamageSourceHandle.translateLegacyName("fallingStalactite", "falling_stalactite");
        DamageSourceHandle.translateLegacyName("mob", "mob_attack");
        DamageSourceHandle.translateLegacyName("mob", "mob_attack_no_aggro");
        DamageSourceHandle.translateLegacyName("player", "player_attack");
        DamageSourceHandle.translateLegacyName("mob", "mob_projectile");
        DamageSourceHandle.translateLegacyName("onFire", "unattributed_fireball");
        DamageSourceHandle.translateLegacyName("witherSkull", "wither_skull");
        DamageSourceHandle.translateLegacyName("indirectMagic", "indirect_magic");
        DamageSourceHandle.translateLegacyName("explosion.player", "player_explosion");
        DamageSourceHandle.translateLegacyName("badRespawnPoint", "bad_respawn_point");
    }

    public static final class DamageSourceClass
    extends Template.Class<DamageSourceHandle> {
        public final Template.StaticMethod.Converted<DamageSourceHandle> generic = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> genericForEntity = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> mobAttack = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> playerAttack = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> arrowHit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thrownHit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> magicHit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thorns = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> byName = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<DamageSourceHandle> byNameForEntity = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Void> initNameLookup = new Template.StaticMethod();
        public final Template.Method<String> getTranslationIndex = new Template.Method();
        public final Template.Method.Converted<Entity> getEntity = new Template.Method.Converted();
        public final Template.Method<Object> toBukkit = new Template.Method();
        public final Template.Method<VehicleDestroyEvent> createVehicleDestroyEvent = new Template.Method();
        public final Template.Method<VehicleDamageEvent> createVehicleDamageEvent = new Template.Method();
        public final Template.Method<Boolean> isExplosion = new Template.Method();
        public final Template.Method<Boolean> isFireDamage = new Template.Method();
    }
}

