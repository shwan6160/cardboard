/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class IntVector2 {
    public static final IntVector2 ZERO = new IntVector2(0, 0);
    public final int x;
    public final int z;

    public IntVector2(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    public IntVector2(Location loc) {
        this(loc.getX(), loc.getZ());
    }

    public IntVector2(double x, double z) {
        this(MathUtil.floor(x), MathUtil.floor(z));
    }

    public IntVector2(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public IntVector2 add(int x, int z) {
        return new IntVector2(this.x + x, this.z + z);
    }

    public IntVector2 add(IntVector2 other) {
        return this.add(other.x, other.z);
    }

    public IntVector2 subtract(int x, int z) {
        return new IntVector2(this.x - x, this.z - z);
    }

    public IntVector2 subtract(IntVector2 other) {
        return this.subtract(other.x, other.z);
    }

    public IntVector2 multiply(int x, int z) {
        return new IntVector2(this.x * x, this.z * z);
    }

    public IntVector2 multiply(double x, double z) {
        return new IntVector2((double)this.x * x, (double)this.z * z);
    }

    public IntVector2 multiply(int factor) {
        return this.multiply(factor, factor);
    }

    public IntVector2 multiply(double factor) {
        return this.multiply(factor, factor);
    }

    public IntVector2 multiply(IntVector2 other) {
        return this.multiply(other.x, other.z);
    }

    public IntVector2 abs() {
        return new IntVector2(Math.abs(this.x), Math.abs(this.z));
    }

    public boolean greaterThan(int value) {
        return this.x > value || this.z > value;
    }

    public boolean greaterEqualThan(int value) {
        return this.x >= value || this.z >= value;
    }

    public Chunk toChunk(World world) {
        return world.getChunkAt(this.x, this.z);
    }

    public double midX() {
        return (double)this.x + 0.5;
    }

    public double midZ() {
        return (double)this.z + 0.5;
    }

    public int hashCode() {
        return 31 * this.x + this.z;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof IntVector2) {
            IntVector2 iv3 = (IntVector2)object;
            return iv3.x == this.x && iv3.z == this.z;
        }
        return false;
    }

    public String toString() {
        return "{" + this.x + ", " + this.z + "}";
    }

    public IntVector3 toIntVector3(int y) {
        return new IntVector3(this.x, y, this.z);
    }

    public static IntVector2 of(int x, int z) {
        return new IntVector2(x, z);
    }

    public static IntVector2 read(DataInputStream stream) throws IOException {
        return new IntVector2(stream.readInt(), stream.readInt());
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.x);
        stream.writeInt(this.z);
    }
}

