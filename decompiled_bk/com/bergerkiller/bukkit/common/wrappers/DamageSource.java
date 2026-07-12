/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleDestroyEvent
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.world.damagesource.DamageSourceHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class DamageSource
extends BasicWrapper<DamageSourceHandle> {
    @Deprecated
    protected DamageSource(Object damageSource) {
        this.setHandle(DamageSourceHandle.createHandle(damageSource));
    }

    protected DamageSource(DamageSourceHandle damageSourceHandle) {
        this.setHandle(damageSourceHandle);
    }

    public VehicleDestroyEvent createVehicleDestroyEvent(Vehicle vehicle, Entity attacker) {
        return ((DamageSourceHandle)this.handle).createVehicleDestroyEvent(vehicle, attacker);
    }

    public VehicleDestroyEvent createVehicleDestroyEvent(Vehicle vehicle) {
        return ((DamageSourceHandle)this.handle).createVehicleDestroyEvent(vehicle);
    }

    public VehicleDamageEvent createVehicleDamageEvent(Vehicle vehicle, Entity attacker, double damage) {
        return ((DamageSourceHandle)this.handle).createVehicleDamageEvent(vehicle, attacker, damage);
    }

    public VehicleDamageEvent createVehicleDamageEvent(Vehicle vehicle, double damage) {
        return ((DamageSourceHandle)this.handle).createVehicleDamageEvent(vehicle, damage);
    }

    public boolean isFireDamage() {
        return ((DamageSourceHandle)this.handle).isFireDamage();
    }

    public boolean isExplosive() {
        return ((DamageSourceHandle)this.handle).isExplosion();
    }

    @Override
    public String toString() {
        return ((DamageSourceHandle)this.handle).getTranslationIndex();
    }

    public Entity getEntity() {
        return ((DamageSourceHandle)this.handle).getEntity();
    }

    public static DamageSource getForHandle(Object damageSource) {
        return new DamageSource(damageSource);
    }
}

