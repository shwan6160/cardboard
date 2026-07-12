/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.ChunkUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  org.bukkit.Bukkit
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.signactions.spawner;

import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import com.bergerkiller.bukkit.tc.signactions.SignActionSpawn;
import com.bergerkiller.bukkit.tc.signactions.spawner.SpawnSignManager;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpawnSign {
    private final TrainCarts plugin;
    private final OfflineSignStore store;
    private final OfflineBlock location;
    private final boolean frontText;
    private SpawnSignManager.SpawnSignMetadata state;
    private int ticksUntilFreeing = 0;
    private double spawnForce = 0.0;
    private String spawnFormat;
    private LongHashMap<SignSpawnChunk> chunks = new LongHashMap();
    private int num_chunks_loaded = 0;

    SpawnSign(TrainCarts plugin, OfflineSignStore store, OfflineSign sign, SpawnSignManager.SpawnSignMetadata metadata) {
        this.plugin = plugin;
        this.store = store;
        this.location = sign.getBlock();
        this.frontText = sign.isFrontText();
        this.updateState(sign, metadata);
        int center_cx = MathUtil.toChunk((int)this.location.getX());
        int center_cz = MathUtil.toChunk((int)this.location.getZ());
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                int cx = center_cx + dx;
                int cz = center_cz + dz;
                this.chunks.put(cx, cz, (Object)this.createSpawnChunk(cx, cz));
            }
        }
    }

    public TrainCarts getPlugin() {
        return this.plugin;
    }

    void updateState(OfflineSign sign, SpawnSignManager.SpawnSignMetadata metadata) {
        this.spawnForce = SpawnOptions.fromOfflineSign((OfflineSign)sign).launchVelocity;
        this.spawnFormat = sign.getLine(2) + sign.getLine(3);
        this.state = metadata;
    }

    void updateUsingEvent(SignActionEvent event) {
        this.store.verifySign(event.getSign(), this.frontText, SpawnSignManager.SpawnSignMetadata.class);
        boolean active = event.isPowered();
        if (active != this.state.active) {
            this.store.putIfPresent(this.location, this.frontText, this.state.setActive(active));
        }
    }

    public OfflineBlock getLocation() {
        return this.location;
    }

    public boolean isFrontText() {
        return this.frontText;
    }

    public boolean hasInterval() {
        return this.state.intervalMillis > 0L;
    }

    public long getInterval() {
        return this.state.intervalMillis;
    }

    public long getRemaining(long previousTime, long currentTime) {
        if (!this.isActive() || !this.hasInterval()) {
            return Long.MAX_VALUE;
        }
        long numIntervalsSkipped = (currentTime - this.state.autoSpawnStartTime) / this.state.intervalMillis;
        long nextSpawnTimestamp = this.state.autoSpawnStartTime + numIntervalsSkipped * this.state.intervalMillis;
        if (nextSpawnTimestamp <= previousTime) {
            nextSpawnTimestamp += this.state.intervalMillis;
        }
        if (currentTime >= nextSpawnTimestamp) {
            return 0L;
        }
        return nextSpawnTimestamp - currentTime;
    }

    public boolean isActive() {
        return this.state.active;
    }

    public double getSpawnForce() {
        return this.spawnForce;
    }

    public SpawnableGroup getSpawnableGroup() {
        return SpawnableGroup.parse(this.getPlugin(), this.spawnFormat);
    }

    public void resetSpawnTime() {
        if (this.store != null) {
            this.store.putIfPresent(this.location, this.frontText, this.state.setAutoSpawnStart(System.currentTimeMillis() + this.state.intervalMillis));
        }
    }

    public World getWorld() {
        return this.location.getLoadedWorld();
    }

    public void loadChunksAsync(double percent) {
        if (this.getWorld() == null) {
            this.num_chunks_loaded = this.chunks.size();
            return;
        }
        percent = MathUtil.clamp((double)percent, (double)0.0, (double)1.0);
        int num_chunks_loaded_goal = (int)((double)this.chunks.size() * percent);
        for (SignSpawnChunk chunk : this.chunks.getValues()) {
            if (this.num_chunks_loaded >= num_chunks_loaded_goal) break;
            if (chunk.chunk.isNone()) continue;
            chunk.loadAsync();
            ++this.num_chunks_loaded;
        }
    }

    public void loadChunksAsyncReset() {
        for (SignSpawnChunk chunk : this.chunks.getValues()) {
            chunk.close();
        }
        this.num_chunks_loaded = 0;
    }

    public void loadChunksAsyncResetAuto() {
        if (this.ticksUntilFreeing > 0 && --this.ticksUntilFreeing == 0) {
            this.loadChunksAsyncReset();
        }
    }

    public void remove() {
        if (this.store != null) {
            this.store.remove(this.location, this.frontText, SpawnSignManager.SpawnSignMetadata.class);
        }
    }

    public void spawn() {
        Block signBlock = this.location.getLoadedBlock();
        if (signBlock != null) {
            Sign bsign = BlockUtil.getSign((Block)signBlock);
            if (bsign == null) {
                this.store.removeAll(signBlock);
                return;
            }
            if (this.store.verifySign(bsign, this.frontText, SpawnSignManager.SpawnSignMetadata.class) == null) {
                return;
            }
            SignActionEvent event = new SignActionEvent(RailLookup.TrackedSign.forRealSign(bsign, this.frontText, null));
            if (SpawnSign.isValid(event)) {
                this.updateUsingEvent(event);
                this.spawn(event);
            } else {
                this.remove();
            }
        } else {
            this.loadChunksAsyncReset();
        }
    }

    public void spawn(SignActionEvent sign) {
        if (this.store != null && this.store.verifySign(sign.getSign(), this.frontText, SpawnSignManager.SpawnSignMetadata.class) == null) {
            return;
        }
        this.ticksUntilFreeing = 2;
        for (SignSpawnChunk chunk : this.chunks.getValues()) {
            chunk.loadSync();
        }
        SpawnableGroup.SpawnLocationList locs = SignActionSpawn.spawn(this, sign);
        if (locs != null && !locs.locations.isEmpty()) {
            LongHashMap new_chunks = new LongHashMap(this.chunks.size());
            for (SpawnableMember.SpawnLocation loc : locs.locations) {
                int x = MathUtil.toChunk((double)loc.location.getX());
                int z = MathUtil.toChunk((double)loc.location.getZ());
                for (int dx = -2; dx <= 2; ++dx) {
                    for (int dz = -2; dz <= 2; ++dz) {
                        int cx = x + dx;
                        int cz = z + dz;
                        long key = MathUtil.longHashToLong((int)cx, (int)cz);
                        if (new_chunks.contains(key)) continue;
                        SignSpawnChunk chunk = (SignSpawnChunk)this.chunks.remove(key);
                        if (chunk == null) {
                            chunk = this.createSpawnChunk(cx, cz);
                            chunk.loadSync();
                        }
                        new_chunks.put(key, (Object)chunk);
                    }
                }
            }
            for (SignSpawnChunk originalChunk : this.chunks.getValues()) {
                originalChunk.close();
            }
            this.chunks = new_chunks;
            this.num_chunks_loaded = this.chunks.size();
        }
    }

    public void showFailParticles(Color color) {
        Vector pos = MathUtil.addToVector((Vector)this.location.getPosition().toVector(), (double)0.5, (double)0.5, (double)0.5);
        Location loc = pos.toLocation(this.getWorld());
        for (Entity e : WorldUtil.getNearbyEntities((Location)loc, (double)64.0, (double)64.0, (double)64.0)) {
            if (!(e instanceof Player)) continue;
            PlayerUtil.spawnDustParticles((Player)((Player)e), (Vector)pos, (Color)color);
            PlayerUtil.playSound((Player)((Player)e), (Location)loc, (ResourceKey)SoundEffect.EXTINGUISH, (float)0.2f, (float)1.0f);
        }
    }

    private SignSpawnChunk createSpawnChunk(int cx, int cz) {
        if (this.store == null) {
            return new SignSpawnChunkSync(this.location.getWorldUUID(), cx, cz);
        }
        return new SignSpawnChunk(this.location.getWorldUUID(), cx, cz);
    }

    public String toString() {
        long currentTime = System.currentTimeMillis();
        StringBuilder str = new StringBuilder();
        str.append("{");
        str.append("pos=").append(this.location.toString());
        str.append(", interval=").append(this.getInterval());
        str.append(", remaining=").append(this.getRemaining(currentTime, currentTime));
        str.append(", spawnForce=").append(this.getSpawnForce());
        str.append(", spawnable=").append(this.spawnFormat);
        str.append("}");
        return str.toString();
    }

    public static double getSpawnForce(SignActionEvent event) {
        return SpawnOptions.fromEvent((SignActionEvent)event).launchVelocity;
    }

    public static long getSpawnTime(SignActionEvent event) {
        return SpawnOptions.fromEvent((SignActionEvent)event).autoSpawnInterval;
    }

    public static boolean isValid(SignActionEvent event) {
        return event != null && event.getMode() != SignActionMode.NONE && event.isType("spawn");
    }

    private static class SignSpawnChunk {
        private final ForcedChunk chunk = ForcedChunk.none();
        public final UUID worldUUID;
        public final int x;
        public final int z;

        public SignSpawnChunk(UUID worldUUID, int x, int z) {
            this.worldUUID = worldUUID;
            this.x = x;
            this.z = z;
        }

        public void loadSync() {
            World world = Bukkit.getWorld((UUID)this.worldUUID);
            if (world == null) {
                return;
            }
            if (this.chunk.isNone()) {
                this.chunk.move(ChunkUtil.forceChunkLoaded((World)world, (int)this.x, (int)this.z));
            }
            this.chunk.getChunk();
        }

        public void loadAsync() {
            World world;
            if (this.chunk != null && (world = Bukkit.getWorld((UUID)this.worldUUID)) != null) {
                this.chunk.move(ChunkUtil.forceChunkLoaded((World)world, (int)this.x, (int)this.z));
            }
        }

        public void close() {
            this.chunk.close();
        }
    }

    public static class SpawnOptions {
        public final double launchVelocity;
        public final long autoSpawnInterval;

        private SpawnOptions(String secondSignLine) {
            String line = secondSignLine.toLowerCase(Locale.ENGLISH);
            int idx = line.indexOf(32);
            String[] args = idx == -1 ? StringUtil.EMPTY_ARRAY : line.substring(idx + 1).split(" ");
            this.launchVelocity = SpawnOptions.parseVelocity(args);
            this.autoSpawnInterval = SpawnOptions.getAutoSpawnInterval(args);
        }

        public static SpawnOptions fromEvent(SignActionEvent event) {
            return new SpawnOptions(event.getLine(1));
        }

        public static SpawnOptions fromOfflineSign(OfflineSign sign) {
            return new SpawnOptions(sign.getLine(1));
        }

        private static double parseVelocity(String[] args) {
            if (args.length >= 2) {
                if (!args[0].contains(":")) {
                    return Util.parseVelocity(args[0], 0.0);
                }
                return Util.parseVelocity(args[1], 0.0);
            }
            if (args.length >= 1 && !args[0].contains(":")) {
                return Util.parseVelocity(args[0], 0.0);
            }
            return 0.0;
        }

        private static long getAutoSpawnInterval(String[] args) {
            if (args.length >= 2) {
                if (args[1].contains(":")) {
                    return ParseUtil.parseTime((String)args[1]);
                }
                return ParseUtil.parseTime((String)args[0]);
            }
            if (args.length >= 1 && args[0].contains(":")) {
                return ParseUtil.parseTime((String)args[0]);
            }
            return 0L;
        }
    }

    private static class SignSpawnChunkSync
    extends SignSpawnChunk {
        public SignSpawnChunkSync(UUID worldUUID, int x, int z) {
            super(worldUUID, x, z);
        }

        @Override
        public void loadSync() {
            World world = Bukkit.getWorld((UUID)this.worldUUID);
            if (world != null) {
                world.getChunkAt(this.x, this.z);
            }
        }

        @Override
        public void loadAsync() {
        }
    }
}

