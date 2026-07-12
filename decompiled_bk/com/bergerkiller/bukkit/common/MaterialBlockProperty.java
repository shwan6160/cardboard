/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Material;

public abstract class MaterialBlockProperty<T>
extends MaterialProperty<T> {
    @Override
    public T get(Material material) {
        return this.get(BlockData.fromMaterial(material));
    }

    @Override
    public abstract T get(BlockData var1);
}

