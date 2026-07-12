/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import org.bukkit.Material;

public abstract class MaterialBooleanProperty
extends MaterialProperty<Boolean> {
    public Collection<Material> getMaterials() {
        ArrayList<Material> mats = new ArrayList<Material>(20);
        for (Material mat : MaterialUtil.getAllMaterials()) {
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES && MaterialUtil.isLegacyType(mat) || !Boolean.TRUE.equals(this.get(mat))) continue;
            mats.add(mat);
        }
        return Collections.unmodifiableCollection(mats);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Material material : this.getMaterials()) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(material.toString().toLowerCase(Locale.ENGLISH));
        }
        return builder.toString();
    }
}

