/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.offline;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public final class OfflineBlock {
    private final OfflineWorld world;
    private final IntVector3 position;

    protected OfflineBlock(OfflineWorld world, IntVector3 position) {
        this.world = world;
        this.position = position;
    }

    public static OfflineBlock of(Block block) {
        return new OfflineBlock(OfflineWorld.of(block.getWorld()), new IntVector3(block));
    }

    public OfflineWorld getWorld() {
        return this.world;
    }

    public UUID getWorldUUID() {
        return this.world.getUniqueId();
    }

    public IntVector3 getPosition() {
        return this.position;
    }

    public int getX() {
        return this.position.x;
    }

    public int getY() {
        return this.position.y;
    }

    public int getZ() {
        return this.position.z;
    }

    public OfflineBlock getRelative(int dx, int dy, int dz) {
        return new OfflineBlock(this.world, this.position.add(dx, dy, dz));
    }

    public OfflineBlock getRelative(BlockFace face) {
        return new OfflineBlock(this.world, this.position.add(face));
    }

    public World getLoadedWorld() {
        return this.world.getLoadedWorld();
    }

    public Block getLoadedBlock() {
        return this.world.getLoadedBlockAt(this.position.x, this.position.y, this.position.z);
    }

    public int hashCode() {
        return this.position.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OfflineBlock) {
            OfflineBlock other = (OfflineBlock)o;
            return this.position.isSame(other.position) && this.world == other.world;
        }
        return false;
    }

    public boolean isLoadedBlock(Block block) {
        return this.position.x == block.getX() && this.position.y == block.getY() && this.position.z == block.getZ() && this.world.getLoadedWorld() == block.getWorld();
    }

    public String toString() {
        return "{world=" + this.world + ", x=" + this.position.x + ", y=" + this.position.y + ", z=" + this.position.z + "}";
    }

    public static OfflineBlock readFrom(DataInputStream stream) throws IOException {
        OfflineWorld world = OfflineWorld.of(StreamUtil.readUUID(stream));
        IntVector3 position = IntVector3.read(stream);
        return new OfflineBlock(world, position);
    }

    public static void writeTo(DataOutputStream stream, OfflineBlock block) throws IOException {
        StreamUtil.writeUUID(stream, block.getWorldUUID());
        block.getPosition().write(stream);
    }
}

