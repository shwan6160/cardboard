/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class IntVector3
implements Comparable<IntVector3> {
    public static final IntVector3 ZERO = new IntVector3(0, 0, 0);
    public final int x;
    public final int y;
    public final int z;

    public IntVector3(Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    public IntVector3(Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public IntVector3(double x, double y, double z) {
        this(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
    }

    public IntVector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return "{" + this.x + ", " + this.y + ", " + this.z + "}";
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof IntVector3) {
            IntVector3 other = (IntVector3)object;
            return this.x == other.x && this.y == other.y && this.z == other.z;
        }
        return false;
    }

    public boolean isSame(IntVector3 other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public int hashCode() {
        int result = this.x;
        result = result * 127 + this.y;
        result = result * 8191 + this.z;
        return result;
    }

    @Override
    public int compareTo(IntVector3 other) {
        return this.y == other.y ? (this.z == other.z ? this.x - other.x : this.z - other.z) : this.y - other.y;
    }

    public IntVector3 subtract(int x, int y, int z) {
        return new IntVector3(this.x - x, this.y - y, this.z - z);
    }

    public IntVector3 subtract(IntVector3 other) {
        return this.subtract(other.x, other.y, other.z);
    }

    public IntVector3 subtract(BlockFace face, int length) {
        return this.subtract(face.getModX() * length, face.getModY() * length, face.getModZ() * length);
    }

    public IntVector3 subtract(BlockFace face) {
        return this.subtract(face.getModX(), face.getModY(), face.getModZ());
    }

    public IntVector3 add(int x, int y, int z) {
        return new IntVector3(this.x + x, this.y + y, this.z + z);
    }

    public IntVector3 add(IntVector3 other) {
        return this.add(other.x, other.y, other.z);
    }

    public IntVector3 add(BlockFace face, int length) {
        return this.add(face.getModX() * length, face.getModY() * length, face.getModZ() * length);
    }

    public IntVector3 add(BlockFace face) {
        return this.add(face.getModX(), face.getModY(), face.getModZ());
    }

    public IntVector3 multiply(int x, int y, int z) {
        return new IntVector3(this.x * x, this.y * y, this.z * z);
    }

    public IntVector3 multiply(double x, double y, double z) {
        return new IntVector3((double)this.x * x, (double)this.y * y, (double)this.z * z);
    }

    public IntVector3 multiply(int factor) {
        return this.multiply(factor, factor, factor);
    }

    public IntVector3 multiply(double factor) {
        return this.multiply(factor, factor, factor);
    }

    public IntVector3 multiply(IntVector3 other) {
        return this.multiply(other.x, other.y, other.z);
    }

    public IntVector3 abs() {
        return new IntVector3(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public boolean greaterThan(int value) {
        return this.x > value || this.y > value || this.z > value;
    }

    public boolean greaterEqualThan(int value) {
        return this.x >= value || this.y >= value || this.z >= value;
    }

    public int getChunkX() {
        return this.x >> 4;
    }

    public int getChunkY() {
        return this.y >> 4;
    }

    public int getChunkZ() {
        return this.z >> 4;
    }

    public Block toBlock(World world) {
        return world.getBlockAt(this.x, this.y, this.z);
    }

    public IntVector2 toChunkCoordinates() {
        return new IntVector2(this.getChunkX(), this.getChunkZ());
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public Vector midVector() {
        return new Vector(this.midX(), this.midY(), this.midZ());
    }

    public double midX() {
        return (double)this.x + 0.5;
    }

    public double midY() {
        return (double)this.y + 0.5;
    }

    public double midZ() {
        return (double)this.z + 0.5;
    }

    public IntVector2 toIntVector2() {
        return new IntVector2(this.x, this.z);
    }

    public static IntVector3 read(DataInputStream stream) throws IOException {
        return new IntVector3(stream.readInt(), stream.readInt(), stream.readInt());
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.x);
        stream.writeInt(this.y);
        stream.writeInt(this.z);
    }

    public static IntVector3 of(int x, int y, int z) {
        return new IntVector3(x, y, z);
    }

    public static IntVector3 coordinatesOf(Block block) {
        return new IntVector3(block.getX(), block.getY(), block.getZ());
    }

    public static IntVector3 blockOf(double x, double y, double z) {
        return new IntVector3(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
    }

    public static IntVector3 blockOf(Vector position) {
        return new IntVector3(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public static IntVector3 blockOf(Location location) {
        return new IntVector3(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}

