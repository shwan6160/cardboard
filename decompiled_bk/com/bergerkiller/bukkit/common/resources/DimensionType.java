/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.world.level.dimension.DimensionTypeHandle;

public final class DimensionType
extends BasicWrapper<DimensionTypeHandle> {
    public static final DimensionType OVERWORLD = DimensionType.fromIdFallback(0);
    public static final DimensionType THE_NETHER = DimensionType.fromIdFallback(-1);
    public static final DimensionType THE_END = DimensionType.fromIdFallback(1);

    private DimensionType(DimensionTypeHandle handle) {
        this.setHandle(handle);
    }

    public int getId() {
        return ((DimensionTypeHandle)this.handle).getId();
    }

    public boolean hasSkyLight() {
        return ((DimensionTypeHandle)this.handle).hasSkyLight();
    }

    public Object getDimensionManagerHandle() {
        return ((DimensionTypeHandle)this.handle).getRaw();
    }

    public static DimensionType fromId(int id) {
        switch (id) {
            case 0: {
                return OVERWORLD;
            }
            case -1: {
                return THE_NETHER;
            }
            case 1: {
                return THE_END;
            }
        }
        return DimensionType.fromIdFallback(id);
    }

    public static DimensionType fromDimensionManagerHandle(Object dimensionManagerHandle) {
        if (dimensionManagerHandle == null) {
            return null;
        }
        if (dimensionManagerHandle == OVERWORLD.getDimensionManagerHandle()) {
            return OVERWORLD;
        }
        if (dimensionManagerHandle == THE_END.getDimensionManagerHandle()) {
            return THE_END;
        }
        if (dimensionManagerHandle == THE_NETHER.getDimensionManagerHandle()) {
            return THE_NETHER;
        }
        return new DimensionType(DimensionTypeHandle.createHandle(dimensionManagerHandle));
    }

    private static DimensionType fromIdFallback(int id) {
        DimensionTypeHandle handle = DimensionTypeHandle.fromId(id);
        if (handle != null) {
            return new DimensionType(handle);
        }
        throw new IllegalArgumentException("Invalid dimension id " + id);
    }

    @Override
    public String toString() {
        try {
            return ((DimensionTypeHandle)this.handle).toString();
        }
        catch (Throwable throwable) {
            return "UNKNOWN[" + ((DimensionTypeHandle)this.handle).getRaw().getClass().getName() + "]";
        }
    }

    public static final class Key {
        public static final ResourceKey<DimensionType> OVERWORLD = ResourceCategory.dimension_type.createKey("overworld");
        public static final ResourceKey<DimensionType> THE_NETHER = ResourceCategory.dimension_type.createKey("the_nether");
        public static final ResourceKey<DimensionType> THE_END = ResourceCategory.dimension_type.createKey("the_end");
    }
}

