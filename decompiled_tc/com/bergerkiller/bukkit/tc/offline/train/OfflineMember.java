/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.SignTracker;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

public final class OfflineMember {
    public final OfflineGroup group;
    public final UUID entityUID;
    public final int cx;
    public final int cz;
    public final double motX;
    public final double motY;
    public final double motZ;
    public final List<OfflineDataBlock> actions;
    public final List<OfflineDataBlock> activeSigns;
    public final List<OfflineDataBlock> skippedSigns;

    OfflineMember(OfflineGroup group, UUID entityUID, int cx, int cz, double motX, double motY, double motZ, List<OfflineDataBlock> actions, List<OfflineDataBlock> activeSigns, List<OfflineDataBlock> skippedSigns) {
        this.group = group;
        this.entityUID = entityUID;
        this.cx = cx;
        this.cz = cz;
        this.motX = motX;
        this.motY = motY;
        this.motZ = motZ;
        this.actions = actions;
        this.activeSigns = activeSigns;
        this.skippedSigns = skippedSigns;
    }

    public OfflineMember(OfflineGroup offlineGroup, MinecartMember<?> instance) {
        this.group = offlineGroup;
        CommonEntity entity = instance.getEntity();
        this.entityUID = entity.getUniqueId();
        this.cx = entity.loc.x.chunk();
        this.cz = entity.loc.z.chunk();
        this.motX = entity.vel.getX();
        this.motY = entity.vel.getY();
        this.motZ = entity.vel.getZ();
        this.actions = instance.getTrainCarts().getActionRegistry().saveTracker(instance.getActions());
        this.activeSigns = instance.getTrainCarts().getTrackedSignLookup().serializeUniqueKeys(instance.getSignTracker().getActiveTrackedSigns(), "sign", SignTracker.ActiveSign::getUniqueKey);
        this.skippedSigns = instance.getTrainCarts().getTrackedSignLookup().serializeUniqueKeys(instance.getSignTracker().getSignSkipTracker().getSkippedSigns(), "skipped-sign", RailLookup.TrackedSign::getUniqueKey);
    }

    public boolean isMoving() {
        return Math.abs(this.motX) >= 0.001 || Math.abs(this.motZ) >= 0.001;
    }

    public Minecart findEntity(Chunk chunk, boolean markChunkDirty) {
        for (Entity e : WorldUtil.getEntities((Chunk)chunk)) {
            if (!(e instanceof Minecart) || !e.getUniqueId().equals(this.entityUID)) continue;
            if (markChunkDirty) {
                Util.markChunkDirty(chunk);
            }
            return (Minecart)e;
        }
        return null;
    }

    public Minecart findEntity(TrainCarts plugin, World world, boolean markChunkDirty) {
        Minecart e = this.findEntity(world.getChunkAt(this.cx, this.cz), markChunkDirty);
        if (e != null) {
            return e;
        }
        int radius = 2;
        for (int cx = this.cx - 2; cx <= this.cx + 2; ++cx) {
            for (int cz = this.cz - 2; cz <= this.cz + 2; ++cz) {
                if (cx == this.cx && cz == this.cz || (e = this.findEntity(world.getChunkAt(cx, cz), markChunkDirty)) == null) continue;
                return e;
            }
        }
        Entity byUUID = EntityUtil.getEntity((World)world, (UUID)this.entityUID);
        if (byUUID instanceof Minecart) {
            Chunk chunk;
            if (markChunkDirty && (chunk = EntityHandle.fromBukkit((Entity)byUUID).getCurrentChunk()) != null) {
                Util.markChunkDirty(chunk);
            }
            Location loc = byUUID.getLocation();
            plugin.log(Level.WARNING, this.toString() + " was not found in Chunk Entities, yet was found in World at [" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]");
            return (Minecart)byUUID;
        }
        return null;
    }

    public String toString() {
        return "Cart [" + this.entityUID + "] of train '" + this.group.name + "' at chunk [" + this.cx + ", " + this.cz + "]";
    }

    public MinecartMember<?> create(TrainCarts plugin, World world) {
        Minecart entity = this.findEntity(plugin, world, false);
        if (entity == null || entity.isDead()) {
            return null;
        }
        MinecartMember<?> mm = MinecartMemberStore.convert(plugin, entity);
        if (mm == null) {
            plugin.log(Level.WARNING, this.toString() + " Controller creation failed!");
            return null;
        }
        ((CommonMinecart)mm.getEntity()).setVelocity(new Vector(this.motX, this.motY, this.motZ));
        return mm;
    }

    void load(MinecartMember<?> member) {
        member.getTrainCarts().getActionRegistry().loadTracker(member.getActions(), this.actions);
        for (Object signKey : member.getTrainCarts().getTrackedSignLookup().deserializeUniqueKeys(this.activeSigns)) {
            member.getSignTracker().addOfflineActiveSignKey(signKey);
        }
        for (Object signKey : member.getTrainCarts().getTrackedSignLookup().deserializeUniqueKeys(this.skippedSigns)) {
            member.getSignTracker().addOfflineSkippedSignKey(signKey);
        }
        member.getSignTracker().clearUpdates();
    }
}

