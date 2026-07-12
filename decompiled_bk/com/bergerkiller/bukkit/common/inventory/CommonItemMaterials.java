/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import org.bukkit.Material;

public class CommonItemMaterials {
    public static final Material SKULL = MaterialUtil.getFirst("PLAYER_HEAD", "LEGACY_SKULL_ITEM");
    public static final Material FILLED_MAP = CommonCapabilities.MATERIAL_ENUM_CHANGES ? CommonLegacyMaterials.getMaterial("FILLED_MAP") : CommonLegacyMaterials.getLegacyMaterial("MAP");
    public static final Material EMPTY_MAP = MaterialUtil.getFirst("MAP", "LEGACY_EMPTY_MAP");
    public static final Material STICK = MaterialUtil.getFirst("STICK", "LEGACY_STICK");
}

