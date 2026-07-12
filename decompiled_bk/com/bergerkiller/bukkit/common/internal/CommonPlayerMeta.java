/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommonPlayerMeta {
    private final LongHashSet visibleChunks = new LongHashSet(441);
    private final WeakReference<Player> playerRef;
    private final Collection<Integer> removeQueue;
    private List<EntityTrackerEntryHook_1_8_to_1_13_2.ViewableLogic> pendingViewerUpdates = Collections.emptyList();
    private int respawnBlindnessEndTick = 0;

    protected CommonPlayerMeta(Player player) {
        this.playerRef = new WeakReference<Player>(player);
        this.removeQueue = ServerPlayerHandle.T.getRemoveQueue.isAvailable() ? ServerPlayerHandle.T.getRemoveQueue.invoke(HandleConversion.toEntityHandle((Entity)player)) : new ArrayList<Integer>();
    }

    public Collection<Integer> getRemoveQueue() {
        return this.removeQueue;
    }

    public void syncRemoveQueue() {
        if (!this.removeQueue.isEmpty()) {
            if (PacketType.OUT_ENTITY_DESTROY.canSupportMultipleEntityIds()) {
                CommonPacket packet = PacketType.OUT_ENTITY_DESTROY.newInstanceMultiple(this.removeQueue);
                this.removeQueue.clear();
                Player p = (Player)this.playerRef.get();
                if (p != null) {
                    PacketUtil.sendPacket(p, packet);
                }
            } else {
                CommonPacket[] packets = new CommonPacket[this.removeQueue.size()];
                int index = 0;
                for (Integer id : this.removeQueue) {
                    packets[index++] = PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(id);
                }
                this.removeQueue.clear();
                Player p = (Player)this.playerRef.get();
                if (p != null) {
                    for (CommonPacket packet : packets) {
                        PacketUtil.sendPacket(p, packet);
                    }
                }
            }
        }
    }

    public Player getPlayer() {
        return (Player)this.playerRef.get();
    }

    public boolean respawnBlindnessCheck(EntityTrackerEntryHook_1_8_to_1_13_2.ViewableLogic viewable) {
        if (this.respawnBlindnessEndTick != 0) {
            int num = this.respawnBlindnessEndTick - CommonUtil.getServerTicks();
            if (num > 0 && CommonPlugin.hasInstance()) {
                if (this.pendingViewerUpdates.isEmpty()) {
                    this.pendingViewerUpdates = new ArrayList<EntityTrackerEntryHook_1_8_to_1_13_2.ViewableLogic>();
                    new ProcessPendingViewerUpdatesTask().start(num);
                }
                this.pendingViewerUpdates.add(viewable);
                return false;
            }
            this.respawnBlindnessEndTick = 0;
        }
        return true;
    }

    public void initiateRespawnBlindness() {
        this.respawnBlindnessEndTick = CommonUtil.getServerTicks() + 5;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public void clearVisibleChunks() {
        LongHashSet longHashSet = this.visibleChunks;
        synchronized (longHashSet) {
            this.visibleChunks.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public boolean isChunkVisible(int chunkX, int chunkZ) {
        LongHashSet longHashSet = this.visibleChunks;
        synchronized (longHashSet) {
            return this.visibleChunks.contains(chunkX, chunkZ);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public void setChunksAsVisible(int[] chunkX, int[] chunkZ) {
        if (chunkX.length != chunkZ.length) {
            throw new IllegalArgumentException("Chunk X and Z coordinate count is not the same");
        }
        LongHashSet longHashSet = this.visibleChunks;
        synchronized (longHashSet) {
            for (int i = 0; i < chunkX.length; ++i) {
                this.visibleChunks.add(chunkX[i], chunkZ[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public void setChunkVisible(int chunkX, int chunkZ, boolean visible) {
        LongHashSet longHashSet = this.visibleChunks;
        synchronized (longHashSet) {
            if (visible) {
                this.visibleChunks.add(chunkX, chunkZ);
            } else {
                this.visibleChunks.remove(chunkX, chunkZ);
            }
        }
    }

    private final class ProcessPendingViewerUpdatesTask
    extends Task {
        public ProcessPendingViewerUpdatesTask() {
            super(CommonPlugin.getInstance());
        }

        @Override
        public void run() {
            Player viewer = CommonPlayerMeta.this.getPlayer();
            List pendingUpdates = CommonPlayerMeta.this.pendingViewerUpdates;
            CommonPlayerMeta.this.pendingViewerUpdates = Collections.emptyList();
            if (viewer != null && PlayerInstancePhase.of(viewer).isConnected()) {
                pendingUpdates.forEach(viewable -> viewable.handleRespawnBlindness(viewer));
            }
        }
    }
}

