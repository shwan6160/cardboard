/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.MaterialBooleanProperty;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.bukkit.Material;

public class MaterialTypeProperty
extends MaterialBooleanProperty {
    private final Collection<Material> allowedTypes;
    private final IntHashMap<Boolean> ordinals = new IntHashMap();
    private final IntHashMap<Boolean> legacyOrdinals = new IntHashMap();

    public MaterialTypeProperty(MaterialTypeProperty ... properties) {
        HashSet<Material> elems = new HashSet<Material>();
        for (MaterialTypeProperty prop : properties) {
            for (Material mat : prop.allowedTypes) {
                elems.add(mat);
            }
        }
        this.allowedTypes = Collections.unmodifiableList(Arrays.asList(elems.toArray(new Material[0])));
        this.buildOrdinals();
    }

    public MaterialTypeProperty(Material ... allowedMaterials) {
        this.allowedTypes = Collections.unmodifiableList(Arrays.asList((Material[])allowedMaterials.clone()));
        this.buildOrdinals();
    }

    public MaterialTypeProperty(String ... allowedMaterials) {
        ArrayList<Material> mats = new ArrayList<Material>(allowedMaterials.length);
        for (String name : allowedMaterials) {
            Material mat = MaterialUtil.getMaterial(name);
            if (mat == null) continue;
            mats.add(mat);
        }
        this.allowedTypes = Collections.unmodifiableList(Arrays.asList(LogicUtil.toArray(mats, Material.class)));
        this.buildOrdinals();
    }

    private void buildOrdinals() {
        for (Material type : this.allowedTypes) {
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES && CommonLegacyMaterials.isLegacy(type)) {
                this.legacyOrdinals.put(CommonLegacyMaterials.getOrdinal(type), Boolean.TRUE);
                continue;
            }
            this.ordinals.put(CommonLegacyMaterials.getOrdinal(type), Boolean.TRUE);
        }
    }

    @Override
    public Boolean get(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES && CommonLegacyMaterials.isLegacy(type)) {
            return this.legacyOrdinals.contains(CommonLegacyMaterials.getOrdinal(type));
        }
        return this.ordinals.contains(CommonLegacyMaterials.getOrdinal(type));
    }

    @Override
    public Boolean get(BlockData blockData) {
        return this.ordinals.contains(CommonLegacyMaterials.getOrdinal(blockData.getType())) || CommonCapabilities.MATERIAL_ENUM_CHANGES && this.legacyOrdinals.contains(CommonLegacyMaterials.getOrdinal(blockData.getLegacyType())) && blockData.hasLegacyType();
    }

    @Override
    public Collection<Material> getMaterials() {
        return this.allowedTypes;
    }
}

