/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

public class HeightMapProxy_1_12_2 {
    public final Object chunk;
    private final int[] heightMap;

    public HeightMapProxy_1_12_2(Object chunkHandle, int[] heightMap) {
        this.chunk = chunkHandle;
        this.heightMap = heightMap;
    }

    public int getHeight(int x, int z) {
        return this.heightMap[z << 4 | x];
    }

    public void setHeight(int x, int z, int height) {
        this.heightMap[z << 4 | x] = height;
    }
}

