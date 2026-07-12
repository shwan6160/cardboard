/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityTypeHandle;
import java.util.IdentityHashMap;
import java.util.Map;

public class BlockStateType
extends BasicWrapper<BlockEntityTypeHandle> {
    private static final Map<Object, BlockStateType> _cache = new IdentityHashMap<Object, BlockStateType>();
    public static final BlockStateType SIGN = BlockStateType.byName("sign");
    public static final BlockStateType HANGING_SIGN = BlockStateType.byName("hanging_sign");

    private BlockStateType(BlockEntityTypeHandle handle) {
        this.setHandle(handle);
    }

    public int getSerializedId() {
        return ((BlockEntityTypeHandle)this.handle).getId();
    }

    public IdentifierHandle getKey() {
        return ((BlockEntityTypeHandle)this.handle).getKey();
    }

    @Override
    public String toString() {
        return "BlockStateType{" + this.getKey().toString() + "}";
    }

    public static BlockStateType bySerializedId(int id) {
        return BlockStateType.fromTileEntityTypesHandle(BlockEntityTypeHandle.getRawById(id));
    }

    public static BlockStateType byName(String name) {
        return BlockStateType.byKey(IdentifierHandle.createNew(name));
    }

    public static BlockStateType byKey(IdentifierHandle key) {
        return BlockStateType.fromTileEntityTypesHandle(BlockEntityTypeHandle.getRawByKey(key));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BlockStateType fromTileEntityTypesHandle(Object nmsTileEntityTypesHandle) {
        if (nmsTileEntityTypesHandle == null) {
            return null;
        }
        Map<Object, BlockStateType> map = _cache;
        synchronized (map) {
            return _cache.computeIfAbsent(nmsTileEntityTypesHandle, raw -> new BlockStateType(BlockEntityTypeHandle.createHandle(raw)));
        }
    }
}

