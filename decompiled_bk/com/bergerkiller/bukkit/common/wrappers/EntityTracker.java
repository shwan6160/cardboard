/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityTracker
extends BasicWrapper<EntityTrackerHandle> {
    public EntityTracker(Object entityTrackerHandle) {
        this.setHandle(EntityTrackerHandle.createHandle(entityTrackerHandle));
    }

    public void sendPacket(Entity entity, CommonPacket packet) {
        ((EntityTrackerHandle)this.handle).sendPacketToEntity(entity, packet);
    }

    public void sendPacket(Entity entity, Object packet) {
        this.sendPacket(entity, new CommonPacket(packet));
    }

    public void removeViewer(Player player) {
        for (EntityTrackerEntryHandle entry : ((EntityTrackerHandle)this.handle).getEntries()) {
            entry.removeViewer(player);
        }
    }

    public void updateViewer(Player player) {
        for (EntityTrackerEntryHandle entry : ((EntityTrackerHandle)this.handle).getEntries()) {
            if (entry.getEntity().getBukkitEntity() == player) continue;
            entry.updatePlayer(player);
        }
    }

    public void startTracking(Entity entity) {
        ((EntityTrackerHandle)this.handle).trackEntity(entity);
    }

    public void stopTracking(Entity entity) {
        ((EntityTrackerHandle)this.handle).untrackEntity(entity);
    }

    public EntityTrackerEntryHandle setEntry(Entity entity, EntityTrackerEntryHandle entityTrackerEntry) {
        EntityHandle.T.setTrackerEntry.invoke(HandleConversion.toEntityHandle(entity), entityTrackerEntry);
        return ((EntityTrackerHandle)this.handle).putEntry(entity.getEntityId(), entityTrackerEntry);
    }

    public EntityTrackerEntryHandle getEntry(Entity entity) {
        return this.getEntry(entity.getEntityId());
    }

    public EntityTrackerEntryHandle getEntry(int entityId) {
        return ((EntityTrackerHandle)this.handle).getEntry(entityId);
    }

    public EntityTrackerEntryHandle removeEntry(int entityId) {
        return ((EntityTrackerHandle)this.handle).putEntry(entityId, null);
    }

    public boolean containsEntry(EntityTrackerEntryHandle entityTrackerEntry) {
        return ((EntityTrackerHandle)this.handle).getEntries().contains(entityTrackerEntry);
    }
}

