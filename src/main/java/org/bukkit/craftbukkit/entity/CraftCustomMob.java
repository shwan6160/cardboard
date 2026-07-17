package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import net.minecraft.world.entity.Mob;

public class CraftCustomMob extends CraftMob {
    public CraftCustomMob(CraftServer server, Mob entity) {
        super(server, entity);
    }
}
