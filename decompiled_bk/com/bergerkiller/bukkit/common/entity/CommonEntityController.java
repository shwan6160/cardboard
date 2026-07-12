/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.controller.Tickable;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import org.bukkit.entity.Entity;

public class CommonEntityController<T extends CommonEntity<? extends Entity>>
implements Tickable {
    protected T entity;

    public T getEntity() {
        return this.entity;
    }

    public void onAttached() {
    }

    public void onDetached() {
    }

    @Override
    public void onTick() {
    }
}

