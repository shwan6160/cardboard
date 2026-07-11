package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.MagmaCube;

public class CraftMagmaCube extends CraftMob implements MagmaCube, CraftEnemy {

    public CraftMagmaCube(CraftServer server, net.minecraft.world.entity.monster.cubemob.MagmaCube entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.cubemob.MagmaCube getHandle() {
        return (net.minecraft.world.entity.monster.cubemob.MagmaCube) this.entity;
    }

    @Override
    public int getSize() {
        return this.getHandle().getSize();
    }

    @Override
    public void setSize(int size) {
        this.getHandle().setSize(size, /* true */ getHandle().isAlive());
    }

    @Override
    public boolean canWander() {
        return true;
    }

    @Override
    public void setWander(boolean canWander) {
    }
}
