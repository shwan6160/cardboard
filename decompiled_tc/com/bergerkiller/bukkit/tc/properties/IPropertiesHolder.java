/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.tc.properties.IProperties;
import org.bukkit.World;

public interface IPropertiesHolder {
    public World getWorld();

    public IProperties getProperties();

    public void onPropertiesChanged();
}

