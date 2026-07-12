/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Color
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.debug.particles.DebugParticles;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MutexZonePath
extends MutexZone {
    private final TrainCarts plugin;
    protected final MutexZoneCacheWorld.PathingSignKey key;
    private MutexZoneCacheWorld world;
    private RailLookup.TrackedSign sign;
    private final double spacing;
    private final double maxDistance;
    private final Set<IntVector3> blocks = new LinkedHashSet<IntVector3>(128);
    private int tickLastUsed;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int minCX;
    private int minCZ;
    private int maxCX;
    private int maxCZ;
    private final List<OrientedBoundingBox> cubes = new ArrayList<OrientedBoundingBox>(128);

    protected MutexZonePath(TrainCarts plugin, RailLookup.TrackedSign sign, TrainProperties trainProperties, OptionsBuilder options) {
        this(plugin, OfflineBlock.of((Block)sign.signBlock), MutexZoneCacheWorld.PathingSignKey.of(sign.getUniqueKey(), trainProperties), sign, options);
    }

    private MutexZonePath(TrainCarts plugin, OfflineBlock signBlock, MutexZoneCacheWorld.PathingSignKey key, RailLookup.TrackedSign sign, OptionsBuilder options) {
        super(signBlock, true, options.type, options.name, options.statement);
        this.plugin = plugin;
        this.key = key;
        this.sign = sign;
        this.spacing = options.spacing;
        this.maxDistance = options.maxDistance;
        this.tickLastUsed = -1;
    }

    public static List<MutexZonePath> readAll(TrainCarts plugin, OfflineDataBlock root) {
        List<OfflineDataBlock> pathDataBlockList = root.findChildren("path-mutex");
        if (pathDataBlockList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<MutexZonePath> paths = new ArrayList<MutexZonePath>(pathDataBlockList.size());
        for (OfflineDataBlock pathDataBlock : pathDataBlockList) {
            MutexZonePath path;
            block13: {
                try {
                    DataInputStream stream = pathDataBlock.readData();
                    try {
                        int version = Util.readVariableLengthInt(stream);
                        if (version == 1) {
                            OfflineBlock signBlock = OfflineBlock.readFrom((DataInputStream)stream);
                            Optional<MutexZoneCacheWorld.PathingSignKey> key = MutexZoneCacheWorld.PathingSignKey.readFrom(plugin, stream);
                            if (!key.isPresent()) continue;
                            OptionsBuilder options = MutexZonePath.createOptions();
                            options.type(MutexZoneSlotType.readFrom(stream));
                            options.name(stream.readUTF());
                            options.statement(stream.readUTF());
                            options.spacing(stream.readDouble());
                            options.maxDistance(stream.readDouble());
                            path = new MutexZonePath(plugin, signBlock, key.get(), null, options);
                            int blockCount = Util.readVariableLengthInt(stream);
                            if (blockCount == 0) {
                                throw new IllegalStateException("Pathing mutex at " + signBlock + " has zero rail blocks");
                            }
                            for (int i = 0; i < blockCount; ++i) {
                                path.addBlock(IntVector3.read((DataInputStream)stream));
                            }
                            break block13;
                        }
                        throw new UnsupportedOperationException("Unsupported data version: " + version);
                    }
                    finally {
                        if (stream == null) continue;
                        stream.close();
                    }
                }
                catch (Throwable t) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load pathing mutex", t);
                }
                continue;
            }
            paths.add(path);
        }
        return Collections.unmodifiableList(paths);
    }

    public void writeTo(OfflineDataBlock root) {
        try {
            root.addChildOrAbort("path-mutex", stream -> {
                Util.writeVariableLengthInt(stream, 1);
                OfflineBlock.writeTo((DataOutputStream)stream, (OfflineBlock)this.signBlock);
                if (!this.key.writeTo(this.plugin, stream)) {
                    throw new OfflineDataBlock.AbortChildException();
                }
                this.type.writeTo(stream);
                stream.writeUTF(this.slot.getName());
                stream.writeUTF(this.statement);
                stream.writeDouble(this.spacing);
                stream.writeDouble(this.getMaxDistance());
                Util.writeVariableLengthInt(stream, this.blocks.size());
                for (IntVector3 block : this.blocks) {
                    block.write(stream);
                }
            });
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save pathing mutex at " + this.signBlock, t);
        }
    }

    public String getTrainName() {
        return this.key.trainProperties.getTrainName();
    }

    public boolean isByGroup(MinecartGroup group) {
        return this.key.trainProperties == group.getProperties();
    }

    @Override
    protected void addToWorld(MutexZoneCacheWorld world) {
        world.byPathingKey.put(this.key, this);
        this.world = world;
    }

    public void remove() {
        if (this.world.byPathingKey.remove(this.key, this)) {
            this.world.remove(this);
        }
    }

    @Override
    public double getSpacing(MinecartGroup group) {
        return this.isByGroup(group) ? 0.0 : this.spacing;
    }

    public double getMaxDistance() {
        return this.maxDistance;
    }

    public void addBlock(IntVector3 block) {
        if (!this.blocks.add(block)) {
            return;
        }
        this.updateBB(block);
        boolean chunksChanged = false;
        if (this.blocks.size() == 1) {
            this.minX = this.maxX = block.x;
            this.minY = this.maxY = block.y;
            this.minZ = this.maxZ = block.z;
            this.minCX = this.maxCX = block.getChunkX();
            this.minCZ = this.maxCZ = block.getChunkZ();
            chunksChanged = true;
        } else {
            if (block.x < this.minX) {
                this.minX = block.x;
            } else if (block.x > this.maxX) {
                this.maxX = block.x;
            }
            if (block.y < this.minY) {
                this.minY = block.y;
            } else if (block.y > this.maxY) {
                this.maxY = block.y;
            }
            if (block.z < this.minZ) {
                this.minZ = block.z;
            } else if (block.z > this.maxZ) {
                this.maxZ = block.z;
            }
            int cx = block.getChunkX();
            int cz = block.getChunkZ();
            if (cx < this.minCX) {
                this.minCX = cx;
                chunksChanged = true;
            } else if (cx > this.maxCX) {
                this.maxCX = cx;
                chunksChanged = true;
            }
            if (cz < this.minCZ) {
                this.minCZ = cz;
                chunksChanged = true;
            } else if (cz > this.maxCZ) {
                this.maxCZ = cz;
                chunksChanged = true;
            }
        }
        if (chunksChanged && this.world != null && this.world.byPathingKey.get(this.key) == this) {
            this.world.addNewChunks(this);
        }
    }

    private void updateBB(IntVector3 coord) {
        this.cubes.add(OrientedBoundingBox.naturalFromTo((Vector)new Vector(coord.x, coord.y, coord.z), (Vector)new Vector((double)coord.x + 1.0, (double)coord.y + 1.0, (double)coord.z + 1.0)));
    }

    @Override
    public boolean containsBlock(IntVector3 block) {
        return this.blocks.contains(block);
    }

    @Override
    public boolean isNearby(IntVector3 block, int radius) {
        return block.x >= this.minX - radius && block.y >= this.minY - radius && block.z >= this.minZ - radius && block.x <= this.maxX + radius && block.y <= this.maxY + radius && block.z <= this.maxZ + radius;
    }

    @Override
    public void forAllContainedChunks(MutexZone.ChunkCoordConsumer action) {
        int chunkMinX = this.minCX;
        int chunkMaxX = this.maxCX;
        int chunkMinZ = this.minCZ;
        int chunkMaxZ = this.maxCZ;
        for (int cz = chunkMinZ; cz <= chunkMaxZ; ++cz) {
            for (int cx = chunkMinX; cx <= chunkMaxX; ++cx) {
                action.accept(cx, cz);
            }
        }
    }

    @Override
    public long showDebugColorSeed() {
        return this.signBlock.hashCode();
    }

    @Override
    public void showDebug(Player player, Color color) {
        DebugParticles particles = DebugParticles.of(player);
        for (IntVector3 block : this.blocks) {
            Vector pos = MathUtil.addToVector((Vector)block.toVector(), (double)0.5, (double)0.5, (double)0.5);
            particles.point(color, pos);
        }
    }

    @Override
    protected void setLeversDown(boolean down) {
        if (this.sign == null) {
            this.sign = this.plugin.getTrackedSignLookup().getTrackedSign(this.key.uniqueKey);
        }
        if (this.sign != null) {
            this.sign.setOutput(down);
        }
    }

    @Override
    public void onUsed(MinecartGroup group) {
        if (this.isByGroup(group)) {
            this.tickLastUsed = CommonUtil.getServerTicks();
        }
    }

    public boolean isExpired(int expireTick) {
        if (this.key.trainProperties.isRemoved()) {
            return true;
        }
        if (!this.key.trainProperties.isLoaded()) {
            this.tickLastUsed = -1;
            return false;
        }
        if (this.tickLastUsed == -1) {
            this.tickLastUsed = CommonUtil.getServerTicks();
            return false;
        }
        return this.tickLastUsed < expireTick;
    }

    @Override
    public double hitTest(double posX, double posY, double posZ, double motX, double motY, double motZ) {
        double result = Double.MAX_VALUE;
        for (OrientedBoundingBox bb : this.cubes) {
            result = Math.min(result, bb.hitTest(posX, posY, posZ, motX, motY, motZ));
        }
        return result;
    }

    public static OptionsBuilder createOptions() {
        return new OptionsBuilder();
    }

    public static final class OptionsBuilder {
        private double spacing = 1.0;
        private double maxDistance = 64.0;
        private MutexZoneSlotType type = MutexZoneSlotType.NORMAL;
        private String name = "";
        private String statement = "";

        private OptionsBuilder() {
        }

        public double spacing() {
            return this.spacing;
        }

        public OptionsBuilder spacing(double spacing) {
            this.spacing = MathUtil.clamp((double)spacing, (double)0.0, (double)TCConfig.maxMutexSize);
            return this;
        }

        public double maxDistance() {
            return this.maxDistance;
        }

        public OptionsBuilder maxDistance(double maxDistance) {
            this.maxDistance = MathUtil.clamp((double)maxDistance, (double)0.0, (double)TCConfig.maxMutexSize);
            return this;
        }

        public OptionsBuilder type(MutexZoneSlotType type) {
            this.type = type;
            return this;
        }

        public OptionsBuilder name(String name) {
            this.name = name;
            return this;
        }

        public OptionsBuilder statement(String statement) {
            this.statement = statement;
            return this;
        }
    }
}

