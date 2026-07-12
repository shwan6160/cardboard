/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartTNTHandle;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartTNT
extends CommonMinecart<ExplosiveMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ? Material.getMaterial((String)"TNT_MINECART") : Material.getMaterial((String)"EXPLOSIVE_MINECART");
    public static final double FAST_MOVEMENT_SQUARED = 0.01;

    public CommonMinecartTNT(ExplosiveMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.TNT, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }

    public void explode() {
        this.explode(this.vel.lengthSquared());
    }

    public void explode(double yieldModifier) {
        MinecartTNTHandle.T.explode.invoke(this.getHandle(), yieldModifier);
    }

    public void primeTNT() {
        MinecartTNTHandle.T.prime.invoke(this.getHandle());
    }

    public boolean isTNTPrimed() {
        return this.getFuseTicks() >= 0;
    }

    public boolean isMovingFast() {
        return this.vel.lengthSquared() > 0.01;
    }

    public int getFuseTicks() {
        return MinecartTNTHandle.T.fuse.getInteger(this.getHandle());
    }

    public void setFuseTicks(int fuseTicks) {
        MinecartTNTHandle.T.fuse.setInteger(this.getHandle(), fuseTicks);
    }
}

