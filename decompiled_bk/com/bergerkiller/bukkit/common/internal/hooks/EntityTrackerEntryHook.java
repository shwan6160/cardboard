/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.controller.EntityNetworkController;

public interface EntityTrackerEntryHook {
    public EntityNetworkController<?> getController();

    public void setController(EntityNetworkController<?> var1);
}

