/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.DebugUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.helper.AttachmentUpdateTransformHelper;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.player.TrainCartsAttachmentViewerMap;
import com.bergerkiller.bukkit.tc.controller.player.network.PacketQueue;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class TrainUpdateController
implements LibraryComponent {
    private final TrainCarts plugin;
    private int tickUpdateDivider = 1;
    private int tickUpdateNow = 0;
    private boolean ticking = true;
    private double realtimeFactor = 1.0;
    private TrainUpdateTask updateTask = null;
    private TrainNetworkSyncTask networkSyncTask = null;
    private TrainFullSyncTask fullSyncTask = null;
    private AttachmentUpdateTransformHelper updateTransformHelper;

    public TrainUpdateController(TrainCarts plugin) {
        this.plugin = plugin;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public double getRealtimeFactor() {
        return this.realtimeFactor;
    }

    public int getTickDivider() {
        return this.tickUpdateDivider;
    }

    public void setTickDivider(int divider) {
        this.tickUpdateDivider = divider;
    }

    public void step(int number) {
        this.tickUpdateNow += number;
    }

    public void enable() {
        this.updateTask = new TrainUpdateTask((JavaPlugin)this.plugin);
        this.networkSyncTask = new TrainNetworkSyncTask();
        if (Common.evaluateMCVersion((String)">=", (String)"1.11")) {
            this.updateTask.start(1L, 1L);
            this.networkSyncTask.start(1L, 1L);
        } else {
            this.fullSyncTask = new TrainFullSyncTask();
            this.fullSyncTask.start(1L, 1L);
        }
        this.updateTransformHelper = AttachmentUpdateTransformHelper.create(1);
    }

    public void startUpdatingAttachments() {
        this.updateTransformHelper = AttachmentUpdateTransformHelper.create(TCConfig.attachmentTransformParallelism);
    }

    public void disable() {
        Task.stop((Task)this.updateTask);
        this.updateTask = null;
        Task.stop((Task)this.networkSyncTask);
        this.networkSyncTask = null;
        Task.stop((Task)this.fullSyncTask);
        this.fullSyncTask = null;
    }

    public void computeAttachmentTransform(Attachment attachment, Matrix4x4 initialTransform) {
        this.updateTransformHelper.startAndFinish(attachment, initialTransform);
    }

    public void syncPositions(Collection<MinecartGroup> groups) {
        this.syncPositions(groups, true);
    }

    public void syncPositions(MinecartMember<?> member) {
        try {
            member.getAttachments().syncPrePositionUpdate(this.updateTransformHelper);
        }
        catch (Throwable ex) {
            this.syncFail(member.getGroup(), ex);
        }
        this.updateTransformHelper.finish();
        try {
            member.getAttachments().syncMovement(true);
        }
        catch (Throwable t) {
            this.syncFail(member.getGroup(), t);
        }
    }

    private void syncPositions(Collection<MinecartGroup> groups, boolean positionSync) {
        for (MinecartGroup group : groups) {
            try {
                group.getAttachments().syncPrePositionUpdate(this.updateTransformHelper);
            }
            catch (Throwable ex) {
                this.syncFail(group, ex);
            }
        }
        this.updateTransformHelper.finish();
        for (MinecartGroup group : groups) {
            try {
                if (positionSync) {
                    group.getAttachments().syncPositionAbsolute();
                    continue;
                }
                group.getAttachments().syncPostPositionUpdate();
            }
            catch (Throwable t) {
                this.syncFail(group, t);
            }
        }
    }

    private void syncFail(MinecartGroup group, Throwable ex) {
        TrainProperties p = group.getProperties();
        this.plugin.log(Level.SEVERE, "Failed to synchronize a network controller of train '" + p.getTrainName() + "' at " + p.getLocation() + ":");
        this.plugin.handle(ex);
    }

    private class TrainNetworkSyncTask
    extends Task {
        public TrainNetworkSyncTask() {
            super((JavaPlugin)TrainUpdateController.this.plugin);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            TrainCartsAttachmentViewerMap viewerMap = TrainUpdateController.this.plugin.getAttachmentViewers();
            viewerMap.forAllPacketQueues(PacketQueue::syncBegin);
            try (ImplicitlySharedSet groups = MinecartGroupStore.getGroups().clone();){
                TrainUpdateController.this.syncPositions((Collection)groups, false);
                TrainUpdateController.this.plugin.getAttachmentViewers().updateCollisionSurfaces();
                TrainUpdateController.this.plugin.getEffectLoopPlayerController().updateSync();
            }
            finally {
                viewerMap.forAllPacketQueues(PacketQueue::syncEnd);
            }
        }
    }

    private class TrainUpdateTask
    extends Task {
        int ctr;
        long lastTick;

        public TrainUpdateTask(JavaPlugin plugin) {
            super(plugin);
            this.ctr = 0;
            this.lastTick = Long.MAX_VALUE;
        }

        public void run() {
            long currentTime = System.currentTimeMillis();
            if (this.lastTick > currentTime) {
                TrainUpdateController.this.realtimeFactor = 1.0;
            } else {
                TrainUpdateController.this.realtimeFactor = MathUtil.clamp((double)((double)(currentTime - this.lastTick) / 50.0), (double)0.05, (double)5.0);
            }
            this.lastTick = currentTime;
            if (++this.ctr >= TrainUpdateController.this.tickUpdateDivider) {
                this.ctr = 0;
                TrainUpdateController.this.tickUpdateNow++;
            }
            if (TrainUpdateController.this.tickUpdateNow > 0) {
                TrainUpdateController.this.tickUpdateNow--;
                TrainUpdateController.this.ticking = true;
            } else {
                TrainUpdateController.this.ticking = false;
            }
            MinecartGroupStore.doFixedTick(TrainUpdateController.this.plugin);
        }
    }

    private class TrainFullSyncTask
    extends Task {
        public TrainFullSyncTask() {
            super((JavaPlugin)TrainUpdateController.this.plugin);
        }

        public void run() {
            TrainUpdateController.this.updateTask.run();
            TrainUpdateController.this.networkSyncTask.run();
        }
    }

    private static class DebugArtificialLag
    extends Task {
        private final Random r = new Random();

        public DebugArtificialLag(JavaPlugin plugin) {
            super(plugin);
        }

        public void run() {
            int lag = DebugUtil.getIntValue((String)"lag", (int)0) + this.r.nextInt(DebugUtil.getIntValue((String)"jitter", (int)0) + 1);
            if (lag > 0) {
                try {
                    Thread.sleep(lag);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
    }
}

