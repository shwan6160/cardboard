/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.world.entity.player.AbilitiesHandle;
import org.bukkit.entity.Player;

public class PlayerAbilities
extends BasicWrapper<AbilitiesHandle> {
    public PlayerAbilities() {
        this.setHandle(AbilitiesHandle.createNew());
    }

    public PlayerAbilities(Object handle) {
        this.setHandle(AbilitiesHandle.createHandle(handle));
    }

    public boolean isInvulnerable() {
        return ((AbilitiesHandle)this.handle).isInvulnerable();
    }

    public void setInvulnerable(boolean invulnerable) {
        ((AbilitiesHandle)this.handle).setIsInvulnerable(invulnerable);
    }

    public boolean isFlying() {
        return ((AbilitiesHandle)this.handle).isFlying();
    }

    public void setFlying(boolean flying) {
        ((AbilitiesHandle)this.handle).setIsFlying(flying);
    }

    public boolean canFly() {
        return ((AbilitiesHandle)this.handle).isCanFly();
    }

    public void setCanFly(boolean canFly) {
        ((AbilitiesHandle)this.handle).setCanFly(canFly);
    }

    public boolean canInstantlyBuild() {
        return ((AbilitiesHandle)this.handle).isCanInstantlyBuild();
    }

    public void setCanInstantlyBuild(boolean canInstantlyBuild) {
        ((AbilitiesHandle)this.handle).setCanInstantlyBuild(canInstantlyBuild);
    }

    public boolean canBuild() {
        return ((AbilitiesHandle)this.handle).isMayBuild();
    }

    public void setCanBuild(boolean canBuild) {
        ((AbilitiesHandle)this.handle).setMayBuild(canBuild);
    }

    public double getFlySpeed() {
        return ((AbilitiesHandle)this.handle).getFlySpeed();
    }

    public void setFlySpeed(double speed) {
        ((AbilitiesHandle)this.handle).setFlySpeed(speed);
    }

    public float getWalkSpeed() {
        return ((AbilitiesHandle)this.handle).getWalkSpeed();
    }

    public void setWalkSpeed(float speed) {
        ((AbilitiesHandle)this.handle).setWalkSpeed(speed);
    }

    public void update(Player player) {
        PacketUtil.sendPacket(player, PacketType.OUT_ABILITIES.newInstance(this));
    }
}

