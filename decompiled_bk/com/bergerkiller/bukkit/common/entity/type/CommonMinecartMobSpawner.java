/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.SpawnerMinecart
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartSpawnerHandle;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartMobSpawner
extends CommonMinecart<SpawnerMinecart> {
    private static final Material _SPAWNER_TYPE = CommonCapabilities.MATERIAL_ENUM_CHANGES ? Material.getMaterial((String)"SPAWNER") : Material.getMaterial((String)"MOB_SPAWNER");

    public CommonMinecartMobSpawner(SpawnerMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(_SPAWNER_TYPE, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.MINECART;
    }

    public MobSpawner getMobSpawner() {
        return MinecartSpawnerHandle.T.mobSpawner.get(this.getHandle());
    }
}

