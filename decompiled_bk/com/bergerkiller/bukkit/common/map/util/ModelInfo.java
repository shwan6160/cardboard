/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.map.util.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelInfo {
    private transient String name = "unknown";
    protected transient boolean placeholder = false;
    protected String parent = null;
    protected List<ItemModel.Overrides.OverriddenModel> overrides = new ArrayList<ItemModel.Overrides.OverriddenModel>();
    protected String credit;
    protected String __comment;

    public final String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final String getParentName() {
        return this.parent;
    }

    public final String getCredit() {
        if (this.credit != null) {
            return this.credit;
        }
        if (this.__comment != null) {
            return this.__comment;
        }
        return "";
    }

    public final boolean isPlaceholder() {
        return this.placeholder;
    }

    public ItemModel asItemModel() {
        if (this.overrides == null || this.overrides.isEmpty()) {
            return ItemModel.MinecraftModel.of(this.name);
        }
        ItemModel.Overrides overrides = new ItemModel.Overrides();
        overrides.overrides = this.overrides;
        overrides.fallback = ItemModel.MinecraftModel.of(this.name);
        return overrides;
    }

    @Deprecated
    public final List<Model.ModelOverride> getOverrides() {
        if (this.overrides == null || this.overrides.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Model.ModelOverride> asOverrides = new ArrayList<Model.ModelOverride>(this.overrides.size());
        for (ItemModel.Overrides.OverriddenModel overriddenModel : this.overrides) {
            asOverrides.add(new Model.ModelOverride(overriddenModel));
        }
        return asOverrides;
    }

    public static ModelInfo createPlaceholder(String name) {
        ModelInfo m = new ModelInfo();
        m.name = name;
        m.placeholder = true;
        return m;
    }
}

