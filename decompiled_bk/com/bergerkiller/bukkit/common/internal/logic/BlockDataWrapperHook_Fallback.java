/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

class BlockDataWrapperHook_Fallback
extends BlockDataWrapperHook {
    BlockDataWrapperHook_Fallback() {
    }

    @Override
    protected void baseEnable() {
    }

    @Override
    public void hook(Object nmsIBlockData, Object accessor, BlockData blockData) {
    }

    @Override
    public Object getAccessor(Object nmsIBlockData) {
        return null;
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        return accessor;
    }
}

