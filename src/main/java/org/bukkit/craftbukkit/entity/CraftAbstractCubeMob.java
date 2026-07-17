package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.AbstractCubeMob;

public class CraftAbstractCubeMob extends CraftAgeable implements AbstractCubeMob {

    public CraftAbstractCubeMob(CraftServer server, net.minecraft.world.entity.monster.cubemob.AbstractCubeMob entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.cubemob.AbstractCubeMob getHandle() {
        return (net.minecraft.world.entity.monster.cubemob.AbstractCubeMob) this.entity;
    }

    @Override
    public int getSize() {
        return this.getHandle().getSize();
    }

    @Override
    public void setSize(int size) {
        this.getHandle().setSize(size, getHandle().isAlive());
    }

    @Override
    public boolean canWander() {
        return true;
    }

    @Override
    public void setWander(boolean canWander) {
    }
}
