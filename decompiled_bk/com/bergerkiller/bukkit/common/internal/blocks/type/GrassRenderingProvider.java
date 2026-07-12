/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.blocks.type;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.World;

public class GrassRenderingProvider
extends BlockRenderProvider {
    private final ArrayList<Material> grass_materials = new ArrayList();
    private final ArrayList<Material> materials = new ArrayList();

    public GrassRenderingProvider() {
        this.grass_materials.add(CommonLegacyMaterials.getLegacyMaterial("GRASS"));
        this.grass_materials.add(CommonLegacyMaterials.getMaterial("GRASS_BLOCK"));
        this.materials.addAll(this.grass_materials);
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("LEAVES"));
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("LEAVES_2"));
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("LONG_GRASS"));
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("VINE"));
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            for (Material m : CommonLegacyMaterials.getAllMaterials()) {
                if (!CommonLegacyMaterials.getMaterialName(m).endsWith("_LEAVES") || this.materials.contains(m)) continue;
                this.materials.add(m);
            }
            this.materials.add(CommonLegacyMaterials.getMaterial("TALL_GRASS"));
            this.materials.add(CommonLegacyMaterials.getMaterial("GRASS"));
            this.materials.add(CommonLegacyMaterials.getMaterial("VINE"));
        }
        for (int i = this.materials.size() - 1; i >= 0; --i) {
            if (this.materials.get(i) != null) continue;
            this.materials.remove(i);
        }
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        Material type = options.getBlockData().getType();
        if (this.grass_materials.contains(type)) {
            options.put("tint", "#9ac460");
        } else if (this.materials.contains(type)) {
            options.put("tint", "#7fa554");
        }
    }

    @Override
    public Collection<Material> getTypes() {
        return this.materials;
    }
}

