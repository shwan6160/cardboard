/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.phys.shapes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.core.DirectionHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.stream.Stream;

@Template.InstanceType(value="net.minecraft.world.phys.shapes.VoxelShape")
public abstract class VoxelShapeHandle
extends Template.Handle {
    public static final VoxelShapeClass T = Template.Class.create(VoxelShapeClass.class, Common.TEMPLATE_RESOLVER);

    public static VoxelShapeHandle createHandle(Object handleInstance) {
        return (VoxelShapeHandle)T.createHandle(handleInstance);
    }

    public static VoxelShapeHandle empty() {
        return VoxelShapeHandle.T.empty.invoke();
    }

    public static Object createRawFromAABB(Object handles) {
        return VoxelShapeHandle.T.createRawFromAABB.invoke(handles);
    }

    public static VoxelShapeHandle fromAABB(AABBHandle aabb) {
        return VoxelShapeHandle.T.fromAABB.invoke(aabb);
    }

    public static VoxelShapeHandle mergeOnlyFirst(VoxelShapeHandle a, VoxelShapeHandle b) {
        return VoxelShapeHandle.T.mergeOnlyFirst.invoke(a, b);
    }

    public static VoxelShapeHandle merge(VoxelShapeHandle a, VoxelShapeHandle b) {
        return VoxelShapeHandle.T.merge.invoke(a, b);
    }

    public static double traceAxis(DirectionHandle.AxisHandle axis, AABBHandle boundingBox, Stream<VoxelShapeHandle> voxels, double coordinate) {
        return VoxelShapeHandle.T.traceAxis.invoke(axis, boundingBox, voxels, coordinate);
    }

    public abstract AABBHandle getBoundingBox();

    public abstract boolean isEmpty();

    public static final class VoxelShapeClass
    extends Template.Class<VoxelShapeHandle> {
        public final Template.StaticMethod.Converted<VoxelShapeHandle> empty = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Object> createRawFromAABB = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<VoxelShapeHandle> fromAABB = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<VoxelShapeHandle> mergeOnlyFirst = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<VoxelShapeHandle> merge = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Double> traceAxis = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<AABBHandle> getBoundingBox = new Template.Method.Converted();
        public final Template.Method<Boolean> isEmpty = new Template.Method();
    }
}

