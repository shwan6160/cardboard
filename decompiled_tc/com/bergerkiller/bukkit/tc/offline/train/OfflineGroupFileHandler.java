/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.AsyncTask
 *  com.bergerkiller.bukkit.common.config.DataReader
 *  com.bergerkiller.bukkit.common.config.TempFileOutputStream
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.AsyncTask;
import com.bergerkiller.bukkit.common.config.DataReader;
import com.bergerkiller.bukkit.common.config.TempFileOutputStream;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupFileFormatModern;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupManager;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorld;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class OfflineGroupFileHandler {
    private final OfflineGroupManager manager;
    private final File dataFile;
    private CompletableFuture<Void> currentSaveOperation = CompletableFuture.completedFuture(null);
    private Thread currentSaveRunningThread = null;

    public OfflineGroupFileHandler(OfflineGroupManager manager) {
        this.manager = manager;
        this.dataFile = manager.getTrainCarts().getDataFile(new String[]{"trains.groupdata"});
    }

    public void load() {
        new DataReader(this.dataFile){

            public void read(DataInputStream stream) throws IOException {
                OfflineGroupFileFormatModern.Data data = OfflineGroupFileFormatModern.readAll(stream);
                OfflineGroupFileHandler.this.manager.load(data.worlds);
                MutexZoneCache.loadState(OfflineGroupFileHandler.this.manager.getTrainCarts(), data.root);
            }
        }.read();
    }

    public void save(TrainCarts.SaveMode saveMode) {
        if (!this.currentSaveOperation.isDone()) {
            if (saveMode != TrainCarts.SaveMode.SHUTDOWN) {
                return;
            }
            if (!this.waitForSaveCompletion()) {
                return;
            }
        }
        List<OfflineGroupWorld> worlds = saveMode == TrainCarts.SaveMode.SHUTDOWN ? this.manager.createSnapshot() : OfflineGroupWorld.mergeSnapshots(this.manager.createSnapshot(), OfflineGroupManager.saveAllGroups());
        OfflineGroupFileFormatModern.Data data = new OfflineGroupFileFormatModern.Data(worlds);
        MutexZoneCache.saveState(this.manager.getTrainCarts(), data.root);
        StreamUtil.toUnmodifiableList();
        this.currentSaveOperation = CommonUtil.runCheckedAsync(() -> {
            try {
                this.currentSaveRunningThread = Thread.currentThread();
                try (TempFileOutputStream fileStream = new TempFileOutputStream(this.dataFile);
                     DataOutputStream stream = new DataOutputStream((OutputStream)fileStream);){
                    try {
                        OfflineGroupFileFormatModern.writeAll(stream, data);
                    }
                    catch (Throwable t) {
                        fileStream.close(false);
                        throw t;
                    }
                }
            }
            finally {
                this.currentSaveRunningThread = null;
            }
        }, runnable -> {
            AsyncTask task = new AsyncTask(this, "TrainCarts-OfflineGroupSaver"){
                final /* synthetic */ OfflineGroupFileHandler this$0;
                {
                    this.this$0 = this$0;
                    super(arg0);
                }

                public void run() {
                    runnable.run();
                }
            };
            task.start();
        }).exceptionally(t -> {
            this.manager.getTrainCarts().getLogger().log(Level.SEVERE, "Failed to save offline group data to disk", (Throwable)t);
            return null;
        });
        if (saveMode == TrainCarts.SaveMode.SHUTDOWN) {
            this.waitForSaveCompletion();
        }
    }

    private boolean waitForSaveCompletion() {
        try {
            this.currentSaveOperation.get(30L, TimeUnit.SECONDS);
        }
        catch (TimeoutException ex) {
            this.manager.getTrainCarts().log(Level.SEVERE, "Failed to save group data on plugin shutdown: save timed out");
            Thread t = this.currentSaveRunningThread;
            if (t != null) {
                this.manager.getTrainCarts().log(Level.SEVERE, "Thread Stack:\n  at " + Stream.of(t.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n  at ")));
            }
            return false;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return true;
    }

    static {
        CommonUtil.loadClass(TempFileOutputStream.class);
        CommonUtil.loadClass(OfflineGroupFileFormatModern.class);
    }
}

