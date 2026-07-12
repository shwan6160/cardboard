/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPoint;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CommonPlayer
extends CommonLivingEntity<Player> {
    public CommonPlayer(Player entity) {
        super(entity);
    }

    public String getName() {
        return ((Player)this.entity).getName();
    }

    public String getCustomName() {
        return ((Player)this.entity).getCustomName();
    }

    public void setCustomName(String customName) {
        ((Player)this.entity).setCustomName(customName);
    }

    public boolean isCustomNameVisible() {
        return ((Player)this.entity).isCustomNameVisible();
    }

    public void setCustomNameVisible(boolean visible) {
        ((Player)this.entity).setCustomNameVisible(visible);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        if (!super.teleport(location, cause)) {
            return false;
        }
        WorldUtil.getTracker(this.getWorld()).updateViewer((Player)this.entity);
        return true;
    }

    public void sendPacket(CommonPacket packet) {
        PacketUtil.sendPacket((Player)this.entity, packet);
    }

    public void sendPacket(CommonPacket packet, boolean throughListeners) {
        PacketUtil.sendPacket((Player)this.entity, packet, throughListeners);
    }

    public void flushEntityRemoveQueue() {
        Collection<Integer> ids = this.getEntityRemoveQueue();
        if (ids.isEmpty()) {
            return;
        }
        if (PacketType.OUT_ENTITY_DESTROY.canSupportMultipleEntityIds()) {
            while (ids.size() >= 128) {
                int[] rawIds = new int[127];
                Iterator<Integer> iter = ids.iterator();
                for (int i = 0; i < rawIds.length; ++i) {
                    rawIds[i] = iter.next();
                    iter.remove();
                }
                this.sendPacket(PacketType.OUT_ENTITY_DESTROY.newInstanceMultiple(rawIds));
            }
            this.sendPacket(PacketType.OUT_ENTITY_DESTROY.newInstanceMultiple(ids));
            ids.clear();
        } else {
            for (Integer id : ids) {
                this.sendPacket(PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(id));
            }
            ids.clear();
        }
    }

    public Collection<Integer> getEntityRemoveQueue() {
        return PlayerUtil.getEntityRemoveQueue((Player)this.entity);
    }

    @Deprecated
    public Block getSpawnPoint() {
        PlayerRespawnPoint p = PlayerRespawnPoint.forPlayer((Player)this.getEntity());
        if (p instanceof PlayerRespawnPointNearBlock) {
            return ((PlayerRespawnPointNearBlock)p).getBlock();
        }
        return null;
    }

    @Deprecated
    public void setSpawnPoint(Block spawnPoint) {
        PlayerRespawnPoint.create(spawnPoint).applyToPlayer((Player)this.getEntity());
    }
}

