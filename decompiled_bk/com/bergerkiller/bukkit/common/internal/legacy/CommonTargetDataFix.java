/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Redstone
 */
package com.bergerkiller.bukkit.common.internal.legacy;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

@Deprecated
public class CommonTargetDataFix
extends MaterialData
implements Redstone {
    public CommonTargetDataFix(Material target_material_type, byte data) {
        super(target_material_type, data);
    }

    public boolean isPowered() {
        return this.getData() > 0;
    }

    public CommonTargetDataFix clone() {
        return (CommonTargetDataFix)super.clone();
    }
}

