package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.SulfurCube;

public class CraftSulfurCube extends CraftAbstractCubeMob implements SulfurCube, io.papermc.paper.entity.PaperShearable, io.papermc.paper.entity.PaperBucketable {

    public CraftSulfurCube(CraftServer server, net.minecraft.world.entity.monster.cubemob.SulfurCube entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.cubemob.SulfurCube getHandle() {
        return (net.minecraft.world.entity.monster.cubemob.SulfurCube) this.entity;
    }

    @Override
    public int getFuseTicks() {
        return this.getHandle().getFuse();
    }

    @Override
    public void setFuseTicks(int ticks) {
        this.getHandle().setFuse(ticks);
    }

    @Override
    public boolean canExplode() {
        return this.getHandle().canExplode();
    }

    @Override
    public boolean ignite(boolean primed) {
        return this.getHandle().primeTime(primed);
    }

    @Override
    public String toString() {
        return "CraftSulfurCube";
    }
}
