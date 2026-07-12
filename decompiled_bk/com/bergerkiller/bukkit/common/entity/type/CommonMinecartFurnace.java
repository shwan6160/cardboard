/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.PoweredMinecart
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartFurnaceHandle;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CommonMinecartFurnace
extends CommonMinecart<PoweredMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ? Material.getMaterial((String)"FURNACE_MINECART") : Material.getMaterial((String)"POWERED_MINECART");
    public static final int COAL_FUEL = 3600;
    public final DataWatcher.EntityItem<Boolean> metaSmoking = this.getDataItem(MinecartFurnaceHandle.DATA_SMOKING);

    public CommonMinecartFurnace(PoweredMinecart base) {
        super(base);
    }

    public int getFuelTicks() {
        return MinecartFurnaceHandle.T.fuel.getInteger(this.getHandle());
    }

    public void setFuelTicks(int fuelTicks) {
        MinecartFurnaceHandle.T.fuel.setInteger(this.getHandle(), fuelTicks);
    }

    public boolean hasFuel() {
        return this.getFuelTicks() > 0;
    }

    public void addFuelTicks(int amount) {
        this.setFuelTicks(this.getFuelTicks() + amount);
    }

    public Vector getPush() {
        return MinecartFurnaceHandle.T.getPushForce.invoke(this.getHandle());
    }

    public void setPush(double fx, double fy, double fz) {
        MinecartFurnaceHandle.createHandle(this.getHandle()).setPushForce(fx, fy, fz);
    }

    public void setPush(Vector push) {
        this.setPush(push.getX(), push.getY(), push.getZ());
    }

    @Deprecated
    public double getPushX() {
        return MinecartFurnaceHandle.createHandle(this.getHandle()).getPushForceX();
    }

    @Deprecated
    public void setPushX(double pushX) {
        MinecartFurnaceHandle.createHandle(this.getHandle()).setPushForceX(pushX);
    }

    @Deprecated
    public double getPushZ() {
        return MinecartFurnaceHandle.createHandle(this.getHandle()).getPushForceZ();
    }

    @Deprecated
    public void setPushZ(double pushZ) {
        MinecartFurnaceHandle.createHandle(this.getHandle()).setPushForceX(pushZ);
    }

    public boolean isSmoking() {
        return this.metaSmoking.get();
    }

    public void setSmoking(boolean smoking) {
        this.metaSmoking.set(smoking);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.FURNACE, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }
}

