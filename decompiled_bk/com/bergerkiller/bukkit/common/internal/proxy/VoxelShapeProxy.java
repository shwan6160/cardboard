/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VoxelShapeProxy {
    public static final VoxelShapeProxy EMPTY = new VoxelShapeProxy(Collections.emptyList());
    private static final DuplexConverter<Object, AABBHandle> toAABBHandle = (DuplexConverter)LogicUtil.unsafeCast(Conversion.findDuplex(CommonUtil.getClass("net.minecraft.world.phys.AxisAlignedBB"), AABBHandle.class));
    private final List<AABBHandle> _aabb;

    private VoxelShapeProxy(List<AABBHandle> aabbObjectHandles) {
        this._aabb = aabbObjectHandles;
    }

    public static VoxelShapeProxy fromAABBHandles(List<AABBHandle> aabbObjectHandles) {
        return new VoxelShapeProxy(aabbObjectHandles);
    }

    public static VoxelShapeProxy fromNMSAABB(List<?> aabbObjects) {
        return new VoxelShapeProxy(new ConvertingList<AABBHandle>(aabbObjects, toAABBHandle));
    }

    public double traceXAxis(AABBHandle boundingBox, double coordinate) {
        int l = this._aabb.size();
        for (int k = 0; k < l; ++k) {
            coordinate = this._aabb.get(k).calcSomeX(boundingBox, coordinate);
        }
        return coordinate;
    }

    public double traceYAxis(AABBHandle boundingBox, double coordinate) {
        int l = this._aabb.size();
        for (int k = 0; k < l; ++k) {
            coordinate = this._aabb.get(k).calcSomeY(boundingBox, coordinate);
        }
        return coordinate;
    }

    public double traceZAxis(AABBHandle boundingBox, double coordinate) {
        int l = this._aabb.size();
        for (int k = 0; k < l; ++k) {
            coordinate = this._aabb.get(k).calcSomeZ(boundingBox, coordinate);
        }
        return coordinate;
    }

    public boolean isEmpty() {
        return this._aabb.isEmpty();
    }

    public List<AABBHandle> getCubes() {
        return this._aabb;
    }

    public AABBHandle getBoundingBox() {
        if (this._aabb.isEmpty()) {
            throw new UnsupportedOperationException("No bounds for empty shape.");
        }
        if (this._aabb.size() == 1) {
            return this._aabb.get(0);
        }
        Iterator<AABBHandle> iter = this._aabb.iterator();
        AABBHandle first = iter.next();
        double minX = first.getMinX();
        double minY = first.getMinY();
        double minZ = first.getMinZ();
        double maxX = first.getMaxX();
        double maxY = first.getMaxY();
        double maxZ = first.getMaxZ();
        while (iter.hasNext()) {
            AABBHandle other = iter.next();
            minX = Math.min(minX, other.getMinX());
            minY = Math.min(minY, other.getMinY());
            minZ = Math.min(minZ, other.getMinZ());
            maxX = Math.max(maxX, other.getMaxX());
            maxY = Math.max(maxY, other.getMaxY());
            maxZ = Math.max(maxZ, other.getMaxZ());
        }
        return AABBHandle.createNew(minX, minY, minZ, maxX, maxY, maxZ);
    }
}

