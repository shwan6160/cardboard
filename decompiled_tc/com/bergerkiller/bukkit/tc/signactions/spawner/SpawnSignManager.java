/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.signactions.spawner;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignMetadataHandler;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignSide;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.signactions.spawner.SpawnSign;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnSignManager {
    public static final long SPAWN_WARMUP_TIME = 10000L;
    public static final long SPAWN_LOAD_DEBOUNCE = 30000L;
    private final TrainCarts plugin;
    private final UpdateTask updateTask;
    private final Map<OfflineSignSide, SpawnSign> signs = new HashMap<OfflineSignSide, SpawnSign>();
    private List<SpawnSign> cachedSortedSigns = null;

    public SpawnSignManager(TrainCarts plugin) {
        this.plugin = plugin;
        this.updateTask = new UpdateTask((JavaPlugin)plugin);
    }

    public void load() {
        this.plugin.getOfflineSigns().registerHandler(SpawnSignMetadata.class, new OfflineSignMetadataHandler<SpawnSignMetadata>(){

            @Override
            public void onUpdated(OfflineSignStore store, OfflineSign sign, SpawnSignMetadata oldValue, SpawnSignMetadata newValue) {
                SpawnSign spawnSign = (SpawnSign)SpawnSignManager.this.signs.get(sign.getSide());
                if (spawnSign != null) {
                    spawnSign.updateState(sign, newValue);
                    SpawnSignManager.this.notifyChanged();
                }
            }

            @Override
            public void onAdded(OfflineSignStore store, OfflineSign sign, SpawnSignMetadata metadata) {
                SpawnSign newSpawnSign = new SpawnSign(SpawnSignManager.this.plugin, store, sign, metadata);
                SpawnSignManager.this.signs.put(sign.getSide(), newSpawnSign);
                SpawnSignManager.this.notifyChanged();
            }

            @Override
            public void onRemoved(OfflineSignStore store, OfflineSign sign, SpawnSignMetadata metadata) {
                SpawnSign removedSign = (SpawnSign)SpawnSignManager.this.signs.remove(sign.getSide());
                if (removedSign != null) {
                    removedSign.loadChunksAsyncReset();
                }
                SpawnSignManager.this.notifyChanged();
            }

            @Override
            public SpawnSignMetadata onSignChanged(OfflineSignStore store, OfflineSign oldSign, OfflineSign newSign, SpawnSignMetadata metadata) {
                if (!oldSign.getLine(0).equals(newSign.getLine(0))) {
                    return null;
                }
                if (!oldSign.getLine(1).equals(newSign.getLine(1))) {
                    if (!newSign.getLine(1).toLowerCase(Locale.ENGLISH).startsWith("spawn")) {
                        return null;
                    }
                    SpawnSign.SpawnOptions options = SpawnSign.SpawnOptions.fromOfflineSign(newSign);
                    if (metadata.intervalMillis != options.autoSpawnInterval) {
                        metadata = metadata.setInterval(options.autoSpawnInterval);
                    }
                }
                return metadata;
            }

            @Override
            public void onEncode(DataOutputStream stream, OfflineSign sign, SpawnSignMetadata value) throws IOException {
                stream.writeBoolean(value.active);
                stream.writeLong(value.intervalMillis);
                stream.writeLong(value.autoSpawnStartTime);
            }

            @Override
            public SpawnSignMetadata onDecode(DataInputStream stream, OfflineSign sign) throws IOException {
                boolean active = stream.readBoolean();
                long intervalMillis = stream.readLong();
                long autoSpawnStartTime = stream.readLong();
                return new SpawnSignMetadata(intervalMillis, autoSpawnStartTime, active);
            }
        });
    }

    public void enable() {
        this.updateTask.start(1L, 1L);
    }

    public void disable() {
        this.plugin.getOfflineSigns().unregisterHandler(SpawnSignMetadata.class);
        this.updateTask.stop();
        this.clear();
    }

    public void clear() {
        for (SpawnSign old_sign : this.signs.values()) {
            old_sign.loadChunksAsyncReset();
        }
        this.signs.clear();
        this.cachedSortedSigns = null;
    }

    public SpawnSign get(Block signBlock, boolean isFrontText) {
        return this.signs.get(OfflineSignSide.of(signBlock, isFrontText));
    }

    public SpawnSign create(SignActionEvent signEvent) {
        OfflineSignSide side;
        if (signEvent.getTrackedSign().isRealSign()) {
            side = OfflineSignSide.of(signEvent.getTrackedSign());
            SpawnSign result = this.signs.get(side);
            if (result != null) {
                result.updateUsingEvent(signEvent);
                return result;
            }
        } else {
            side = OfflineSignSide.of(signEvent.getBlock(), true);
        }
        SpawnSign.SpawnOptions options = SpawnSign.SpawnOptions.fromEvent(signEvent);
        SpawnSignMetadata metadata = new SpawnSignMetadata(options.autoSpawnInterval, System.currentTimeMillis() + options.autoSpawnInterval, signEvent.isPowered());
        if (signEvent.getTrackedSign().isRealSign() && options.autoSpawnInterval > 0L) {
            this.plugin.getOfflineSigns().put(signEvent.getTrackedSign(), metadata);
            SpawnSign result = this.signs.get(side);
            if (result == null) {
                throw new IllegalStateException("No SpawnSign was put, onAdded() not called");
            }
            return result;
        }
        return new SpawnSign(this.plugin, null, OfflineSign.fromSign(signEvent.getSign(), side.isFrontText()), metadata);
    }

    public void remove(SignActionEvent signEvent) {
        this.plugin.getOfflineSigns().remove(signEvent.getTrackedSign(), SpawnSignMetadata.class);
    }

    public void remove(SpawnSign sign) {
        this.plugin.getOfflineSigns().remove(sign.getLocation(), sign.isFrontText(), SpawnSignMetadata.class);
    }

    public List<SpawnSign> getSigns() {
        if (this.cachedSortedSigns == null) {
            this.cachedSortedSigns = new ArrayList<SpawnSign>(this.signs.values());
        }
        return this.cachedSortedSigns;
    }

    public void notifyChanged() {
        this.cachedSortedSigns = null;
    }

    private class UpdateTask
    extends Task {
        private long previousTime;

        public UpdateTask(JavaPlugin plugin) {
            super(plugin);
            this.previousTime = Long.MAX_VALUE;
        }

        public void run() {
            long currentTime = System.currentTimeMillis();
            if (this.previousTime != Long.MAX_VALUE) {
                for (SpawnSign pending : SpawnSignManager.this.getSigns()) {
                    long remainingMillis = pending.getRemaining(this.previousTime, currentTime);
                    if (remainingMillis > 30000L) {
                        pending.loadChunksAsyncResetAuto();
                        continue;
                    }
                    if (remainingMillis == 0L) {
                        pending.spawn();
                        continue;
                    }
                    if (remainingMillis > 10000L) continue;
                    pending.loadChunksAsync(1.0 - (double)(remainingMillis - 1000L) / 10000.0);
                }
            }
            this.previousTime = currentTime;
        }
    }

    public static final class SpawnSignMetadata {
        public final long intervalMillis;
        public final long autoSpawnStartTime;
        public final boolean active;

        public SpawnSignMetadata(long intervalMillis, long autoSpawnStartTime, boolean active) {
            this.intervalMillis = intervalMillis;
            this.autoSpawnStartTime = autoSpawnStartTime;
            this.active = active;
        }

        public SpawnSignMetadata setAutoSpawnStart(long timestamp) {
            return new SpawnSignMetadata(this.intervalMillis, timestamp, this.active);
        }

        public SpawnSignMetadata setActive(boolean active) {
            return new SpawnSignMetadata(this.intervalMillis, this.autoSpawnStartTime, active);
        }

        public SpawnSignMetadata setInterval(long intervalMillis) {
            return new SpawnSignMetadata(intervalMillis, this.autoSpawnStartTime, this.active);
        }
    }
}

